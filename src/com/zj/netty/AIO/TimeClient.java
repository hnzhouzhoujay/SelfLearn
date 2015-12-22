package com.zj.netty.AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class TimeClient implements Runnable,
		CompletionHandler<Void, TimeClient> {
	AsynchronousSocketChannel channel;
	CountDownLatch count;

	public static void main(String[] args) {
		TimeClient c=new TimeClient();
		new Thread(c,"aio_client").start();
	}
	
	public TimeClient() {
		try {
			channel = AsynchronousSocketChannel.open();
			count = new CountDownLatch(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		channel.connect(new InetSocketAddress("127.0.0.1", 8000), this, this);
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void completed(Void result, TimeClient attachment) {
		String s="query time";
		ByteBuffer writeBuffer=ByteBuffer.wrap(s.getBytes());
		attachment.channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer,ByteBuffer>(){
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				// TODO Auto-generated method stub
				if(attachment.hasRemaining()){
					channel.write(attachment, attachment,this);
				}else{
					ByteBuffer readBuffer=ByteBuffer.allocate(1024);
					channel.read(readBuffer, readBuffer,new CompletionHandler<Integer,ByteBuffer>(){

						@Override
						public void completed(Integer result,
								ByteBuffer attachment) {
							byte[] bytes=new byte[attachment.remaining()];
							attachment.get(bytes);
							String time;
							try {
								time = new String(bytes,"UTF-8");
								System.out.println("now time is"+time);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							count.countDown();
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							try {
								channel.close();
								count.countDown();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					});
				}
			}
			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				// TODO Auto-generated method stub
				try {
					channel.close();
					count.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		});
		

	}
	@Override
	public void failed(Throwable exc, TimeClient attachment) {
		// TODO Auto-generated method stub
		try {
			channel.close();
			count.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		

}

