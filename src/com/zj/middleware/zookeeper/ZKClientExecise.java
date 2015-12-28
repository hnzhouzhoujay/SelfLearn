package com.zj.middleware.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * 模拟分布式服务
 * @author zj
 *
 */
public class ZKClientExecise {
	ZkClient zkclient;
	String url;
	List<String> serverList;
	Map<String,Integer> weightMap;
	public ZKClientExecise(String url){
		zkclient=new ZkClient(url,3000,50000);
		
	}
	/**
	 * 注册服务
	 * ip 模拟注册服务的机器ip
	 */
	public void register(String servicename,String ip,int weight){
		if(!zkclient.exists("/servercenter")){
			zkclient.createPersistent("/servercenter", "服务中心");
		}
		if(!zkclient.exists("/servercenter/"+servicename)){
			zkclient.createPersistent("/servercenter/"+servicename, servicename+"服务");
			//客户端注册一个对服务的监听
			zkclient.subscribeChildChanges("/servercenter/"+servicename, new IZkChildListener() {
				@Override
				public void handleChildChange(String path, List<String> children)
						throws Exception {
					System.out.println("子节点变化了"+path);
					weightMap=null;
					weightMap=new ConcurrentHashMap<String,Integer>();
					for(String child:children){
						System.out.print(child+"\r");
						int weight=zkclient.readData(path+"/"+child);
						weightMap.put(child, weight);
					}
					System.out.println();
					serverList=children;
				}
			});
		}
		zkclient.createEphemeral("/servercenter/"+servicename+"/"+ip, weight);
	}
	/**
	 * 模拟服务器挂了,相当于断开连接临时节点自动删除了
	 * @param servicename
	 * @param ip
	 */
	public void unregister(String servicename,String ip){
		System.out.println(ip+"挂了");
		zkclient.delete("/servercenter/"+servicename+"/"+ip);
	}
	/**
	 * 请求服务
	 */
	public void requestService(){
		System.out.println("请求"+upstream()+"机器上的服务");
	}
	/**
	 * 负载均衡
	 * 有了本地服务列表，可以很方便的自定义负载均衡策略
	 * 这里模拟一个权重的
	 */
	public String upstream(){
		List<String> list=new ArrayList<String>();
		for(Entry<String, Integer> entry:weightMap.entrySet()){
			for(int i=1;i<=entry.getValue();i++){
				list.add(entry.getKey());
			}
		}
		Random r=new Random();
		int index=r.nextInt(list.size());
		return list.get(index);
	}
	/*  测试结果成功，当某个节点断开时,本地serverList自动更新  */
	public static void main(String[] args) {
		
		ZKClientExecise zk=new ZKClientExecise("192.168.237.139:2181");
		zk.register("getLoc", "192.168.0.1",1);
		zk.register("getLoc", "192.168.0.2",2);
		zk.register("getLoc", "192.168.0.3",3);
		zk.requestService();
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		zk.unregister("getLoc", "192.168.0.3");
		zk.requestService();
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
