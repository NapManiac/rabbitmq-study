package com.fly.rabbitmq.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * topic 和 fanout 都能实现一个消息发给多个队列
 * topic 适用于不用的功能模块来接收一个消息
 */
public class Send {

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
            connection = factory.newConnection(); //获取连接
            channel = connection.createChannel(); //获取通道

            /**
             * 声明一个交换机
             * 参数 1 为交换机的名称
             * 参数 2 为交换机的类型 direct fanout topic headers
             * 参数 3 是否为持久化交换机
             * 注意：
             *      1、声明交换机时，如果这个交换机已存在，则会放弃声明
             */
            channel.exchangeDeclare("topicExchange", "topic", true);

            String message = "topic的测试消息";
            /**
             * 发送消息到MQ
             * 参数 1 为交换机名称，这里为空字符串表示不使用交换机
             * 参数 2 为队列名或RoutingKey，当指定了交换机名称以后这个值就是RoutingKey
             * 参数 3 为消息属性信息，通常为空即可
             * 参数 4 为具体的消息数据的字节数组
             */
            channel.basicPublish("topicExchange", "aa.sdf.sds", null, message.getBytes("utf-8"));

            System.out.println("消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
