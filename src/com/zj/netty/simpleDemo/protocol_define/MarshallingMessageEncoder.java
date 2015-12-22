package com.zj.netty.simpleDemo.protocol_define;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jboss.marshalling.Marshaller;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;

public class MarshallingMessageEncoder {
	private Marshaller marshaller;
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	 
	public MarshallingMessageEncoder(){
		this.marshaller=MarshallingCodeCFactory.buildMarshalling();
	}

	public void encode(Object value, ByteBuf buf) throws Exception {
		int startIndex=buf.writerIndex();
		buf.writeBytes(LENGTH_PLACEHOLDER);
		ChannelBufferByteOut byteout=new ChannelBufferByteOut(buf);
		marshaller.start(byteout);
		marshaller.writeObject(value);
		marshaller.finish();
		marshaller.close();
		int writerIndex=buf.writerIndex();
		buf.setInt(startIndex, writerIndex-4-startIndex);
		
	}
	public static void main(String[] args) {
		MarshallingMessageEncoder messageEncoder=new MarshallingMessageEncoder();
		NettyMessage message=new NettyMessage();
		Header header=new Header();
		header.setSessionID(5);
		header.setPriority((byte)4);
		message.setHeader(header);
		ByteBuf buf=Unpooled.buffer(1024);
		try {
			messageEncoder.encode(message, buf);
			String content=buf.toString(Charset.forName("utf-8"));
			System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
