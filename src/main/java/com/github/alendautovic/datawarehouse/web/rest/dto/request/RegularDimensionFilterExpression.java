package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@ApiModel(description = "Subtype of Filter used for filtering regular dimensions (datasource and campaign)")
public class RegularDimensionFilterExpression implements FilterExpression {
    @NotNull
    private String valueEquals;

    public String getValueEquals() {
        return valueEquals;
    }

    public void setValueEquals(String valueEquals) {
        this.valueEquals = valueEquals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularDimensionFilterExpression that = (RegularDimensionFilterExpression) o;
        return valueEquals.equals(that.valueEquals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueEquals);
    }
}
