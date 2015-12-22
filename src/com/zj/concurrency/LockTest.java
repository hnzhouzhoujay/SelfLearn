package com.zj.concurrency;

public class LockTest {
	public static void main(String[] args) {
		final LockedObject obj=new LockedObject();
		for(int i=0;i<5;i++){
			new Thread(new Runnable(){
				@Override
				public void run() {
					obj.add();
					try {
						System.out.println("拿到锁处理中");
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}).start();
				
			
		}
	}
}
