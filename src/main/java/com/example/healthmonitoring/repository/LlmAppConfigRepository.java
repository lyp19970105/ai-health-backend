package com.example.healthmonitoring.repository;

import com.example.healthmonitoring.model.LlmAppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LlmAppConfigRepository extends JpaRepository<LlmAppConfig, Long> {
    Optional<LlmAppConfig> findByAppCode(String appCode);
}
