package com.zj.netty.simpleDemo.protocol_define;

import java.io.Serializable;

public final class NettyMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Header header;
	private Object body;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	@Override
	public String toString(){
		return "["+this.getHeader()+","+this.getBody()+"]";
	}
}
