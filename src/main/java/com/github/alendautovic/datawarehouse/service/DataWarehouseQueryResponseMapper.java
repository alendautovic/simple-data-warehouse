package com.github.alendautovic.datawarehouse.service;

import com.github.alendautovic.datawarehouse.service.enumeration.Aggregation;
import com.github.alendautovic.datawarehouse.service.enumeration.Metric;
import com.github.alendautovic.datawarehouse.util.QuerydslUtlis;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.DataWarehouseQuery;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.MetricResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DataWarehouseQueryResponseMapper {

    public List<DataWarehouseQueryResponse> tupleToDataWarehouseQueryResponse(DataWarehouseQuery queryRequest,
                                                                              List<Tuple> queryResult) {
        return queryResult.stream().map(tuple -> {
            DataWarehouseQueryResponse responseItem = new DataWarehouseQueryResponse();

            responseItem.setDatasource(tuple.get(Expressions.stringPath("datasource")));
            responseItem.setCampaign(tuple.get(Expressions.stringPath("campaign")));
            responseItem.setDate(tuple.get(Expressions.datePath(LocalDate.class, "date")));

            Set<MetricResult> metricResults = queryRequest.getMetricQueries().stream().map(metricQuery -> {
                MetricResult metricResult = new MetricResult();
                metricResult.setAggregation(metricQuery.getAggregation());
                metricResult.setMetric(metricQuery.getMetric());
                if (metricQuery.getAggregation() == Aggregation.AVERAGE || metricQuery.getMetric() == Metric.CTR) {
                    metricResult.setValue(
                            tuple.get(Expressions.numberPath(Double.class,
                                    QuerydslUtlis.buildMetricQueryExpressionAlias(metricQuery))));
                } else {
                    Integer value = tuple.get(Expressions.numberPath(Integer.class,
                            QuerydslUtlis.buildMetricQueryExpressionAlias(metricQuery)));
                    metricResult.setValue(value != null ? Double.valueOf(value) : null);
                }
                return metricResult;
            }).collect(Collectors.toSet());
            responseItem.setMetricResults(metricResults);
            return responseItem;
        }).collect(Collectors.toList());
    }
}
