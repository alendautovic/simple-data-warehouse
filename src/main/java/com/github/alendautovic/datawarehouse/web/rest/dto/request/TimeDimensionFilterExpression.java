package com.github.alendautovic.datawarehouse.web.rest.dto.request;


import io.swagger.annotations.ApiModel;

import java.time.LocalDate;

@ApiModel(description = "Subtype of FilterExpressions used for filtering time dimension")
public class TimeDimensionFilterExpression implements FilterExpression {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private LocalDate dateEquals;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public LocalDate getDateEquals() {
        return dateEquals;
    }

    public void setDateEquals(LocalDate dateEquals) {
        this.dateEquals = dateEquals;
    }
}
