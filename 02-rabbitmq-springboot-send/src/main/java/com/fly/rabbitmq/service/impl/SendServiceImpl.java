package com.fly.rabbitmq.service.impl;

import com.fly.rabbitmq.service.SendService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendServiceImpl implements SendService {

    @Resource
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendMessage(String message) {
        /**
         * 发送消息
         * 参数 1 为交换机名
         * 参数 2 为routingKey
         * 参数 3 为具体消息
         */
        amqpTemplate.convertAndSend("bootExchange", "bootRoutingKey", message);
    }

    @Override
    public void sendFanoutMessage(String message) {
        amqpTemplate.convertAndSend("fanoutExchange", "", message);
    }

    @Override
    public void sendTopicMessage(String message) {
        amqpTemplate.convertAndSend("topicExchange", "aa.a.a", message);
    }
}
