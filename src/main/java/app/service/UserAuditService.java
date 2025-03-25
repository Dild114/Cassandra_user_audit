package app.service;

import app.entity.UserAudit;
import app.manager.UserAuditStatementManager;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datastax.oss.driver.api.core.cql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAuditService {

  @Autowired
  private CqlSession session;

  @Autowired
  private UserAuditStatementManager userAuditStatementManager;

  public void saveAudit(UserAudit userAudit) {
    BoundStatement boundStatement = userAuditStatementManager.getInsertStatement().bind(
        userAudit.getId(),
        userAudit.getEvent_time(),
        userAudit.getEvent_type().name(),
        userAudit.getEvent_details()
    );
    session.execute(boundStatement);
  }

  public List<Row> findById(UUID id) {
    BoundStatement boundStatement = userAuditStatementManager.getSelectStatement().bind(id);
    ResultSet resultSet = session.execute(boundStatement);
    List<Row> events = new ArrayList<>();
    for (Row row : resultSet) {
      events.add(row);
    }
    return events;
  }
}
