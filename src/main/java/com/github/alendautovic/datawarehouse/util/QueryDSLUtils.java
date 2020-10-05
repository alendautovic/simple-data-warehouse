package com.github.alendautovic.datawarehouse.util;

import com.github.alendautovic.datawarehouse.web.rest.dto.request.MetricQuery;

/**
 * QueryDSL util class with helper methods
 */
public class QueryDSLUtils {

    /**
     * Method for building metric query expression alias to be used in generated database query
     *
     * @param metricQuery {@link MetricQuery} object
     * @return String alias
     */
    public static String buildMetricQueryExpressionAlias(MetricQuery metricQuery) {
        return metricQuery.getMetric().name() + "_" + (metricQuery.getAggregation() != null ? metricQuery.getAggregation().name() : "");
    }

    private QueryDSLUtils() {
    }
}
