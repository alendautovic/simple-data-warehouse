package com.github.alendautovic.datawarehouse.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "daily_statistics")
public class DailyStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "datasource", nullable = false)
    private String datasource;

    @NotNull
    @Column(name = "campaign", nullable = false)
    private String campaign;

    @NotNull
    @Column(name = "collected_date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "clicks", nullable = false)
    private Integer clicks;

    @NotNull
    @Column(name = "impressions", nullable = false)
    private Integer impressions;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DailyStatistics)) {
            return false;
        }
        return id != null && id.equals(((DailyStatistics) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public Integer getImpressions() {
        return impressions;
    }

    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }
}
