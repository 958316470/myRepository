package demo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Demo2 {

    static String param = null;
    static String maxNum = Long.MAX_VALUE + "";
    static List<String> list = Arrays.asList("(\\d+)?-(\\d+),(\\d+)-(\\d+)?", "(\\d+)?-(\\d+)", "(\\d+)-(\\d+)?", "(\\d+)?-(\\d+),(\\d+)-(\\d+),(\\d+)-(\\d+)?");

    public static boolean checkRange(String param) {
        if(!checkNum(param)){
            return false;
        }
        if (param.startsWith("-")) {
            param =  0 + param;
        }
        //以“-”结尾的用maxNum
        if (param.endsWith("-")) {
            param = param + maxNum;
        }
        param = param.replace("-", ",");
        String[] range = param.split(",");
        int length = range.length;
        try{
            for (int i = 0; i < length; i=i+2) {
                if(Long.parseLong(range[i])>=Long.parseLong(range[i+1])){
                    return false;
                }
                if(length>2 && i/2 != 1){
                    if(Long.parseLong(range[i/2+1]) != Long.parseLong(range[i/2+2])){
                        return false;
                    }
                }
            }
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    public static boolean checkNum(String num){
        for (String rule : list) {
            //如果不匹配格式直接返回
            if (Pattern.matches(rule,num)) {
                return true;
            }
        }
        return false;
    }



    public static void main(String[] args) {
        checkRange(param);

    }


}
