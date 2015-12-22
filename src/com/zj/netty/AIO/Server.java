package com.zj.netty.AIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Server {
	static Charset charset=Charset.forName("utf-8");
	static CharsetEncoder encoder=charset.newEncoder();
	public static void main(String[] args) {
		
			AsynchronousChannelGroup group;
			try {
				group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
				final AsynchronousServerSocketChannel server=AsynchronousServerSocketChannel.open(group);
				server.bind(new InetSocketAddress(7499));
				server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>(){
					
					@SuppressWarnings("static-access")
					@Override
					public void completed(AsynchronousSocketChannel result,
							Void attachment) {
						server.accept(null,this);//递归调用，让服务器可以不断接受请求
						try {
							String time="now is:"+new Date().toString();
							ByteBuffer buffer;
//							buffer=encoder.encode(CharBuffer.wrap(time));
							buffer=ByteBuffer.wrap(time.getBytes());
							Future<Integer> f=result.write(buffer);
							System.out.println("write to client"+time);
							f.get();//等待写操作完成
							result.close();
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
					@Override
					public void failed(Throwable exc, Void attachment) {
						exc.printStackTrace();
					}
				});
				group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
			
}
