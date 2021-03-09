package com.fly.rabbitmq;

import com.fly.rabbitmq.service.SendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private SendService sendService;

	@Test
	void contextLoads() {
	}

	@Test
	void testSendMessage() {
		String msg = "springboot集成rabbitmq测试消息！";
		sendService.sendMessage(msg);
	}

	@Test
	void testSendFanoutMessage() {
		String msg = "springboot集成rabbitmq测试消息！ fanout";
		sendService.sendFanoutMessage(msg);
	}

	@Test
	void testSendTopicMessage() {
		String msg = "springboot集成rabbitmq测试消息！ topic";
		sendService.sendTopicMessage(msg);
	}

}
