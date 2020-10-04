package com.github.alendautovic.datawarehouse.web.rest.dto.response;

import com.github.alendautovic.datawarehouse.service.enumeration.Aggregation;
import com.github.alendautovic.datawarehouse.service.enumeration.Metric;

public class MetricResult {
    private Metric metric;
    private Aggregation aggregation;
    private Double value;

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
