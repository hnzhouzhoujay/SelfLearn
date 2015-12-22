package com.zj.netty.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class SelectorTest {
	public static void main(String[] args) {
		try {
			ServerSocketChannel server=ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress("127.0.0.1", 8000));//设置服务器IP，端口
			server.configureBlocking(false);
			while(true){
				SocketChannel channel=server.accept();
				if(channel!=null){
					System.out.println("open a connection");
					Selector selector=Selector.open();//打开一个selector
					channel.configureBlocking(false);//设定为非阻塞模式
					ByteBuffer buffer=ByteBuffer.allocate(48);
					int interestKey=SelectionKey.OP_CONNECT  | SelectionKey.OP_READ | SelectionKey.OP_WRITE;//设定感兴趣的监听项
					SelectionKey selectionKey=channel.register(selector, interestKey);//注册
					selectionKey.attach(buffer);//附加一个BUFFer方便找到对应Channel的buffer
					while(true){
						int readyChannel= selector.select();//有多少个通道准备就绪了
						if(readyChannel==0) continue;
						Set<SelectionKey> selkeys=selector.selectedKeys();//拿到selector上所有的selkey
						Iterator<SelectionKey> it=selkeys.iterator();
						while(it.hasNext()){
							SelectionKey selKey=it.next();
							ByteBuffer buf=(ByteBuffer) selKey.attachment();
							SocketChannel socket=(SocketChannel) selKey.channel();
							if(selKey.isAcceptable()){//接收连接就绪
								System.out.println("接收就绪-----");
							}
							if(selKey.isConnectable()){//连接就绪
								System.out.println("连接就绪-----");
							}
							if(selKey.isReadable()){//读就绪
								socket.read(buf);
								System.out.println(buf.asCharBuffer().toString());
								System.out.println("读就绪-----");
							}
							if(selKey.isWritable()){//写就绪
								System.out.println("写就绪-----");
							}
							it.remove();//完成操作移除
						}
					
				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
