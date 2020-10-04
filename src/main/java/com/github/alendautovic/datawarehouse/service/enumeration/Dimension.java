package com.github.alendautovic.datawarehouse.service.enumeration;

public enum Dimension {

    DATE(DimensionType.TIME),
    CAMPAIGN(DimensionType.REGULAR),
    DATASOURCE(DimensionType.REGULAR);

    public final DimensionType type;

    Dimension(DimensionType type) {
        this.type = type;
    }
}
