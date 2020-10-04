package com.github.alendautovic.datawarehouse.repository;

import com.github.alendautovic.datawarehouse.domain.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, Long> {
}
