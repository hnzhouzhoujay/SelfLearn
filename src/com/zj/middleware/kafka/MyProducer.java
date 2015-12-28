package com.zj.middleware.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class MyProducer {
	private Producer<String, String> producer;
	public MyProducer(){
		Properties props=new Properties();
		//消息序列化类，可自定义
		props.put("serializer.class", "kafka.serializer.StringEncoder");  
		//broker集群,格式为xx:端口，...
        props.put("metadata.broker.list", "192.168.237.145:9092,192.168.237.145:9093,192.168.237.145:9094");  
        //指定分区函数这里为定义的
        props.put("partitioner.class", "com.zj.middleware.kafka.MyPartitioner");  
        props.put("request.required.acks", "1"); 
        ProducerConfig config=new ProducerConfig(props);
        producer=new Producer<String, String>(config);
	}
	/**
	 * KeyedMessage 可指定key的message,key可用于分区
	 */
	public void sendMessage(){
		//单条发送
		for(int i=0;i<100;i++){
			KeyedMessage<String, String> message=new KeyedMessage<String, String>("topic1", i+"", "hello"+i);
			producer.send(message);
		}
		//多条发送
		List<KeyedMessage<String, String>> messages=new ArrayList<KeyedMessage<String, String>>();
		for(int i=0;i<100;i++){
			KeyedMessage<String, String> message=new KeyedMessage<String, String>("topic1", i+"", "listsend"+i);
			messages.add(message);
		}
		producer.send(messages);
	}
		public static void main(String[] args) {
			MyProducer producer=new MyProducer();
			producer.sendMessage();
			
		}
}
