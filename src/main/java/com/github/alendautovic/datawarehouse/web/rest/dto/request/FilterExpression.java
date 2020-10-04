package com.github.alendautovic.datawarehouse.web.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

@JsonSubTypes({@JsonSubTypes.Type(value = RegularDimensionFilterExpression.class,
        name = "regular"), @JsonSubTypes.Type(value = TimeDimensionFilterExpression.class,
        name = "time")})
@ApiModel(subTypes = {RegularDimensionFilterExpression.class, TimeDimensionFilterExpression.class},
        description = "Supertype of all filter expressions (check RegularDimensionFilterExpression and TimeDimensionFilterExpression)")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "filterExpressionType", visible = true)
public interface FilterExpression {
}
