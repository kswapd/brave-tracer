package services;

import brave.spring.rabbit.SpringRabbitTracing;
import com.rabbitmq.client.Consumer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RabbitProducer {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/brave-context.xml","rabbit.xml"});

        //AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("rabbit.xml");
        //AmqpTemplate template = (AmqpTemplate)ctx.getBean("amqpTemplate");
        AmqpTemplate template = (AmqpTemplate)ctx.getBean("rabbitTemplateTracing");

      //  SimpleRabbitListenerContainerFactory  listenerContainerFactory = (SimpleRabbitListenerContainerFactory )ctx.getBean("simpleRabbitListenerContainerFactoryTracing");

      //  SimpleMessageListenerContainer listenerContainer = listenerContainerFactory.createContainerInstance();
//(SimpleMessageListenerContainer)ctx.getBean("simpleRabbitListenerContainerFactoryTracing");

       // Consumer consumer = (Consumer)ctx.getBean(Consumer.class);
        //listenerContainer.

       // listenerContainer.setQueueNames("myQueue");
        //listenerContainer.setMessageListener(consumer);
        //SimpleMessageListenerContainer c;
       // c.setM
        //container.setMessageListener(messageListener());



        /*ConnectionFactory connectionFactory = (ConnectionFactory)ctx.getBean(ConnectionFactory.class);

        SpringRabbitTracing rabbitTracing = (SpringRabbitTracing) ctx.getBean(SpringRabbitTracing.class);


        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.setQueueNames("myQueue");

        RabbitConsumer2 c2 = new RabbitConsumer2();
        MessageListenerAdapter listener = new MessageListenerAdapter(c2);
        listener.setDefaultListenerMethod("listen");
        container.setMessageListener(listener);
*/
        //SpringRabbitTracing


        template.convertAndSend("Hello, world!");
        Thread.sleep(1000);
        ctx.destroy();
    }
}