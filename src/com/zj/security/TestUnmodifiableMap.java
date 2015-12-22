package com.zj.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestUnmodifiableMap {
	public static void main(String[] args) {
		final Map<String,String> map=new HashMap<String,String>();
		map.put("zj", "111");
		map.put("xf", "22");
		 Map<String, String> unmodifiableMap=Collections.unmodifiableMap(map);
//		 unmodifiableMap.put("xh", "333");
		 map.put("xh", "333");
		 System.out.println(unmodifiableMap.get("xh"));
		 final ConcurrentMap<String,String> map1=new ConcurrentHashMap<String,String>();
		 map1.replace("xx", "1");
		 CopyOnWriteArrayList<String> list;
		 
	
	}
}
