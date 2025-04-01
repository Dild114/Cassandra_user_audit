package app.config;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
//
//  private ObjectMapper objectMapper;
//
//  public KafkaConsumerService(ObjectMapper objectMapper) {
//    this.objectMapper = objectMapper;
//  }

  @KafkaListener(topics = {"${success}"})
  public void consumeMessage(String message) {
//    String parsedMessage = objectMapper.readValue(message, String.class);
    log.info("Retrieved message {}", message);
  }
}
