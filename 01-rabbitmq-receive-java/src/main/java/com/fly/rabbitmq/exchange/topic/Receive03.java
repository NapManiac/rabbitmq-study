package com.fly.rabbitmq.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive03 {
    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //配置mq的连接相关信息
        factory.setHost("192.168.119.128");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root");

        Connection connection = null; //定义连接
        Channel channel = null; //定义通道

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            /**
             * 声明一个队列
             * 参数 1 为队列名，取任意值
             * 参数 2 为是否持久化的队列
             * 参数 3 为是否排外，如果排外则这个队列只允许一个消费者监听
             * 参数 4 是否自动删除队列，如果为true则表示当队列中没有消息，也没有消费者链接时就会自动删除这个队列
             * 参数 5 为队列的一些属性设置，通常为null即可
             * 注意：
             *      1、声明队列时，这个队列名称如果已存在，则放弃声明，不存在则创建
             *      2、队列名可以取任易。但是要与消息接收时完全一致
             */
            channel.queueDeclare("topicQueue03", true, false, false, null);

            /**
             * 声明一个交换机
             * 参数 1 为交换机的名称
             * 参数 2 为交换机的类型 direct fanout topic headers
             * 参数 3 是否为持久化交换机
             * 注意：
             *      1、声明交换机时，如果这个交换机已存在，则会放弃声明
             */
            channel.exchangeDeclare("topicExchange", "topic", true);

            /**
             * 将队列绑定到交换机
             * 参数 1 为队列的名称
             * 参数 2 为交换机的名称
             * 参数 3 为消息的routingKey（就是bindingKey）
             * 注意：在进行队列和交换机绑定时必须要保证交换机和队列都已经成功声明
             */
            channel.queueBind("topicQueue03", "topicExchange", "aa.#");

            /**
             * 接收消息，监听队列
             * 参数 1 为当前消费者需要监听的队列名，队列名必须要与发送时的队列名完全一致否则接收不到消息
             * 参数 2 为消息是否自动确认，true表示自动确认接收完消息以后会自动将消息从队列中移除
             * 参数 3 为消息接收者的标签，用于当多个消费者同时监听一个队列时用于确认不同消费者，通常为空字符串即可
             * 参数 4 为消息接收的回调方法，这个方法中具体完成对消息的处理代码
             * 注意：使用了basicConsume方法后，会启动一个线程在持续的监听队列，如果队列中有信息的数据进入则会自动接收消息
             */
            channel.basicConsume("topicQueue03", true, "", new DefaultConsumer(channel) {
                //消息的具体接收和处理方法
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "utf-8");
                    System.out.println("Receive03 消费者---> " + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}

