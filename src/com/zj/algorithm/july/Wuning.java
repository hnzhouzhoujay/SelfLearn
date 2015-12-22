package com.zj.algorithm.july;

public class Wuning {
	public static void main(String[] args) {
//		int a[]={1,2,3,4,5,6,7,8,9,10};
//		int b[]={1,2,3,4,5,8};
//		int j=0,k=0;
//		for(int i=0;i<a.length;i++){
//			if(j<b.length&&a[i]==b[j]){
//				if(a[k]==b[j])
//					k++;
//				a[i]=-1;
//				j++;
//			}
//		}
//		System.out.println(a[k]);
//		System.out.println(test());
		int[] b={5,2,3,6};
		System.out.println("heelo");
		System.out.println(test1(b));
	
	}
	public static int test(){
		int a[]={1,2,3,4,5,6,7,8,9,10};
		int b[]={1,2,3,4,5,8};
		int j=0;
		for(int i=0;i<a.length;i++){
			if(j<b.length&&a[i]!=b[j]){
				return a[i];
			}else{
				j++;
			}
		}
		return -1;
	}
	public static int test1(int b[]){
		int a[]={1,2,3,4,5,6,7,8,9,10};
		int min=b[0];
		for(int i=0;i<b.length;i++){
			if(b[i]<min){
				min=b[i];
			}
		}
		for(int i=0;i<a.length;i++){
			if(a[i]<min){
				return a[i];
			}
		}
		return -1;
	}
}
