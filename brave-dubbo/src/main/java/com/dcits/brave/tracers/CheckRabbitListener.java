package com.dcits.brave.tracers;



import brave.spring.rabbit.SpringRabbitTracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CheckRabbitListener implements Condition {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);
	private ApplicationContext context;
	@Override
	public boolean matches(ConditionContext condContext, AnnotatedTypeMetadata metadata) {
		logger.debug("Checking if listener factory factory exist.");
		SimpleRabbitListenerContainerFactory fact = null;
		try {
			fact = condContext.getBeanFactory().getBean(SimpleRabbitListenerContainerFactory.class);
		}catch (NoSuchBeanDefinitionException e){
			logger.info("Rabbit listener factory not found.");
		}
		if(fact == null){
			logger.debug("Rabbit listener factory doesn't exist.");
			return true;

		}
		logger.debug("Rabbit listener factory exists, decorate it by rabbit tracing.");
		SpringRabbitTracing tracing = context.getBean(SpringRabbitTracing.class);
		tracing.decorateSimpleRabbitListenerContainerFactory(fact);
		return false;
	}

}
