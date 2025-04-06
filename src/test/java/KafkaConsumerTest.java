

import app.Application;
import app.config.KafkaConsumerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
@Testcontainers
public class KafkaConsumerTest {

  @Container
  public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1")).withStartupTimeout(Duration.ofSeconds(60));

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
  }

  @Autowired
  private KafkaConsumerService kafkaConsumerService;



  @Test
  public void consumeMessage() {
    String validMessage = "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"eventTime\":\"2025-04-06T12:00:00Z\",\"eventType\":\"CREATE\",\"eventDetails\":\"Created user\"}";
    kafkaConsumerService.listenAuditMessages(validMessage);
  }

  @Test
  public void consumeInvalidMessage() {
    String invalidMessage = null;
    assertThrows(Exception.class, () -> {
      kafkaConsumerService.listenAuditMessages(invalidMessage);
    });
  }
}
