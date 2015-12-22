package com.zj.middleware.zookeeper;


import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZKWatcher implements Watcher{
	CountDownLatch countDownLatch;
	public ZKWatcher(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}
	public ZKWatcher(){
		
	}
	@Override
	public void process(WatchedEvent event) {
		if(event.getState()==KeeperState.SyncConnected){
			System.out.println("connected ok");
			countDownLatch.countDown();
		}
		if(event.getType().equals(EventType.NodeCreated)){
			System.out.println("Node create");
		}
		if(event.getType().equals(EventType.NodeDataChanged)){
			System.out.println("Node's data changed");
		}
		if(event.getType().equals(EventType.NodeDeleted)){
			System.out.println("Node deleted");
		}
		if(event.getType().equals(EventType.NodeChildrenChanged)){
			System.out.println("Node childreChanged");
		}
	}

}
