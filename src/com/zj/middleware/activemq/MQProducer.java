package com.zj.middleware.activemq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MQProducer {
	private String url="tcp://192.168.237.131:61616";
	private Connection  conn;
	private Map<String,MessageProducer> producers=new HashMap<String,MessageProducer>();
	public MQProducer(){
		init();
	}
	public void init() {
		ConnectionFactory facotory =new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, url);
		try {
			conn = facotory.createConnection();
			conn.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public MessageProducer createProducer(String name){
		if(producers.containsKey(name)){
			return producers.get(name);
		}
		Session session;
		try {
			session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination destination=session.createQueue(name);
			MessageProducer producer= session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producers.put(name, producer);
			session.commit();
			return producer;
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void sendMessage(String name,String msg){
		Session session;
			try {
				session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer=producers.get(name);
				if(producer==null){
					producer=createProducer(name);
				}
				ObjectMessage message=session.createObjectMessage(msg);
				producer.send(message);
				session.commit();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			
	}
}
