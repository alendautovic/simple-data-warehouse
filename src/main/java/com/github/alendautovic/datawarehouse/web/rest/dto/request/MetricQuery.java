package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.github.alendautovic.datawarehouse.service.enumeration.Aggregation;
import com.github.alendautovic.datawarehouse.service.enumeration.Metric;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class MetricQuery {
    @NotNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricQuery that = (MetricQuery) o;
        return metric == that.metric &&
                aggregation == that.aggregation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(metric);
    }
}
