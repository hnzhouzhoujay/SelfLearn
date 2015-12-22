package com.zj.netty.simpleDemo.serial;

import java.io.Serializable;

public class ResponInfo implements  Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int reqId;
	int resStatus;
	String desc;
	public int getReqId() {
		return reqId;
	}
	public void setReqId(int reqId) {
		this.reqId = reqId;
	}
	public int getResStatus() {
		return resStatus;
	}
	public void setResStatus(int resStatus) {
		this.resStatus = resStatus;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String toString(){
		return "["+reqId+","+resStatus+"."+desc+"]";
	}
}
