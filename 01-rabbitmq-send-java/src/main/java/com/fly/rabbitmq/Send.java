package com.fly.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
            channel.queueDeclare("myQueue", true, false, false, null);

            String message = "我的RabbitMQ的测试消息2";
            /**
             * 发送消息到MQ
             * 参数 1 为交换机名称，这里为空字符串表示不使用交换机
             * 参数 2 为队列名或RoutingKey，当指定了交换机名称以后这个值就是RoutingKey
             * 参数 3 为消息属性信息，通常为空即可
             * 参数 4 为具体的消息数据的字节数组
             */
            channel.basicPublish("", "myQueue", null, message.getBytes("utf-8"));
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
