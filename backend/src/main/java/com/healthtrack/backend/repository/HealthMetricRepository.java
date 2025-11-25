package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByUserId(Long userId);
    List<HealthMetric> findByUserIdAndMetricMonth(Long userId, String metricMonth);
}
