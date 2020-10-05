package com.github.alendautovic.datawarehouse.service;

import com.github.alendautovic.datawarehouse.service.enumeration.Dimension;
import com.github.alendautovic.datawarehouse.service.enumeration.DimensionType;
import com.github.alendautovic.datawarehouse.util.QueryDSLUtils;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.*;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Method for querying Data Warehouse in a dynamic and flexible way
     *
     * @param queryRequest {@link DataWarehouseQuery} DTO representing query logic to be applied on the Data Warehouse
     * @return List of {@link DataWarehouseQueryResponse} objects
     */
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

    /**
     * Method for building dimensions select expressions, based on the {@link DataWarehouseQuery} object. Optimized to not fetch data that is irrelevant.
     *
     * @param queryRequest {@link DataWarehouseQuery} DTO representing query logic to be applied on the Data Warehouse
     * @return List of QueryDSL {@link Expression} objects that are translating to select block columns of the generated SQL query
     */
    private List<Expression<?>> buildDimensionsSelectExpressions(DataWarehouseQuery queryRequest) {
        Set<DimensionFilter> filters = queryRequest.getFilters();
        Set<MetricQuery> metricQueries = queryRequest.getMetricQueries();

        List<Expression<?>> selectExpressions = new ArrayList<>();

        String datasourceAlias = dailyStatistics.datasource.getMetadata().getName();
        String campaignAlias = dailyStatistics.campaign.getMetadata().getName();
        String dateAlias = dailyStatistics.date.getMetadata().getName();

        if (queryRequest.getGroupBy() != null && !queryRequest.getGroupBy().isEmpty()) {
            if (queryRequest.getGroupBy().contains(Dimension.DATASOURCE)) {
                selectExpressions.add(dailyStatistics.datasource.as(datasourceAlias));
            }
            if (queryRequest.getGroupBy().contains(Dimension.CAMPAIGN)) {
                selectExpressions.add(dailyStatistics.campaign.as(campaignAlias));
            }
            if (queryRequest.getGroupBy().contains(Dimension.DATE)) {
                selectExpressions.add(dailyStatistics.date.as(dateAlias));
            }
            return selectExpressions;
        }

        boolean anyAggregation = metricQueries.stream().anyMatch(
                metricQuery -> metricQuery.getAggregation() != null);

        if (anyAggregation) {
            if (filters.stream().anyMatch(
                    dimensionFilter -> dimensionFilter.getDimension().equals(Dimension.DATASOURCE))) {
                selectExpressions.add(dailyStatistics.datasource.as(datasourceAlias));
            }
            if (filters.stream().anyMatch(
                    dimensionFilter -> dimensionFilter.getDimension().equals(Dimension.CAMPAIGN))) {
                selectExpressions.add(dailyStatistics.campaign.as(campaignAlias));
            }
            if (hasDateEqualsFilter(filters)) {
                selectExpressions.add(dailyStatistics.date.as(dateAlias));
            }
            return selectExpressions;
        }

        selectExpressions.add(dailyStatistics.datasource.as(datasourceAlias));
        selectExpressions.add(dailyStatistics.campaign.as(campaignAlias));
        selectExpressions.add(dailyStatistics.date.as(dateAlias));

        return selectExpressions;
    }

    /**
     * Method for building metrics query expressions, based on the Set of {@link MetricQuery} objects.
     *
     * @param metricQueries Set of {@link MetricQuery} objects representing metrics to be included in select expression along with aggregation to be applied (if any)
     * @return List of QueryDSL {@link NumberExpression} objects that are translating to select block expressions of the generated SQL query
     */
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
            if (metricQuery.getAggregation() != null) {
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
            }
            expression = expression.as(QueryDSLUtils.buildMetricQueryExpressionAlias(metricQuery));
            return expression;
        }).collect(Collectors.toList());
    }

    /**
     * Method for building filters clause, based on Set of {@link DimensionFilter} objects
     *
     * @param filters Set of {@link DimensionFilter} objects representing filtering logic to be applied on the Data Warehouse
     * @return QueryDSL {@link BooleanBuilder}  object that translates to logical expressions inside where clause of generated SQL query
     */
    private BooleanBuilder buildFiltersClause(Set<DimensionFilter> filters) {
        BooleanBuilder filtersClause = new BooleanBuilder();
        if (filters == null) {
            return filtersClause;
        }
        filters.stream().map(dimensionFilter -> {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if (dimensionFilter.getFilterExpression() instanceof RegularDimensionFilterExpression) {
                Predicate predicate = getRegularDimensionFilterPredicate(dimensionFilter);
                booleanBuilder.and(predicate);
                return booleanBuilder;
            }

            TimeDimensionFilterExpression filter = (TimeDimensionFilterExpression) dimensionFilter.getFilterExpression();
            if (dimensionFilter.getDimension().type != DimensionType.TIME) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Regular dimension can not be filtered with time dimension filter.");
            }

            if (filter.getDateEquals() != null) booleanBuilder.and(dailyStatistics.date.eq(filter.getDateEquals()));
            if (filter.getDateFrom() != null) booleanBuilder.and(dailyStatistics.date.goe(filter.getDateFrom()));
            if (filter.getDateTo() != null) booleanBuilder.and(dailyStatistics.date.loe(filter.getDateTo()));

            return booleanBuilder;
        }).forEach(filtersClause::and);
        return filtersClause;
    }

    /**
     * Method for building group by expressions, based on the Set of {@link Dimension}.
     *
     * @param groupBy Set of {@link Dimension}
     * @return List of QueryDSL {@link Expression} objects that are translating to group by columns of the generated SQL query
     */
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

    /**
     * Helper method to build QueryDSL {@link Predicate} object based on {@link DimensionFilter}
     *
     * @param dimensionFilter {@link DimensionFilter} object
     * @return QueryDSL {@link Predicate} object
     */
    private Predicate getRegularDimensionFilterPredicate(DimensionFilter dimensionFilter) {
        RegularDimensionFilterExpression filter = (RegularDimensionFilterExpression) dimensionFilter.getFilterExpression();
        Predicate predicate;
        switch (dimensionFilter.getDimension()) {
            case CAMPAIGN:
                predicate = dailyStatistics.campaign.eq(filter.getValueEquals());
                break;
            case DATASOURCE:
                predicate = dailyStatistics.datasource.eq(filter.getValueEquals());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Time dimension can not be filtered with regular dimension filter.");
        }
        return predicate;
    }

    /**
     * Helper method that checks if there is a filter matching exact date inside of Set of {@link DimensionFilter} objects
     *
     * @param filters Set of {@link DimensionFilter} objects
     * @return boolean indicating if there is a filter matching exact date inside of Set of {@link DimensionFilter} objects
     */
    private boolean hasDateEqualsFilter(Set<DimensionFilter> filters) {
        return filters.stream().anyMatch(
                dimensionFilter -> {
                    if (!dimensionFilter.getDimension().type.equals(DimensionType.TIME)) return false;

                    if (dimensionFilter.getFilterExpression() instanceof TimeDimensionFilterExpression) {
                        TimeDimensionFilterExpression timeDimensionFilter = (TimeDimensionFilterExpression) dimensionFilter.getFilterExpression();
                        return timeDimensionFilter.getDateEquals() != null;
                    }
                    return false;
                });
    }
}
