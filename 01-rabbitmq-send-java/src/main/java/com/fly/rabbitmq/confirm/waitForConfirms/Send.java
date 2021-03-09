package com.fly.rabbitmq.confirm.waitForConfirms;

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

            channel.queueDeclare("confirmQueue", true, false, false, null);


            channel.exchangeDeclare("confirmExchange", "direct", true);


            channel.queueBind("confirmQueue", "confirmExchange", "confirmRoutingKey");

            String message = "普通发送者确认模式的测试消息";


            //启动发送者确认模式
            channel.confirmSelect();
            channel.basicPublish("confirmExchange", "confirmRoutingKey", null, message.getBytes("utf-8"));
            //阻塞线程，等待服务器返回响应，
            //可以为这个方法指定一个时间，用于等待服务器响应时间，超时则抛出异常
            //可以使用redis定时补发，或者递归重发
            boolean flag = channel.waitForConfirms();

            System.out.println("消息发送成功" + flag);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
