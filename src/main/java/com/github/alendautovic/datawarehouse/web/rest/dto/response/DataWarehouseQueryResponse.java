package com.github.alendautovic.datawarehouse.web.rest.dto.response;

import java.time.LocalDate;
import java.util.List;

public class DataWarehouseQueryResponse {
    private String datasource;
    private String campaign;
    private LocalDate date;
    private List<MetricResult> metricResults;

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

    public List<MetricResult> getMetricResults() {
        return metricResults;
    }

    public void setMetricResults(List<MetricResult> metricResults) {
        this.metricResults = metricResults;
    }
}
