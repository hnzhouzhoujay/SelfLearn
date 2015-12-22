package com.zj.netty.simpleDemo.protocol_define;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * 协议头
 *  crcCode:校验码  length:消息总长度 sessionID:唯一会话标识  type:类型  priority:优先级 attachment:附加信息
 * @author Administrator
 *
 */
public class Header implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int crcCode=0xabef0101;
	private int length;
	private long sessionID;
	private byte type;
	private byte priority;
	private Map<String,Object> attachment=new HashMap<String,Object>();
	
	public int getCrcCode() {
		return crcCode;
	}
	public void setCrcCode(int crcCode) {
		this.crcCode = crcCode;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public long getSessionID() {
		return sessionID;
	}
	public void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public byte getPriority() {
		return priority;
	}
	public void setPriority(byte priority) {
		this.priority = priority;
	}
	public Map<String, Object> getAttachment() {
		return attachment;
	}
	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}
	@Override
	public String toString(){
		return "["+this.getCrcCode()+","+this.getLength()+","+this.getPriority()+","
				+this.getSessionID()+","+this.getType()+","+this.getAttachment()+"]";
	}
}
