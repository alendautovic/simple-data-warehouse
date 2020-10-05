package com.github.alendautovic.datawarehouse.service;

import com.github.alendautovic.datawarehouse.service.enumeration.Aggregation;
import com.github.alendautovic.datawarehouse.service.enumeration.Metric;
import com.github.alendautovic.datawarehouse.util.QueryDSLUtils;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.DataWarehouseQuery;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.MetricResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.alendautovic.datawarehouse.domain.QDailyStatistics.dailyStatistics;

@Service
public class DataWarehouseQueryResponseMapper {

    /**
     * Method that converts Data Warehouse query result (List of QueryDSL {@link Tuple} objects) to List of {@link DataWarehouseQueryResponse} objects, based on the query DTO {@link DataWarehouseQuery}
     *
     * @param queryRequest {@link DataWarehouseQuery}
     * @param queryResult  List of QueryDSL {@link Tuple} objects
     * @return List of {@link DataWarehouseQueryResponse} objects
     */
    public List<DataWarehouseQueryResponse> tupleToDataWarehouseQueryResponse(DataWarehouseQuery queryRequest,
                                                                              List<Tuple> queryResult) {
        String datasourceAlias = dailyStatistics.datasource.getMetadata().getName();
        String campaignAlias = dailyStatistics.campaign.getMetadata().getName();
        String dateAlias = dailyStatistics.date.getMetadata().getName();

        return queryResult.stream().map(tuple -> {
            DataWarehouseQueryResponse responseItem = new DataWarehouseQueryResponse();

            responseItem.setDatasource(tuple.get(Expressions.stringPath(datasourceAlias)));
            responseItem.setCampaign(tuple.get(Expressions.stringPath(campaignAlias)));
            responseItem.setDate(tuple.get(Expressions.datePath(LocalDate.class, dateAlias)));

            List<MetricResult> metricResults = queryRequest.getMetricQueries().stream().map(metricQuery -> {
                MetricResult metricResult = new MetricResult();
                metricResult.setAggregation(metricQuery.getAggregation());
                metricResult.setMetric(metricQuery.getMetric());
                if (metricQuery.getAggregation() == Aggregation.AVERAGE || metricQuery.getMetric() == Metric.CTR) {
                    metricResult.setValue(
                            tuple.get(Expressions.numberPath(Double.class,
                                    QueryDSLUtils.buildMetricQueryExpressionAlias(metricQuery))));
                } else {
                    Integer value = tuple.get(Expressions.numberPath(Integer.class,
                            QueryDSLUtils.buildMetricQueryExpressionAlias(metricQuery)));
                    metricResult.setValue(value != null ? Double.valueOf(value) : null);
                }
                return metricResult;
            }).collect(Collectors.toList());
            responseItem.setMetricResults(metricResults);
            return responseItem;
        }).collect(Collectors.toList());
    }
}
