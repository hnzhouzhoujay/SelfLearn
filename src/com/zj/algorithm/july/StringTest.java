package com.zj.algorithm.july;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


import org.apache.activemq.console.Main;

/**
 * 字符串处理
 * @author Administrator
 *
 */
public class StringTest {
	public static void main(String[] args) {
		int a=1;int b=5; int c=3;
		a=b=c;
		System.out.println(a+","+b+","+c);
		System.out.println(test1("ab2sfa21aba"));
		System.out.println(test1("abcbbcacb"));
	}
	/**
	 * 给定一个字符串，删掉里面的a字符,复制里面的b字符，字符串长度不限
	 * @param str
	 * @return
	 */
	public static String test1(String str){
		int j=0;
		int count=0;
		char[] chars=str.toCharArray();
		//就利用原字符串设置 j始终小于i的值，并且j只用来保存非a的字符
		for(int i=0;i<chars.length;i++){
			if(chars[i]!='a'){
				chars[j]=chars[i];
				j++;
			}
			if(chars[i]=='b'){
				count++;//统计b的个数
			}
		}
		int len=j+count;//新长度
		char[] c=new char[len];
		//倒排，不需要移动插入后的数据
		int a=len-1;
		for(int i=j-1;i>=0;i--){
			c[a--]=chars[i];
			if(chars[i]=='b'){
				c[a--]='b';
			}
		}
//		System.out.println(j);
		String s=new String();
		for(int i=0;i<c.length;i++){
			s+=c[i];
		}
		return s;
	}
}
