package demo;


import java.util.Random;

public class Demo3 {
    static int NUM = 54;
    static int COUNT = 20;
    static int INIT_NUM = 17;
    static int THREE = 3;
    static int[] arr = new int[NUM];
    static int[] person1 = new int[COUNT];
    static int[] person2 = new int[COUNT];
    static int[] person3 = new int[COUNT];

    public static void main(String[] args) {




    }

    public static void swap(int m, int n) {
        int t = arr[m];
        arr[m] = arr[n];
        arr[n] = t;
    }

}
