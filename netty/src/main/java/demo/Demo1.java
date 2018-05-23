package demo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Demo1 {

    private List<Integer> arr =  Arrays.asList(22,33,38,41,42,43,64,75,80,83,84);
    private List<Integer> arr2 =  Arrays.asList(22,43);
    private List<Integer> arr3 =  Arrays.asList(22,33,38,41,42,43);
    private List<String> result = new ArrayList<String>();
    public static void main(String[] args) {
    String[] num1 = {"10","20","20","40","40","60"};
    String[] num2 = {"20","10","20","40","40","60"};
    String[] num3 = {"20","10","10","40","40","60"};

    new Demo1().data1(num2);
    System.out.println("没有问题");
    }

    public void print(int count,String m){
        try {
            if (arr2.contains(count)) {
                if (!Demo2.checkRange(m)) {
                    System.out.println("不通过m : " + m + " count: " + count);
                    System.exit(1);
                }
            } else {
                if (Demo2.checkRange(m)) {
                    System.out.println("不通过m : " + m + " count: " + count);
                    System.exit(1);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("不通过m : " + m + " count: " + count);
            System.exit(1);
        }
    }

    public void data1(String[] nums){
        int count = 0;
        String first = "";
        String second = "";
        String three = "";
        String four = "";
        String five = "";
        String six = "";
        for (int i=0;i<2;i++){
            if(i==1){
                first = nums[0];
            }else {
                first="";
            }
            for (int j=0;j<2;j++){
                if (j==1){
                    second=nums[1];
                }else {
                    second="";
                }
                String two = String.format("%s-%s", first, second);
                count++;
                print(count,two);
                for (int t=0;t<2;t++){
                    if (t==1){
                        three=nums[2];
                    }else {
                        three="";
                    }
                    for(int f=0;f<2;f++){
                        if (f==1){
                            four=nums[3];
                        }else {
                            four="";
                        }
                        String ffff = String.format("%s-%s,%s-%s", first, second,three,four);
                        count++;
                        print(count,ffff);
                        for (int v=0;v<2;v++){
                            if(v==1){
                                five = nums[4];
                            }else {
                                five="";
                            }
                            for (int s = 0;s<2;s++){
                                if(s==1){
                                    six = nums[5];
                                }else {
                                    six="";
                                }
                                String m = String.format("%s-%s,%s-%s,%s-%s", first, second, three, four, five, six);
                                count++;
                                print(count,m);
                            }
                        }
                    }
                }
            }
        }
    }
}
