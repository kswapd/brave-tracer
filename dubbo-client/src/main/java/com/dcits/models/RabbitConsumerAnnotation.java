package com.dcits.models;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@EnableRabbit
//@RabbitListener(queues = "kxwQueue", containerFactory = "rabbitListenerContainerFactory")
public class RabbitConsumerAnnotation {
    @RabbitListener(containerFactory = "simpleRabbitListenerContainerFactoryTracing", queues="queueTracing")
    public void listen(@Payload String foo) {
        System.out.println(foo+"----------=======");
    }
}