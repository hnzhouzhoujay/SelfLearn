package com.zj.middleware.zookeeper;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;


import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ZkClientDemo {
	List<String> serviceList;
	public static void main(String[] args) {
		ZkClientDemo demo=new ZkClientDemo();
		demo.provider("service-a");
		demo.provider("service-b");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void provider(String servicename){
		String server="192.168.237.131:2181";
		String ROOT="/configcenter";
		ZkClient zkclient=new ZkClient(server);
		if(!zkclient.exists(ROOT)){
			zkclient.createPersistent(ROOT);
		}
		String servicePath=ROOT+"/"+servicename;
		if(!zkclient.exists(servicePath)){
			zkclient.createPersistent(servicePath);
		}
		try {
			String ip=Inet4Address.getLocalHost().getHostAddress();
			zkclient.createPersistent(servicePath+"/"+ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void delNode(String path){
		String server="192.168.237.131:2181";
		ZkClient zkclient=new ZkClient(server);
		zkclient.delete(path);
	}
	//消费
	public void customer(){
		String servername="service-a";
		String servicePath="/configcenter/"+servername;
		String server="192.168.237.131:2181";
		ZkClient zkclient=new ZkClient(server);
		zkclient.subscribeChildChanges(servicePath, new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				// TODO Auto-generated method stub
				serviceList=currentChilds;
				System.out.println(serviceList);
			}
		});
		
		boolean exist=zkclient.exists(servicePath);
		if(exist){
			serviceList=zkclient.getChildren(servicePath);
			System.out.println(serviceList);
		}else{
			System.out.println("服务不存在");
		}
	}
}
