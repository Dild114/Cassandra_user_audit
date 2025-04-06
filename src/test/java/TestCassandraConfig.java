import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.InetSocketAddress;

@TestConfiguration
public class TestCassandraConfig {

  @Bean
  public CqlSession cqlSession() {
    CassandraContainer<?> cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:3.11.10"))
        .withExposedPorts(9042);

    cassandraContainer.start();

    InetSocketAddress contactPoint = new InetSocketAddress(cassandraContainer.getHost(), cassandraContainer.getMappedPort(9042));

    return CqlSession.builder()
        .addContactPoint(contactPoint)
        .withKeyspace("my_keyspace")
        .build();
  }
}
