package com.fiap.comparecimento.infrastructure.config.usecase.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    //Configuração do RabbitMQ
    public static final String QUEUE = "comparecimento.queue";
    public static final String EXCHANGE = "agendamento.exchange";
    public static final String ROUTING_KEY = "agendamento.key";

    @Bean
    public Queue comparecimentoQueue(){
        return QueueBuilder.durable(QUEUE).build();
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
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
