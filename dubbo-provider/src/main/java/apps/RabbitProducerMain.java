package apps;

import java.io.IOException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;


@Configuration
public class RabbitProducerMain {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/brave-context.xml","rabbit.xml"});

        //AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("rabbit.xml");
        //AmqpTemplate template = (AmqpTemplate)ctx.getBean("amqpTemplate");
        RabbitTemplate template = (RabbitTemplate)ctx.getBean(RabbitTemplate.class);
        ResourcePropertySource ps = null; // handle exception
        try {
            ps = new ResourcePropertySource(new ClassPathResource("commons.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ctx.getEnvironment().getPropertySources().addFirst(ps);
       // SpringRabbitTracing rabbitTracing = (SpringRabbitTracing) ctx.getBean(SpringRabbitTracing.class);
        //rabbitTracing.decorateRabbitTemplate((RabbitTemplate)template);
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
        //SimpleRabbitListenerContainerFactory listenerFactory = (SimpleRabbitListenerContainerFactory)ctx.getBean(SimpleRabbitListenerContainerFactory.class);
        //rabbitTracing.decorateSimpleRabbitListenerContainerFactory(listenerFactory);


        /*System.out.println("Received: " + template.receiveAndConvert());

        System.out.println("222");
        Thread.sleep(3000);
        System.out.println("333");*/




       /* SimpleRabbitListenerContainerFactory listenerFactory = (SimpleRabbitListenerContainerFactory)ctx.getBean(SimpleRabbitListenerContainerFactory.class);
        SimpleMessageListenerContainer container = listenerFactory.createListenerContainer();

        //SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        ConnectionFactory connectionFactory = (ConnectionFactory)ctx.getBean(ConnectionFactory.class);
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("kxwQueue");
       // Queue queue = ctx.getBean(Queue.class);
       // container.setQueues(queue);
        RabbitConsumer2 c2 = new RabbitConsumer2();

        MessageListenerAdapter listener = new MessageListenerAdapter(c2,"listen");
        //listener.setDefaultListenerMethod("listen");
        container.setMessageListener(listener);
        container.start();*/




        Thread.sleep(1000);
        String exchangeName = ctx.getEnvironment().getProperty("zipkin.rabbit.service.exchange");
        String routingKey = ctx.getEnvironment().getProperty("zipkin.rabbit.service.routingkey");

        //template.setRoutingKey(routingKey);
        //template.setExchange(exchangeName);
        //template.convertAndSend("Hello!");
        //template.convertAndSend(exchangeName,routingKey,"Hello, rabbit!");
        template.convertAndSend(exchangeName,"second","Hello,second!");
        template.convertAndSend(exchangeName,"hello","Hello,hello!");

        template.convertAndSend("fanout-exchange","","Hello,fanout!");
        //template.convertAndSend("Hello, world!");
        Thread.sleep(1000);
        System.out.println("producer finished");
        ctx.destroy();


    }

}