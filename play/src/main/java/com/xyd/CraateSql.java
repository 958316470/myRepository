package com.xyd;

import java.util.List;

public class CraateSql {

    private int start = 1;
    private int startYear = 1970;
    private int startMonth = 0;
    private int startDay = 0;

    public static void main(String[] args) {
        String[] nameArr = {"赵四","周小元","王配","大景鱼","小鱼","虾米",
                            "贝塔","钱图可","苏尔","孙婆婆","马鞍山","西二旗",
                             "吴情","无穷","吴进","吴勇","郑凯","魔鬼",
                             "蘑菇","迷糊","诚挚","橙汁","惩戒","周无",
                             "孙版","小琼","夏侯惇","闵月","半月","天外飞仙",
                            "孙策","大乔","诸葛亮","黄月英","黄忠","达达"};
        CraateSql sql = new CraateSql();
        sql.generateSql(nameArr);
    }

    public void generateSql(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            StringBuilder sb = new StringBuilder("INSERT INTO `employee` VALUES (");
            sb.append("'" + getID(i+start) + "',");
            sb.append("'" + arr[i] + "',");
            if ((i+start) % 2 == 0) {
                sb.append("'女',");
            } else {
                sb.append("'男',");
            }
            String birthDay = getBirthDay();
            sb.append("'" + birthDay + "',");
            if (i % 2 == 0) {
                sb.append("'本科',");
            } else {
                sb.append("'专科',");
            }
            sb.append("'245622"+birthDay.replace("-","")+"5266',");
            if (i % 2 == 0) {
                sb.append("'汉族',");
            } else {
                sb.append("'蒙古族',");
            }
            if (i % 2 == 0) {
                sb.append("'员工',");
            } else if(i % 2!=0 && i % 3 ==0){
                sb.append("'组长',");
            } else {
                sb.append("'经理',");
            }
            if (i % 2 == 0) {
                sb.append("'03',");
            } else {
                sb.append("'04',");
            }
            if ((10 + i) <= 60) {
                sb.append("'" + (10 + i) + "'");
            }else {
                sb.append("'"+i+"'");
            }
            sb.append(");");
            System.out.println(sb.toString());
        }
    }

    public String getID(int i){
        if (i < 10){
            return "0"+i;
        }
        return i+"";
    }

    public String getBirthDay() {
        startYear++;
        if (startYear > 2017) {
            startYear = 1960;
        }
        startMonth++;
        if (startMonth > 12) {
            startMonth = 1;
        }
        startDay++;
        String month = startMonth + "";
        String small = "4,6,9,11";
        String big = "1,3,5,7,8,10,12";
        if (small.contains(month) && startDay > 30) {
            startDay = 1;
        } else if (big.contains(month) && startDay > 31) {
            startDay = 1;
        } else if (startDay > 28) {
            if ((startYear % 4 == 0 && startYear % 100 != 0) || startYear % 400 == 0) {
                if (startDay > 29) {
                    startDay = 1;
                }
            } else {
                startDay = 1;
            }
        }

        return startYear + "-" + getID(startMonth) + "-" + getID(startDay);
    }
}
