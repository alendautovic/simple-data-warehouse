package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.github.alendautovic.datawarehouse.service.enumeration.Dimension;

public class DimensionFilter {
    private Dimension dimension;
    private FilterExpression filterExpression;

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public FilterExpression getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(FilterExpression filterExpression) {
        this.filterExpression = filterExpression;
    }
}
