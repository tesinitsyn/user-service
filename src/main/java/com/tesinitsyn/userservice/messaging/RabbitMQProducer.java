package com.tesinitsyn.userservice.messaging;

import com.tesinitsyn.userservice.config.rabbit.RabbitMQConfig;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class RabbitMQProducer {

    private AmqpTemplate amqpTemplate;

    public void sendMessage(String email) {
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, email);
        log.info("Sent: {}", email);
    }
}
