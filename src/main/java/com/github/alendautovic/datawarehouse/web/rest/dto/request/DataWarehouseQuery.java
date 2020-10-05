package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.github.alendautovic.datawarehouse.service.enumeration.Dimension;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class DataWarehouseQuery {

    @NotEmpty
    private Set<@Valid MetricQuery> metricQueries;
    private Set<Dimension> groupBy;
    private Set<@Valid DimensionFilter> filters;

    public Set<MetricQuery> getMetricQueries() {
        return metricQueries;
    }

    public void setMetricQueries(
            Set<MetricQuery> metricQueries) {
        this.metricQueries = metricQueries;
    }

    public Set<Dimension> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(
            Set<Dimension> groupBy) {
        this.groupBy = groupBy;
    }

    public Set<DimensionFilter> getFilters() {
        return filters;
    }

    public void setFilters(
            Set<DimensionFilter> filters) {
        this.filters = filters;
    }
}
