package com.fly.rabbitmq.rabbitmq.service;

public interface ReceiveService {
    void directReceive(String message);
    void fanoutReceive01(String message);
    void fanoutReceive02(String message);
    void topicReceive01(String message);
    void topicReceive02(String message);
    void topicReceive03(String message);
}
