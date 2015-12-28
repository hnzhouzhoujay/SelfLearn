package com.zj.middleware.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class ZookeeperDemo {
	public static void main(String[] args) {
		new ZookeeperDemo().publishNode();
	}
	public void publishNode(){
		String url="192.168.237.139:2181";
		try {
			CountDownLatch countDownLatch=new CountDownLatch(1);
			ZKWatcher watcher=new ZKWatcher(countDownLatch);
			ZooKeeper zookeeper=new ZooKeeper(url,2000,watcher);
//			ZooKeeper zookeeper=new ZooKeeper(url,10000,null);
			countDownLatch.await(); 
			String actualPath=zookeeper.create("/root", "root hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println(actualPath);
			Stat stat=new Stat();
			byte[] before=zookeeper.getData("/root", true, stat);
			System.out.println(new String(before));
			zookeeper.setData("/root", "welcom".getBytes(), -1);
			byte[] data=zookeeper.getData("/root", true, stat);
			System.out.println(new String(data));
			
			zookeeper.delete("/root", -1);
			
//			
//			Stat nodeStat=zookeeper.exists("/root/child", false);
////			Stat nodeStat=zookeeper.exists("/root/child", watcher);
//			if(nodeStat==null){
//				String childPath=zookeeper.create("/root/child", "the kid".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//				System.out.println(childPath);
//			}else{
//				throw new RuntimeException("节点已存在");
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
