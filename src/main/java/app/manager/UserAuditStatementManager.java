package app.manager;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UserAuditStatementManager {
  private final PreparedStatement insertStatement;
  private final PreparedStatement selectStatement;

  public UserAuditStatementManager(CqlSession session) {
    this.insertStatement = session.prepare(
        "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type, event_details) " +
            "VALUES (?, ?, ?, ?)"
    );
    this.selectStatement = session.prepare(
        "SELECT * FROM my_keyspace.user_audit WHERE user_id = ?"
    );
  }
}
