package com.zj.netty.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class TimeClient  implements Runnable{
	Selector selector;
	SocketChannel sc;
	volatile boolean stop=false;
	public TimeClient(){
		try {
			selector=selector.open();
			sc=sc.open();
			sc.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			doconnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> keys=selector.selectedKeys();
				Iterator<SelectionKey> it=keys.iterator();
				while(it.hasNext()){
					SelectionKey key=it.next();
					it.remove();
					try {
						handler(key);
					} catch (Exception e) {
						System.out.println("处理异常");
						if(key!=null){
							key.cancel();
							if(key.channel()!=null){
								key.channel().close();
							}
						}
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
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

	private void handler(SelectionKey key) throws Exception {
		if(key.isValid()){
			SocketChannel sc=(SocketChannel) key.channel();
			if(key.isConnectable()){
				if(sc.finishConnect()){
					sc.register(selector, SelectionKey.OP_READ);
					writerequest(sc);
				}
			}
			if(key.isReadable()){
				ByteBuffer buffer=ByteBuffer.allocate(1024);
				int read=sc.read(buffer);
				if(read>0){
					buffer.flip();
					byte[] bytes=new byte[buffer.remaining()];
					buffer.get(bytes);
					String body=new String(bytes,"UTF-8");
					System.out.println("now time is"+body);
					this.stop=true;
				}else if(read<0){
					key.cancel();
					sc.close();
				}else{
					
				}
			}
		}
		
	}

	private void doconnection() throws IOException {
		boolean connected=sc.connect(new InetSocketAddress("127.0.0.1", 8000));
		if(connected){
			sc.register(selector, SelectionKey.OP_READ);
			writerequest(sc);
		}else{
			sc.register(selector, SelectionKey.OP_CONNECT);
		}
		
	}

	private void writerequest(SocketChannel sc2) throws IOException {
		// TODO Auto-generated method stub
		String s="query time";
		byte[] bytes=s.getBytes();
		ByteBuffer buffer=ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		sc.write(buffer);
		if(!buffer.hasRemaining()){
			System.out.println("send success");
		}
	}
	public static void main(String[] args) {
		TimeClient timeClient=new TimeClient();
		new Thread(timeClient,"timeClient").run();
	}

}
