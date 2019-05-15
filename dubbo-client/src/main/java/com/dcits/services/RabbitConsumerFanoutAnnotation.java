package com.dcits.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
//@EnableRabbit
//@RabbitListener(queues = "kxwQueue", containerFactory = "rabbitListenerContainerFactory")
public class RabbitConsumerFanoutAnnotation {
    //@RabbitListener(containerFactory = "simpleRabbitListenerContainerFactoryTracing", queues="queue-fanout1")
    @RabbitListener(queues="queue-fanout1")
    public void listen(@Payload String foo) {
        System.out.println("fanout listener 1 msg:"+foo);
    }


   /* @RabbitListener(containerFactory = "simpleRabbitListenerContainerFactoryTracing", queues="queue-fanout2")
    public void listen2(@Payload String foo) {
        System.out.println("fanout listener 2 msg:"+foo);
    }*/
}