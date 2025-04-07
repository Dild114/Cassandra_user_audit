

import app.Application;
import app.config.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Testcontainers
@SpringBootTest(classes = Application.class)
public class KafkaConsumerTest {

  @Container
  public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
  }
  @Container
  private static final CassandraContainer<?> cassandraContainer =
      new CassandraContainer<>("cassandra:4.1.3")
          .withExposedPorts(9042)
          .withReuse(true)
          .waitingFor(Wait.forLogMessage(".*Created default superuser role 'cassandra'.*", 1));

  @DynamicPropertySource
  static void cassandraProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cassandra.contact-points",
        () -> cassandraContainer.getHost() + ":" + cassandraContainer.getMappedPort(9042));
    registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
    registry.add("spring.cassandra.keyspace-name", () -> "my_keyspace");
  }

  @BeforeAll
  static void checkContainer() {
    if (!cassandraContainer.isRunning()) {
      cassandraContainer.start();
    }
    log.info("Cassandra port: " + cassandraContainer.getMappedPort(9042));
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
