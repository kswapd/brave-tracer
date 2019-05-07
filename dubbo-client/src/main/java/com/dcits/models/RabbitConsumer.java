package com.dcits.models;

import org.springframework.messaging.handler.annotation.Payload;


//@Component
//@EnableRabbit
//@RabbitListener(queues = "kxwQueue", containerFactory = "rabbitListenerContainerFactory")
public class RabbitConsumer {
    //@RabbitHandler
    public void listen(@Payload String foo) {
        System.out.println(foo+"=========");
    }
}