package com.fly.rabbitmq.confirm.addConfirmListener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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

            String message = "发送者确认模式的测试消息";


            //启动发送者确认模式
            channel.confirmSelect();

            /**
             * 异步消息监听器
             * 发送消息前启动
             */
            channel.addConfirmListener(new ConfirmListener() {
                /**
                 * 消息确认以后的回调方法
                 * @param deliveryTag 被确认的消息编号
                 * @param multiple 是否同时确认了多个
                 * @throws IOException
                 */
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息被确认了 --- 消息编号：" + deliveryTag + " 是否确认了多条：" + multiple);
                }
                //消息没有确认以后的回调方法
                //如何这个方法执行，则消息需要进行补发
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息没有被确认了 --- 消息编号：" + deliveryTag + " 是否没有确认多条：" + multiple);

                }
            });

            for (int i = 0; i < 10000; i++) {
                channel.basicPublish("confirmExchange", "confirmRoutingKey", null, message.getBytes("utf-8"));
            }
            System.out.println("消息发送成功");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }  finally {
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
