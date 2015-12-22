package com.zj.middleware.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MQCustomer {
	public static void init() throws JMSException{
		ConnectionFactory facotory =new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://192.168.237.131:61616");
		Connection  conn=facotory.createConnection();
		conn.start();
		Session session=conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination=session.createQueue("hello");
		MessageConsumer customer=session.createConsumer(destination);
		while(true){
			ObjectMessage message=(ObjectMessage) customer.receive(10000);
			if(message!=null){
				String words=(String)message.getObject();
				System.out.println(words);
			}else{
				break;
			}
		}
		conn.close();
	}
	public static void main(String[] args) {
		try {
			init();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
