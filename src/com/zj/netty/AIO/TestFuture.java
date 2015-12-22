package com.zj.netty.AIO;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestFuture {
	public static void main(String[] args) {
		ExecutorService exe=Executors.newCachedThreadPool();
		Future<String> f=exe.submit(new Callable<String>(){
			@Override
			public String call() throws Exception {
				Thread.currentThread().sleep(10);
				return "ok";
			}
			
		});
		try {
			String s=f.get(1000,TimeUnit.MILLISECONDS);
			System.out.println(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
