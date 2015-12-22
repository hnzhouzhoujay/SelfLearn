package com.zj.middleware.jredis;

import redis.clients.jedis.JedisPubSub;

public class Subsricbe extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message) {
		// TODO Auto-generated method stub
		System.out.println("message"+message);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		// TODO Auto-generated method stub
		System.out.println("message"+message);
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

}
