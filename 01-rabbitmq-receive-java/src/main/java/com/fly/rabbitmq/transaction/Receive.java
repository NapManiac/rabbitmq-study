package com.fly.rabbitmq.transaction;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive {
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

            channel.queueDeclare("transactionQueue", true, false, false, null);


            channel.exchangeDeclare("transactionExchange", "direct", true);


            channel.queueBind("transactionQueue", "transactionExchange", "transactionRoutingKey");

            /**
             * 开启事务
             * 当消费者开启事务以后，即使不做事务的提交，依然可以获取队列中的消息
             * 注意：暂时事务对接收者没什么影响
             */
            channel.txSelect();
            channel.basicConsume("transactionQueue", true, "", new DefaultConsumer(channel) {
                //消息的具体接收和处理方法
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "utf-8");
                    System.out.println("消费者---> " + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}

