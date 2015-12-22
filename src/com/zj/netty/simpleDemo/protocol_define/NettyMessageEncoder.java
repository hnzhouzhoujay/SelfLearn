package com.zj.netty.simpleDemo.protocol_define;

import java.util.List;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {
	
	private MarshallingMessageEncoder marshallingEncoder;
	public NettyMessageEncoder(){
		this.marshallingEncoder=new MarshallingMessageEncoder();
	}
	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg,
			List<Object> out) throws Exception {
		if(msg==null || msg.getHeader()==null){
			throw new RuntimeException("非法协议");
		}
		ByteBuf buf=Unpooled.buffer();
		buf.writeInt(msg.getHeader().getCrcCode());
		buf.writeInt(msg.getHeader().getLength());
		buf.writeLong(msg.getHeader().getSessionID());
		buf.writeByte(msg.getHeader().getPriority());
		buf.writeByte(msg.getHeader().getType());
		String key=null;
		Object value=null;
		byte[] bytes=null;
		for(Entry<String, Object> entry:msg.getHeader().getAttachment().entrySet()){
			key=entry.getKey();
			bytes=key.getBytes("utf-8");
			int keylength=bytes.length;
			buf.writeInt(keylength);
			buf.writeBytes(bytes);
			value=entry.getValue();
			marshallingEncoder.encode(value,buf);
		}
		key=null;
		value=null;
		bytes=null;
		if(msg.getBody()!=null){
			marshallingEncoder.encode(value,buf);
		}else{
			buf.writeInt(0);
		}
		buf.setInt(4, buf.readableBytes());
	}

}
