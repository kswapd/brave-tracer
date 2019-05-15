package com.dcits.brave.tracers;



import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CheckRabbitConnectionFactory implements Condition {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);
	private ApplicationContext context;
	@Override
	public boolean matches(ConditionContext condContext, AnnotatedTypeMetadata metadata) {
		logger.debug("Checking if rabbit connection factory exist.");
		ConnectionFactory cf = null;
		try {
			cf = condContext.getBeanFactory().getBean(ConnectionFactory.class);
		}catch (NoSuchBeanDefinitionException e){
			logger.info("Connection factory not found.");
		}
		if(cf == null){
			logger.debug("Rabbit connection factory doesn't exist.");
			return true;

		}
		logger.debug("Rabbit connection factory exists.");
		return false;
	}

}
