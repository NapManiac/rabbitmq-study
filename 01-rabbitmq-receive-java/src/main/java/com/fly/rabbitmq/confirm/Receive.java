package com.fly.rabbitmq.confirm;

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

            channel.queueDeclare("confirmQueue", true, false, false, null);


            channel.exchangeDeclare("confirmExchange", "direct", true);


            channel.queueBind("confirmQueue", "confirmExchange", "confirmRoutingKey");

            //开启事务
            channel.txSelect();

            /**
             * 接收消息
             * 参数 2 为消息的确认机制，true表示自动确认，确认后消息会从队列中移除
             * 注意：如果消息还没来得及处理，自动确认则会丢失消息
             */

            channel.basicConsume("confirmQueue", false, "", new DefaultConsumer(channel) {
                //消息的具体接收和处理方法
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "utf-8");
                    System.out.println("消费者 处理了消息---> " + message);
                    //获取消息编号
                    long tag = envelope.getDeliveryTag();
                    //获取内部类的通道
                    Channel c = this.getChannel();
                    /**
                     * 手动确认消息，确认以后表示当前消息已经成功处理了，需要从队列中移除掉
                     * 这个方法应该在当前消息的处理程序全部完成以后执行
                     * 参数 1 为消息的序号
                     * 参数 2 是否批量处理
                     */
                    c.basicAck(tag, true);
                    //如何启动了事务，消费者确认模式为手动，那么必须要提交事务，否则即使调用了确认方法，消息也不会从队列中被移除掉
                    c.txCommit();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}

