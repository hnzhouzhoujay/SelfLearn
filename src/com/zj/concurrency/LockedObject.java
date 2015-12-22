package com.zj.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedObject {
	private int value=0;
	Lock lock=new ReentrantLock();
	public void add(){
		
		try{
			lock.lock();
			value++;
		}finally{
			lock.unlock();
		}
		
	}
}
