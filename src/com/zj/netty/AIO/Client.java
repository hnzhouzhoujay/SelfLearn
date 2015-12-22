package com.zj.netty.AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
	public static void main(String[] args) {
		try {
			AsynchronousSocketChannel client=AsynchronousSocketChannel.open();
			Future<Void> f=client.connect(new InetSocketAddress("127.0.0.1", 7499));
			f.get();
			final ByteBuffer buffer=ByteBuffer.allocate(100);
			client.read(buffer, null, new CompletionHandler<Integer, Void>() {

				@Override
				public void completed(Integer result, Void attachment) {
					try {
						buffer.flip();
						byte[] bytes=new byte[buffer.remaining()];
						buffer.get(bytes);
						System.out.println("client receiver:"+new String(bytes,"utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					exc.printStackTrace();
				}
			});
			Thread.sleep(10000);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
}
