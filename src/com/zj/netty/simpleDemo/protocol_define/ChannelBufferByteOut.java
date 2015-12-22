package com.zj.netty.simpleDemo.protocol_define;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import org.jboss.marshalling.ByteOutput;

public class ChannelBufferByteOut implements ByteOutput{
	private ByteBuf buf;
	public ChannelBufferByteOut(ByteBuf buf){
		this.buf=buf;
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(int arg0) throws IOException {
		buf.writeInt(arg0);
		
	}

	@Override
	public void write(byte[] arg0) throws IOException {
		// TODO Auto-generated method stub
		buf.writeBytes(arg0);
	}

	@Override
	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		buf.writeBytes(arg0, arg1, arg2);
		
	}
	 ByteBuf getBuffer() {
	        return buf;
	 }


}
