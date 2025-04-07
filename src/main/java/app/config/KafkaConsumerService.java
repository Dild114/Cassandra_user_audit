package app.config;


import app.entity.UserAudit;
import app.manager.UserAuditStatementManager;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

  private final UserAuditStatementManager userAuditStatementManager;
  private final ObjectMapper objectMapper;

  @Autowired
  public KafkaConsumerService(UserAuditStatementManager userAuditStatementManager, ObjectMapper objectMapper) {
    this.userAuditStatementManager = userAuditStatementManager;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "audit", groupId = "audit")
  public void listenAuditMessages(String userAuditJson) {
    log.info("Received Kafka message: {}", userAuditJson);
    if (userAuditJson == null) {
      throw new IllegalArgumentException("Invalid user audit data");
    }
    try {
      UserAudit userAudit = objectMapper.readValue(userAuditJson, UserAudit.class);
      insertUserAudit(userAudit);
      log.info("Received and saved audit message: {}", userAudit);
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize Kafka message", e);
    }
  }

  private void insertUserAudit(UserAudit userAudit) {
    try {
      BoundStatement boundStatement = userAuditStatementManager.getInsertStatement().bind(
          userAudit.getId(),
          userAudit.getEventTime(),
          userAudit.getEventType().name(),
          userAudit.getEventDetails()
      );
      userAuditStatementManager.getSession().execute(boundStatement);
    } catch (Exception e) {
      log.error("Error inserting audit message", e);
    }
  }
}
