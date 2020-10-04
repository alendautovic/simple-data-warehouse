package com.github.alendautovic.datawarehouse.util;

import com.github.alendautovic.datawarehouse.web.rest.dto.request.MetricQuery;

public class QuerydslUtlis {

    public static String buildMetricQueryExpressionAlias(MetricQuery metricQuery) {
        return metricQuery.getMetric().name() + "_" + metricQuery.getAggregation().name();
    }

    private QuerydslUtlis() {
    }
}
