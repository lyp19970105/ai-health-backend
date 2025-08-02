package com.kalby.healthmonitoring.repository;

import com.kalby.healthmonitoring.model.domain.MessageDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageDORepository extends JpaRepository<MessageDO, Long> {
    List<MessageDO> findByConversationId(Long conversationId);
}