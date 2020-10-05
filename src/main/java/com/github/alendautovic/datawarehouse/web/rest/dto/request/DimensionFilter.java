package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.github.alendautovic.datawarehouse.service.enumeration.Dimension;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class DimensionFilter {
    @NotNull
    private Dimension dimension;
    @Valid
    @NotNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionFilter that = (DimensionFilter) o;
        return dimension == that.dimension &&
                filterExpression.equals(that.filterExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension);
    }
}
