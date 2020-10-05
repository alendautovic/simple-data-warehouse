package com.github.alendautovic.datawarehouse.web.rest;

import com.github.alendautovic.datawarehouse.service.DailyStatisticsService;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.DataWarehouseQuery;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DataWarehouseQueryController {

    private final DailyStatisticsService dailyStatisticsService;

    public DataWarehouseQueryController(
            DailyStatisticsService dailyStatisticsService) {
        this.dailyStatisticsService = dailyStatisticsService;
    }

    /**
     * API for querying Data Warehouse in a dynamic and flexible way
     *
     * @param queryRequest {@link DataWarehouseQuery} DTO representing query logic to be applied on the Data Warehouse.
     *                     Automatically mapped from request body of HTTP POST request, parsed as JSON string.
     * @return List of {@link DataWarehouseQueryResponse} objects. Automatically mapped to HTTP response body, serialized in JSON format.
     */
    @PostMapping("/query")
    public List<DataWarehouseQueryResponse> queryDataWarehouse(@Valid @RequestBody DataWarehouseQuery queryRequest) {
        return dailyStatisticsService.queryDataWarehouse(queryRequest);
    }

}
