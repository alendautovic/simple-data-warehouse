package com.github.alendautovic.datawarehouse.service;

import com.github.alendautovic.datawarehouse.service.enumeration.Dimension;
import com.github.alendautovic.datawarehouse.util.QuerydslUtlis;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.*;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.alendautovic.datawarehouse.domain.QDailyStatistics.dailyStatistics;

@Service
@Transactional
public class DailyStatisticsService {

    @PersistenceContext
    private EntityManager entityManager;
    private final DataWarehouseQueryResponseMapper dataWarehouseQueryResponseMapper;

    public DailyStatisticsService(
            DataWarehouseQueryResponseMapper dataWarehouseQueryResponseMapper) {
        this.dataWarehouseQueryResponseMapper = dataWarehouseQueryResponseMapper;
    }

    public List<DataWarehouseQueryResponse> queryDataWarehouse(DataWarehouseQuery queryRequest) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Expression<?>> selectExpressions = buildDimensionsSelectExpressions(queryRequest);

        List<NumberExpression<?>> metricsQueryExpressions = buildMetricsQueryExpressions(
                queryRequest.getMetricQueries());
        selectExpressions.addAll(metricsQueryExpressions);

        BooleanBuilder filtersClause = buildFiltersClause(queryRequest.getFilters());

        List<? extends Expression<?>> groupByExpressions = buildGroupByExpressions(queryRequest.getGroupBy());

        List<Tuple> queryResult = queryFactory.from(dailyStatistics).select(
                selectExpressions.toArray(new Expression<?>[0])).groupBy(
                groupByExpressions.toArray(new Expression<?>[0])).where(filtersClause).fetch();

        return dataWarehouseQueryResponseMapper.tupleToDataWarehouseQueryResponse(queryRequest, queryResult);
    }

    private List<Expression<?>> buildDimensionsSelectExpressions(DataWarehouseQuery queryRequest) {
        Set<DimensionFilter> filters = queryRequest.getFilters();
        Set<MetricQuery> metricQueries = queryRequest.getMetricQueries();
        List<Expression<?>> selectExpressions = new ArrayList<>();
        if (queryRequest.getGroupBy() != null && !queryRequest.getGroupBy().isEmpty()) {
            if (queryRequest.getGroupBy().contains(Dimension.DATASOURCE)) {
                selectExpressions.add(dailyStatistics.datasource.as("datasource"));
            }
            if (queryRequest.getGroupBy().contains(Dimension.CAMPAIGN)) {
                selectExpressions.add(dailyStatistics.campaign.as("campaign"));
            }
            if (queryRequest.getGroupBy().contains(Dimension.DATE)) {
                selectExpressions.add(dailyStatistics.date.as("date"));
            }
        } else {
            boolean anyAggregation = metricQueries.stream().anyMatch(
                    metricQuery -> metricQuery.getAggregation() != null);
            if (anyAggregation) {
                if (filters.stream().anyMatch(
                        dimensionFilter -> dimensionFilter.getDimension().equals(Dimension.DATASOURCE))) {
                    selectExpressions.add(dailyStatistics.datasource.as("datasource"));
                }
                if (filters.stream().anyMatch(
                        dimensionFilter -> dimensionFilter.getDimension().equals(Dimension.CAMPAIGN))) {
                    selectExpressions.add(dailyStatistics.campaign.as("campaign"));
                }
                if (hasDateEqualsFilter(filters)) {
                    selectExpressions.add(dailyStatistics.date.as("date"));
                }
            }
        }
        return selectExpressions;
    }

    private boolean hasDateEqualsFilter(Set<DimensionFilter> filters) {
        return filters.stream().anyMatch(
                dimensionFilter -> {
                    if (!dimensionFilter.getDimension().equals(Dimension.DATE)) return false;

                    if (dimensionFilter.getFilterExpression() instanceof TimeDimensionFilterExpression) {
                        TimeDimensionFilterExpression timeDimensionFilter = (TimeDimensionFilterExpression) dimensionFilter.getFilterExpression();
                        return timeDimensionFilter.getDateEquals() != null;
                    }
                    return false;
                });
    }

    private List<NumberExpression<?>> buildMetricsQueryExpressions(Set<MetricQuery> metricQueries) {
        return metricQueries.stream().map(metricQuery -> {
            NumberExpression<?> expression = null;
            switch (metricQuery.getMetric()) {
                case CLICKS:
                    expression = dailyStatistics.clicks;
                    break;
                case IMPRESSIONS:
                    expression = dailyStatistics.impressions;
                    break;

                case CTR:
                    expression = dailyStatistics.clicks.divide(dailyStatistics.impressions);
                    break;
            }
            switch (metricQuery.getAggregation()) {
                case SUM:
                    expression = expression.sum();
                    break;
                case AVERAGE:
                    expression = expression.avg();
                    break;
                case MAX:
                    expression = expression.max();
                    break;
                case MIN:
                    expression = expression.min();
                    break;
            }
            expression = expression.as(QuerydslUtlis.buildMetricQueryExpressionAlias(metricQuery));
            return expression;
        }).collect(Collectors.toList());
    }


    private BooleanBuilder buildFiltersClause(Set<DimensionFilter> filters) {
        BooleanBuilder filtersClause = new BooleanBuilder();
        if (filters == null) {
            return filtersClause;
        }
        filters.stream().map(dimensionFilter -> {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if (dimensionFilter.getFilterExpression() instanceof RegularDimensionFilterExpression) {
                RegularDimensionFilterExpression filter = (RegularDimensionFilterExpression) dimensionFilter.getFilterExpression();
                switch (dimensionFilter.getDimension()) {
                    case CAMPAIGN:
                        booleanBuilder.and(dailyStatistics.campaign.eq(filter.getValueEquals()));
                        break;
                    case DATASOURCE:
                        booleanBuilder.and(dailyStatistics.datasource.eq(filter.getValueEquals()));
                        break;
                    default:
                        throw new RuntimeException("Time dimension can not be filtered with regular dimension filter.");
                }
            } else {
                TimeDimensionFilterExpression filter = (TimeDimensionFilterExpression) dimensionFilter.getFilterExpression();
                if (dimensionFilter.getDimension() != Dimension.DATE) {
                    throw new RuntimeException("Regular dimension can not be filtered with time dimension filter.");
                }
                if (filter.getDateEquals() != null) booleanBuilder.and(dailyStatistics.date.eq(filter.getDateEquals()));
                if (filter.getDateFrom() != null) booleanBuilder.and(dailyStatistics.date.goe(filter.getDateFrom()));
                if (filter.getDateTo() != null) booleanBuilder.and(dailyStatistics.date.loe(filter.getDateTo()));
            }

            return booleanBuilder;
        }).forEach(filtersClause::and);
        return filtersClause;
    }

    private List<Expression<?>> buildGroupByExpressions(Set<Dimension> groupBy) {
        if (groupBy == null) {
            return new ArrayList<>();
        }
        return groupBy.stream().map(dimension -> {
            Expression<?> expression = null;
            switch (dimension) {
                case CAMPAIGN:
                    expression = dailyStatistics.campaign;
                    break;
                case DATASOURCE:
                    expression = dailyStatistics.datasource;
                    break;
                case DATE:
                    expression = dailyStatistics.date;
            }
            return expression;
        }).collect(Collectors.toList());
    }
}
