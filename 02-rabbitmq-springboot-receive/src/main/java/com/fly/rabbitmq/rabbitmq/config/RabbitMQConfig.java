package com.fly.rabbitmq.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //配置一个交换器
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("bootExchange");
    }

    //配置一个队列
    @Bean
    public Queue directQueue() {
        return new Queue("bootQueue");
    }


    /**
     * 配置一个交换机和队列的绑定
     * @param directQueue 需要绑定的队列对象，参数名要和方法名一致，这样可以自动注入
     * @param directExchange 需要绑定的交换器
     * @return
     */
    @Bean
    public Binding directBinding(Queue directQueue, DirectExchange directExchange) {

        return BindingBuilder.bind(directQueue).to(directExchange).with("bootRoutingKey");
    }
}
