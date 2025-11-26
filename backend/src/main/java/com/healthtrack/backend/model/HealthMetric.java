package com.healthtrack.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "health_metrics")
public class HealthMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "metric_month")
    private String metricMonth;

    @Column(name = "average_weight")
    private Double averageWeight;

    @Column(name = "average_bp")
    private String averageBp;

    @Column(name = "total_steps")
    private Integer totalSteps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMetricMonth() {
        return metricMonth;
    }

    public void setMetricMonth(String metricMonth) {
        this.metricMonth = metricMonth;
    }

    public Double getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(Double averageWeight) {
        this.averageWeight = averageWeight;
    }

    public String getAverageBp() {
        return averageBp;
    }

    public void setAverageBp(String averageBp) {
        this.averageBp = averageBp;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }
}
