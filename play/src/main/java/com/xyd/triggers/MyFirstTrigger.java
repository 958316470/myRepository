package com.xyd.triggers;

public class MyFirstTrigger {

    public void execute(){
        System.out.println("这是一个测试，10秒输出一次");
    }
    public void test1(){
        System.out.println("这是一个测试");
    }
    public static void main(String[] args){
        new MyFirstTrigger().test1();
    }
}
