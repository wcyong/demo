package org.wcy.activimq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Enumeration;

public class MessageReceiver implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;
            try {
                Enumeration mapNames = mapMessage.getMapNames();
                while (mapNames.hasMoreElements()) {
                    String name = (String) mapNames.nextElement();
                    Object value = mapMessage.getObject(name);
                    System.out.println("value = " + value);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        System.out.println("已接收消息");
    }
}