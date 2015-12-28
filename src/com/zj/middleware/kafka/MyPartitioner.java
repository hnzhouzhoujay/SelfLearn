package com.zj.middleware.kafka;

import java.util.Map;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * 自建分区函数
 * @author Administrator
 *
 */
public class MyPartitioner implements Partitioner{
	/**不加这个构造会报错
	 */
	public MyPartitioner(VerifiableProperties verifiableProperties){
		
	}
	@Override
	public int partition(Object key, int partitionnum) {
		// TODO Auto-generated method stub
		return Integer.valueOf((String)key)%partitionnum;
	}



}
