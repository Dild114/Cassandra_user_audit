package app.entity;

import app.Event_type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@Table(value = "user_audit")
public class UserAudit {
  @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private UUID id;

  @Column(value = "event_time")
  private Instant event_time;

  @Column(value = "event_type")
  private Event_type event_type;

  @Column(value = "event_details")
  private String event_details;
}
