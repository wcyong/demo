package org.wcy.test;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MessageReceiverBTest {

    @Test
    public void receiverA() throws Exception {
        new ClassPathXmlApplicationContext("applicationContext-receiver.xml");

        TimeUnit.DAYS.sleep(Integer.MAX_VALUE);
    }
}