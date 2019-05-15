package com.dcits.brave.tracers;



import brave.spring.rabbit.SpringRabbitTracing;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CheckRabbitTemplate implements Condition {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);
	private ApplicationContext context;
	@Override
	public boolean matches(ConditionContext condContext, AnnotatedTypeMetadata metadata) {
		logger.debug("Checking if rabbit template exist.");
		RabbitTemplate temp = null;
		try {
			temp = condContext.getBeanFactory().getBean(RabbitTemplate.class);
		}catch (
		NoSuchBeanDefinitionException e){
			logger.info("Rabbit template not found.");
		}
		if(temp == null){
			logger.debug("Rabbit template doesn't exist.");
			return true;

		}
		logger.debug("Rabbit template exists, decorate it by rabbit tracing.");
		SpringRabbitTracing tracing = context.getBean(SpringRabbitTracing.class);
		tracing.decorateRabbitTemplate(temp);
		return false;
	}

}
