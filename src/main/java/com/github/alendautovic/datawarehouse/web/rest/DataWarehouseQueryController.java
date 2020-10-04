package com.github.alendautovic.datawarehouse.web.rest;

import com.github.alendautovic.datawarehouse.service.DailyStatisticsService;
import com.github.alendautovic.datawarehouse.web.rest.dto.request.DataWarehouseQuery;
import com.github.alendautovic.datawarehouse.web.rest.dto.response.DataWarehouseQueryResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DataWarehouseQueryController {

    private final DailyStatisticsService dailyStatisticsService;

    public DataWarehouseQueryController(
            DailyStatisticsService dailyStatisticsService) {
        this.dailyStatisticsService = dailyStatisticsService;
    }

    @PostMapping("/query")
    public List<DataWarehouseQueryResponse> queryDataWarehouse(@RequestBody DataWarehouseQuery queryRequest) {
        return dailyStatisticsService.queryDataWarehouse(queryRequest);
    }

}
