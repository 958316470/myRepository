package com.xyd;

public class Demo {

    public static void main(String[] args){
        String[] argStr = {"resume","force","sort","solr","threads","numTasks",
        "topN","curTime","filter","normalize","seed","seedDir","class","depth",
        };
        for(String arg : argStr){
            System.out.println(formatStr(arg));
            System.out.println();
        }
        String[] stats = {"msg","phase","progress","jobs","counters"};
        for(String arg : stats){
            System.out.println(formatStat(arg));
            System.out.println();
        }
    }

    public static String formatStr(String name){
        return "public static final String ARG_" +name.toUpperCase()+" = \""+name+"\";";
    }

    public static String formatStat(String name){
        return "public static final String STAT_" +name.toUpperCase()+" = \""+name+"\";";
    }
}
