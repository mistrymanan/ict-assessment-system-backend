package com.cdad.project.plagiarismservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configurable
public class RabbitMQConfig {

    public static final String QUEUE="plagiarism_check_queue";
    public static final String QUEUE_DIRECT_EXCHANGE="plagiarism_check_exchange";
    public static final String ROUTING_KEY="plagiarism_check_key";


    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(QUEUE_DIRECT_EXCHANGE);
    }
    @Bean
    public Binding binding(Queue queue,DirectExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
    @Bean
    public MessageConverter messageConverter() {
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
         RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return  template;
    }

}
