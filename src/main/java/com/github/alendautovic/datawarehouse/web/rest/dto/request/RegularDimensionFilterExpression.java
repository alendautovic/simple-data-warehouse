package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Subtype of Filter used for filtering regular dimensions (datasource and campaign)")
public class RegularDimensionFilterExpression implements FilterExpression {
    private String valueEquals;

    public String getValueEquals() {
        return valueEquals;
    }

    public void setValueEquals(String valueEquals) {
        this.valueEquals = valueEquals;
    }
}
