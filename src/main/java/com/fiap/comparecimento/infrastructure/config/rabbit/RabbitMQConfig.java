package com.fiap.comparecimento.infrastructure.config.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    //Configuração do RabbitMQ
    public static final String QUEUE = "comparecimento.queue";
    public static final String EXCHANGE = "agendamento.exchange";
    public static final String ROUTING_KEY = "agendamento.key";

    public static final String QUEUE_ROLLBACK = "agendamento.queue";
    public static final String ROUTING_KEY_ROLLBACK = "agendamento.rollback";

    @Bean
    public Queue comparecimentoQueue(){
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Queue agendamentoRollBackQueue(){
        return QueueBuilder.durable(QUEUE_ROLLBACK).build();
    }

    @Bean
    public TopicExchange agendamentoExchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(comparecimentoQueue()).to(agendamentoExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding rollBackBinding(){
        return BindingBuilder.bind(agendamentoRollBackQueue()).to(agendamentoExchange()).with(ROUTING_KEY_ROLLBACK);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
