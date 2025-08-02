package com.kalby.healthmonitoring.repository;

import com.kalby.healthmonitoring.model.domain.ConversationDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationDORepository extends JpaRepository<ConversationDO, Long> {
    Page<ConversationDO> findByAppCodeAndUserId(String appCode, Long userId, Pageable pageable);
}
