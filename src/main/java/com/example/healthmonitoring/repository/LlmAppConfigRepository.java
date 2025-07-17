package com.example.healthmonitoring.repository;

import com.example.healthmonitoring.model.domain.LlmAppConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LlmAppConfigRepository extends JpaRepository<LlmAppConfigDO, Long> {
    Optional<LlmAppConfigDO> findByAppCode(String appCode);
}