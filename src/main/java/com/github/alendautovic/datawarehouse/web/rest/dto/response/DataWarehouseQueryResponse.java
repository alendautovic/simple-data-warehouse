package com.github.alendautovic.datawarehouse.web.rest.dto.response;

import java.time.LocalDate;
import java.util.Set;

public class DataWarehouseQueryResponse {
    private String datasource;
    private String campaign;
    private LocalDate date;
    private Set<MetricResult> metricResults;

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<MetricResult> getMetricResults() {
        return metricResults;
    }

    public void setMetricResults(Set<MetricResult> metricResults) {
        this.metricResults = metricResults;
    }
}
