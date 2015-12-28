package com.zj.middleware.kafka;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.message.MessageAndOffset;

public class Myconsumer {
	
	/*
	 * 低层次用的api,更准确的读消息
	 */
	public void consume(){
		int partition=0;
		long startOffset=1;
		String topic="topic1";
		//fetchSize设置大小与Message长度有关,太大了显示重复取了好多消息
		int fetchSize=50;
		String brokers="192.168.237.145:9092,192.168.237.145:9093,192.168.237.145:9094";
		BrokerEndPoint broker=findLeader(brokers, partition, "topic1");
		SimpleConsumer sc=new SimpleConsumer(broker.host(),broker.port(),20000,10000,"consumer");
		getLastOffset(sc,topic,partition,"consumer",System.currentTimeMillis());//报错了，key not found?
		while (true) {
			long offset=startOffset;
			/*
			 * addFetch参数分别为主题,分区号,开始偏移量，大小 ，实际位置是偏移量*大小?
			 */
			FetchRequest request = new FetchRequestBuilder().addFetch(topic,
					partition, startOffset, fetchSize).build();
			FetchResponse response = sc.fetch(request);
			ByteBufferMessageSet byteBufferMessageSet = response.messageSet(
					topic, partition);
			for (MessageAndOffset messageAndOffset : byteBufferMessageSet) {
				Message message = messageAndOffset.message();
				ByteBuffer buf = message.payload();//这个方法拿到message的bytebuff
				byte[] bytes = new byte[buf.limit()];
				buf.get(bytes);
				String msg = new String(bytes);
				long off=messageAndOffset.offset();
				System.out.println("partition:"+partition+",offset:"+off+",msg:"+msg);
			}
			startOffset=offset+1;
		}
	}
	public BrokerEndPoint findLeader(String brokers,int partition,String topic){
		BrokerEndPoint leader=findPartitionMetadata(brokers,partition,topic).leader();
		System.out.println("主题"+topic+"的分区"+partition+" leader broker在"+leader.host()+":"+leader.port());
		return leader;
	}
	/**
	 * 找到指定主题指定分区的metadata
	 * @param brokers brokers的hosts
	 * @param partition 指定分区id
	 * @param topic 主题
	 */
	public PartitionMetadata findPartitionMetadata(String brokers,int partition,String topic){
		PartitionMetadata retnParData=null;
		for(String broker:brokers.split(",")){
			//SimpleConsumer用来发送请求
			SimpleConsumer  con=null;
			String[] splits=broker.split(":");
			con=new SimpleConsumer(splits[0],Integer.parseInt(splits[1]),40000,64*1024,"findLeaderGroup");
			List<String> list=Collections.singletonList(topic);
			//TopicMetadataRequest用于请求指定主题的metadata，这里只有一个主题
			TopicMetadataRequest request=new TopicMetadataRequest(list);
			TopicMetadataResponse  respon=con.send(request);
			//这一段逻辑，找到主题topicMetadata,通过topicMetadata找到分区partitionMetadata
			//             通过分区的id判断当前分区是否是
			for(TopicMetadata topicMetadata:respon.topicsMetadata()){
				for(PartitionMetadata partitionMetadata:topicMetadata.partitionsMetadata()){
					if(partitionMetadata.partitionId()==partition){
						retnParData=partitionMetadata;
					}
				}
			}
			if(con!=null){
				con.close();
			}
		}
		return retnParData;
		
	}
	
	public long getLastOffset(SimpleConsumer consumer,String topic,int partition,String clientID,long time){
		TopicAndPartition topicAndPartition=new TopicAndPartition(topic,partition);
		PartitionOffsetRequestInfo reqInfo=new PartitionOffsetRequestInfo(time,1);
		Map<TopicAndPartition,PartitionOffsetRequestInfo> map=new HashMap<TopicAndPartition,PartitionOffsetRequestInfo>();
		OffsetRequest request=new OffsetRequest(map, kafka.api.OffsetRequest.CurrentVersion(), clientID);
		OffsetResponse response=consumer.getOffsetsBefore(request);//此方法查询指定的Offset
		long[] offsets=response.offsets(topic, partition);
		System.out.println(offsets[0]);
		return offsets[0];
	}
	public static void main(String[] args) {
		Myconsumer consumer=new Myconsumer();
		consumer.consume();
	}

}
