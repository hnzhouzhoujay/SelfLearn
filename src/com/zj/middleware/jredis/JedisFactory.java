package com.zj.middleware.jredis;

import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {
	private static JedisPool pool;
	static{
		ResourceBundle bundle=ResourceBundle.getBundle("redis");
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxActive(Integer.parseInt(bundle.getString("redis.pool.maxActive")));
		config.setMaxIdle(Integer.parseInt(bundle.getString("redis.pool.maxIdle")));
		config.setMaxWait(Long.parseLong(bundle.getString("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.parseBoolean(bundle.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.parseBoolean(bundle.getString("redis.pool.testOnReturn")));
		pool=new JedisPool(config,bundle.getString("redis.ip"),Integer.parseInt(bundle.getString("redis.port")));
	}
	public static Jedis getJedisFromPool(){
		return pool.getResource();
	}
	public static void releaseJedis(Jedis jedis){
		pool.returnResource(jedis);
	}
	
}
