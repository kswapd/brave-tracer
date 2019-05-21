package com.dcits.services;

import interfaces.FooService;
import javax.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


//@Component
//@EnableRabbit
//@RabbitListener(queues = "kxwQueue", containerFactory = "rabbitListenerContainerFactory")
public class RabbitConsumerAnnotationToDubbo3 {

    @Resource
    FooService fs;
    //@RabbitListener(containerFactory = "simpleRabbitListenerContainerFactoryTracing", queues="queue-second")
    @RabbitListener(queues="queue-hello")
    public void listen(@Payload String foo) {
        System.out.println(foo+"3333333----------=======");
        fs.sayFoo("consumer...");
    }
}