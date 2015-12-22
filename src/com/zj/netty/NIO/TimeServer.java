package com.zj.netty.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class TimeServer implements Runnable {
	private int port=8000;
	private Selector selector;
	private ServerSocketChannel svrChannel;
	private volatile boolean stop=false;
	public TimeServer(){
		try {
			selector=Selector.open();
			svrChannel=ServerSocketChannel.open();
			svrChannel.socket().bind(new InetSocketAddress(port));
			svrChannel.configureBlocking(false);//设定为非阻塞
			svrChannel.register(selector, SelectionKey.OP_ACCEPT);//监听服务器的接收请求
			System.out.println("timeserver start");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		TimeServer timeserver=new TimeServer();
		new Thread(timeserver,"timeserverThread1").start();
	}

	@Override
	public void run() {
		while(!stop){
			try {
				selector.select(1000);//将阻塞1S直到有监听事件发生
				Set<SelectionKey> selkeys=selector.selectedKeys();
				Iterator<SelectionKey> it=selkeys.iterator();
				SelectionKey selkey=null;
				while(it.hasNext()){
					selkey=it.next();
					it.remove();//去除此次监听，因为监听完成后会保留，所以要去除
					try{
						handlerInput(selkey);//监听处理
					}catch(Exception e){
						//异常发生时，取消监听，关闭对应的channel
						if(selkey!=null){
							selkey.cancel();
							System.out.println("处理异常");
							if(selkey.channel()!=null){
								selkey.channel().close();
							}
						}
					}
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(selector!=null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	protected void handlerInput(SelectionKey selkey) throws IOException {
		if(selkey.isValid()){
			//当服务器接收新的请求，将产生的SocketChannel设为阻塞并注册监听读事件
			if(selkey.isAcceptable()){
				ServerSocketChannel svrChannel=(ServerSocketChannel) selkey.channel();
					SocketChannel sc=svrChannel.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
			}
			if(selkey.isConnectable()){
				
			}
			if(selkey.isReadable()){//监听读
				SocketChannel sc=(SocketChannel) selkey.channel();
				ByteBuffer buffer=ByteBuffer.allocate(1024);
					int read=sc.read(buffer);
					if(read>0){
						buffer.flip();
						byte[] bytes=new byte[buffer.remaining()];
						buffer.get(bytes);
						String body=new String(bytes,"UTF-8");
						//判断请求发来的字符是否正确，并写入socket
						String time="query time".equalsIgnoreCase(body)?new Date().toString():"bad request";
						dowrite(sc,time);
					}else if(read<0){
						selkey.cancel();
						sc.close();
					}else{
						
					}
			}
			if(selkey.isWritable()){
				
			}
		}
	}
	private void dowrite(SocketChannel sc, String time) throws IOException {
		if(sc!=null&&!time.isEmpty()){
			byte[] bytes=time.getBytes();
			ByteBuffer buffer=ByteBuffer.allocate(bytes.length);
			buffer.put(bytes);
			buffer.flip();
			sc.write(buffer);
		}
		
	}

}
