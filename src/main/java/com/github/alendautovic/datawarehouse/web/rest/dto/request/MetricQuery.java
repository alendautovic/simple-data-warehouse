package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.github.alendautovic.datawarehouse.service.enumeration.Aggregation;
import com.github.alendautovic.datawarehouse.service.enumeration.Metric;

public class MetricQuery {
    private Metric metric;
    private Aggregation aggregation;

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public void setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
    }
}
