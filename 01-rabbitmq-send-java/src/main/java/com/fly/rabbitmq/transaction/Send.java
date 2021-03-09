package com.fly.rabbitmq.transaction;

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

            channel.queueDeclare("transactionQueue", true, false, false, null);


            channel.exchangeDeclare("transactionExchange", "direct", true);


            channel.queueBind("transactionQueue", "transactionExchange", "transactionRoutingKey");

            String message = "transaction的测试消息";

            //启动事务，需要配合txCommit()提交或者txRollBack回滚
            channel.txSelect();

            channel.basicPublish("transactionExchange", "transactionRoutingKey", null, message.getBytes("utf-8"));

            channel.basicPublish("transactionExchange", "transactionRoutingKey", null, message.getBytes("utf-8"));

            channel.txCommit();
            System.out.println("消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    //回滚事务，放弃事务中当前没有提交的消息，释放能存
                    channel.txRollback();
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
