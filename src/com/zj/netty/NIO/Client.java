package com.zj.netty.NIO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;

public class Client {
	public static void main(String[] args) {
			try {
				SocketChannel s=SocketChannel.open();
				s.configureBlocking(false);
				s.connect(new InetSocketAddress("127.0.0.1", 8000));
				while(!s.finishConnect()){
					//当没完成连接时
				};
				ByteBuffer cb=ByteBuffer.allocate(100);
				cb.asCharBuffer().append("heelo");
				s.write(cb);
				s.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
}
