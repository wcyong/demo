package org.wcy.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wcy.activimq.MessageSender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageSenderTest {

    @Test
    public void sender() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext-sender.xml");
        MessageSender messageSender = ctx.getBean("messageSender", MessageSender.class);
        for (int i = 0; i < 148; i++) {
            Map<String, Object> content = new HashMap<>();
            content.put("name", "wwss");
            content.put("age", i);
            content.put("will", "say hello");
            messageSender.sender(content);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}