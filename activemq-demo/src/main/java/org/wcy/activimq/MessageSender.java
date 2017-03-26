package org.wcy.activimq;

import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

public class MessageSender {

    private final JmsTemplate jmsTemplate;

    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sender(final Map<String, Object> map) {
        jmsTemplate.convertAndSend(map);
    }
}