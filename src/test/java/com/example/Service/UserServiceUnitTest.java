package com.example.Service;

import app.Application;
import app.Event_type;
import app.entity.UserAudit;
import app.service.UserAuditService;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest(classes = Application.class)
@Testcontainers
public class UserServiceUnitTest {

  @Autowired
  public UserAuditService userAuditService;

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
    System.out.println("Cassandra port: " + cassandraContainer.getMappedPort(9042));
  }


  @Test
  public void testSaveUser() {
    UserAudit user = new UserAudit(UUID.randomUUID(), Instant.now(), Event_type.CREATE, "test");
    userAuditService.saveAudit(user);

    List<Row> userAudit = userAuditService.findById(user.getId());
    Assertions.assertNotNull(user.getId());
    Row row = userAudit.get(0);
    assertEquals(userAudit.size(), 1);
    System.out.println(row.getColumnDefinitions());
    System.out.println(row.getColumnDefinitions());
    if (row.getColumnDefinitions().contains("event_details")) {
      assertEquals(row.getString("event_details"), "test");
    } else {
      throw new NotFoundException("User not found");
    }
    if (row.getColumnDefinitions().contains("user_id")) {
      assertEquals(row.getUuid("user_id"), user.getId());
    } else {
      throw new NotFoundException("User not found");
    }

  }

  @Test
  public void testGetUserById() {
    UserAudit invalidUser = null;

    assertThrows(RuntimeException.class, () -> {
      userAuditService.saveAudit(invalidUser);
    });
  }
}