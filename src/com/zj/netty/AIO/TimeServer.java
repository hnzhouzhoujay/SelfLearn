package com.zj.netty.AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TimeServer implements Runnable {
	AsynchronousServerSocketChannel server;
	CountDownLatch count;
	public static void main(String[] args) {
		TimeServer s=new TimeServer(8000);
		new Thread(s,"nio_timeServer").start();
	}
	public TimeServer(int port){
		try {
			server= AsynchronousServerSocketChannel.open();//创建异步ServerSocket
			server.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		count=new CountDownLatch(1);
		doaccept();
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void doaccept() {
		server.accept(this,new CompletionHandler<AsynchronousSocketChannel,TimeServer>(){
			@Override
			public void completed(AsynchronousSocketChannel result,
					TimeServer attachment) {
				attachment.server.accept(attachment, this);//递归调用
				ByteBuffer readbuffer=ByteBuffer.allocate(100);
				result.read(readbuffer, readbuffer, new ReadCompletionHandler(result));
				
			}
			@Override
			public void failed(Throwable exc, TimeServer attachment) {
				attachment.count.countDown();
			}
		});
	}
	class ReadCompletionHandler implements  CompletionHandler<Integer,ByteBuffer>{
		private AsynchronousSocketChannel channel;
		public ReadCompletionHandler(AsynchronousSocketChannel channel){
			this.channel=channel;
		}
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			attachment.flip();
			byte[] body=new byte[attachment.remaining()];
			attachment.get(body);
				try {
					String query=new String(body,"UTF-8");
					System.out.println("receiver:"+query);
					String curtime="query time".equals(query)?new Date().toString():"bad query";
					dowrite(curtime);
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		}

		private void dowrite(String curtime) {
			System.out.println("curtime:"+curtime);
			ByteBuffer writebuffer=ByteBuffer.wrap(curtime.getBytes());
			writebuffer.flip();
			this.channel.write(writebuffer, writebuffer,new CompletionHandler<Integer,ByteBuffer>(){

				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					//如果没写完，继续写
					if(attachment.hasRemaining()){
						channel.write(attachment, attachment, this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		
			});
		}
		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
