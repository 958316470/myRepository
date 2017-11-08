package com.xyd;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class CreateStatus {

    public static void main(String[] args) {
        String className = "Host";
        String[] nameArr = {"metadata","outlinks","inlinks"};
        String[] typeArr = {"Map<CharSequence,ByteBuffer>","Map<CharSequence, CharSequence>","Map<CharSequence, CharSequence>"};
        String schema = "";
        String sourceString = head(className,schema,typeArr) + Filed(nameArr,typeArr)+endFile(nameArr,typeArr,className);   //待写入字符串
        String path = "F:/work/"+className+".java";
        byte[] sourceByte = sourceString.getBytes();
        if(null != sourceByte){
            try {
                File file = new File(path);     //文件路径（路径+文件名）
                if (!file.exists()) {   //文件不存在则创建文件，先创建目录
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    file.createNewFile();
                }
                FileOutputStream outStream = new FileOutputStream(file);    //文件输出流用于将数据写入文件
                outStream.write(sourceByte);
                outStream.close();  //关闭文件输出流
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String head(String name, String schema,String[] typeArr) {
        StringBuilder builder = new StringBuilder();
        builder.append("package com.nutch.storage;\n" +
                "\n" +
                "import org.apache.avro.AvroRuntimeException;\n" +
                "import org.apache.avro.Schema;\n" +
                "import org.apache.avro.data.RecordBuilder;\n" +
                "import org.apache.avro.specific.SpecificRecordBuilderBase;\n" +
                "import org.apache.gora.persistency.Dirtyable;\n" +
                "import org.apache.gora.persistency.Persistent;\n" +
                "import org.apache.gora.persistency.impl.DirtyListWrapper;\n" +
                "\n" +
                "import java.nio.ByteBuffer;\n");
        boolean mapFlag = true;
        boolean listFlag = true;
        for(String type : typeArr) {
            if(type.contains("Map") && mapFlag){
                builder.append("import java.util.Map;\n");
                mapFlag = false;
            }else if(type.contains("List") && listFlag){
                builder.append("import java.util.List;\n");
                listFlag = false;
            }
        }
        builder.append("public class " + name + " extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord,\n" +
                "    org.apache.gora.persistency.Persistent{\n");
        builder.append(" public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()\n" +
                "            .parse(\"" + schema + "\");\n\n");
        return builder.toString();
    }

    public static String Filed(String[] nameArr, String[] typeArr) {
        StringBuilder builder = new StringBuilder();
        builder.append("public static enum Field {\n");
        int length = nameArr.length;
        //Field的值
        for (int i = 0; i < length; i++) {
            String temp = nameArr[i].toUpperCase() + "(" + i + ",\"" + nameArr[i] + "\")";
            builder.append(temp);
            if (i == length - 1) {
                builder.append(";\n");
            } else {
                builder.append(",");
            }
            if (i != length - 1 && i % 6 == 0) {
                builder.append("\n");
            }
        }
        //Field的属性
        builder.append("private int index;\n");
        builder.append("private String name;\n");
        //Field的构造方法
        builder.append("Field(int index,String name) {\n" +
                "            this.index = index;\n" +
                "            this.name = name;\n" +
                "        }\n" +
                "\n" +
                "        public int getIndex() {\n" +
                "            return index;\n" +
                "        }\n" +
                "        public String getName(){\n" +
                "            return name;\n" +
                "        }\n" +
                "\n" +
                "        @Override\n" +
                "        public String toString() {\n" +
                "            return name;\n" +
                "        }\n" +
                "    };\n");
        return builder.toString();
    }

    public static String endFile(String[] nameArr, String[] typeArr, String className) {
        StringBuilder builder = new StringBuilder();
        builder.append("public static final String[] _All_FIELDS = {");
        int length = nameArr.length;
        //_All_FIELDS
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                builder.append("\"" + nameArr[i] + "\"};\n");
            } else {
                builder.append("\"" + nameArr[i] + "\",");
            }
        }
        //getFieldsCount
        builder.append("@Override\n");
        builder.append("public int getFieldsCount() {\n" +
                "            return " + className + "._All_FIELDS.length;\n" +
                "        }\n");

        for (int i = 0; i < length; i++) {
            //<--private int code-->;
            builder.append("private " + changeType(typeArr[i]) + " " + nameArr[i] + ";\n");
        }
        //getSchema
        builder.append("@Override\n" +
                "    public Schema getSchema() {\n" +
                "        return SCHEMA$;\n" +
                "    }\n");
        //get
        builder.append("@Override\n");
        builder.append("public Object get(int field$) {\n");
        builder.append("switch (field$) {\n");
        for (int i = 0; i < length; i++) {
            builder.append("case " + i + ":\nreturn " + nameArr[i] + ";\n");
        }
        builder.append("default:\nthrow new AvroRuntimeException(\"Bad index\");\n");
        builder.append("}\n}\n");
        //put
        builder.append("@Override\n");
        builder.append("public void put(int field$, Object value) {\n");
        builder.append("switch (field$) {\n");
        for (int i = 0; i < length; i++) {
            if (typeArr[i].contains("Map") || typeArr[i].contains("List")) {
                String temp = "Map";
                if (typeArr[i].contains("List")) {
                    temp = "List";
                }
                builder.append("case " + i + ":\n" + nameArr[i] + " = (" + typeArr[i] + ") ((value instanceof Dirtyable) ? value : new Dirty"+temp+"Wrapper((" + temp + ")value));\nbreak;\n");
            } else {
                builder.append("case " + i + ":\n" + nameArr[i] + " = (" + typeArr[i] + ") value;\nbreak;\n");
            }
        }
        builder.append("default:\nthrow new AvroRuntimeException(\"Bad index\");\n");
        builder.append("}\n}\n");
        //get set isDirty
        for (int i = 0; i < length; i++) {
            builder.append(codeHelp(nameArr[i], typeArr[i], i));
        }

        //build static
        builder.append("public static "+className+".Builder newBuilder() {\n" +
                "        return new Builder();\n" +
                "    }\n" +
                "\n" +
                "    public static "+className+".Builder newBuilder("+className+".Builder other) {\n" +
                "        return new "+className+".Builder(other);\n" +
                "    }\n" +
                "\n" +
                "    public static "+className+".Builder newBuilder("+className+" other){\n" +
                "        return new "+className+".Builder(other);\n" +
                "    }\n");
        //class build
        builder.append(" public static class Builder extends SpecificRecordBuilderBase<"+className+"> implements RecordBuilder<"+className+"> {\n");
        for (int i = 0; i < length; i++) {
            //<--private int code-->;
            builder.append("private " + changeType(typeArr[i]) + " " + nameArr[i] + ";\n");
        }
        builder.append("private Builder(){\n" +
                "            super("+className+".SCHEMA$);\n" +
                "        }");
        builder.append(" private Builder("+className+".Builder other) {\n" +
                "            super(other);\n" +
                "        }");
        builder.append("private Builder("+className+" other) {\n");
        builder.append("super(SCHEMA$);\n");
        for(int i=0;i<length;i++){
            builder.append(codeHelp1(nameArr[i],typeArr[i],i));
        }
        builder.append(" }\n");
        //get set has clear
        for(int i=0;i<length;i++) {
            builder.append(codeHelp2(nameArr[i],typeArr[i],i,className));
        }

        //build
        builder.append("  @Override\npublic "+className+" build() {\n");
        builder.append(" try {\n "+className+" record = new "+className+"();\n");
        for(int i=0;i<length;i++){
            builder.append(codeHelp3(nameArr[i],typeArr[i],i));
        }
        builder.append("  return record;\n" +
                "           }catch (Exception e) {\n" +
                "               throw new AvroRuntimeException(e);\n" +
                "           }\n" +
                "        }\n" +
                "    }\n");
        //build class 结束
        //TOMBSTONE class
        builder.append(" @Override\n" +
                "    public Tombstone getTombstone() {\n" +
                "        return TOMBSTONE;\n" +
                "    }\n");
        builder.append(" @Override\n" +
                "    public Persistent newInstance() {\n" +
                "        return newBuilder().build();\n" +
                "    }\n");

        builder.append("private static final Tombstone TOMBSTONE = new Tombstone();\n");
        builder.append(" public static final class Tombstone extends" +
                " "+className+" implements org.apache.gora.persistency.Tombstone {\n");
        builder.append("private Tombstone() {}\n");
        for(int i=0;i<length;i++){
            builder.append(codeHelp4(nameArr[i],typeArr[i]));
        }
        builder.append("    }\n" +
                "}");
        //整个类结束
        return builder.toString();
    }
    private static List<String> typeList = Arrays.asList("Integer","Float","Double","Long");
    private static String changeType(String type) {
        switch (type) {
            case "Integer":
                return "int";
            case "Float":
                return "float";
            case "Double":
                return "double";
            case "Long":
                return "long";
            default:
                return type;
        }
    }

    public static String codeHelp4(String name,String type) {
        char[] tempArray = name.toCharArray();
        tempArray[0] = (char) (tempArray[0] - 32);
        String nameUp = new String(tempArray);
        StringBuilder result = new StringBuilder();
        result.append("@Override\n");
        result.append("public "+type+" get"+nameUp+"() {\n");
        result.append("throw new UnsupportedOperationException(\"Get is not supported on tombstones\");\n}\n\n");

        result.append("@Override\n");
        result.append("public void set"+nameUp+"("+type+" value) {\n");
        result.append("throw new UnsupportedOperationException(\"Set is not supported on tombstones\");\n}\n\n");

        result.append("@Override\n");
        result.append("public boolean is"+nameUp+"Dirty("+type+" value){\n");
        result.append("throw new UnsupportedOperationException(\"IsDirty is not supported on tombstones\");\n}\n\n");
        return result.toString();
    }



    //record
    public static String codeHelp3(String name,String type,int value) {
        if (type.contains("Map") || type.contains("List")) {
            String temp = "Map";
            if (type.contains("List")) {
                temp = "List";
            }
            return "record." +name+" = fieldSetFlags()["+value+"] ? this." +name + " : (" + type +") new Dirty"+temp+"Wrapper(("+temp+")defaultValue(fields()["+value+"]));\n" ;
        }else {
            return "record." +name+" = fieldSetFlags()["+value+"] ? this." +name + " : (" + type +")defaultValue(fields()["+value+"]);\n" ;
        }

    }

    //get set has clear
    public static String codeHelp2(String name,String type,int value,String className) {
        char[] tempArray = name.toCharArray();
        tempArray[0] = (char) (tempArray[0] - 32);
        String nameUp = new String(tempArray);
        StringBuilder result = new StringBuilder();
        result.append("public " + type + " get" + nameUp + "() {\n");
        result.append(" return " + name + ";\n}\n\n");

        result.append("public com.nutch.storage."+className+".Builder set" + nameUp + "(" + type + " " + name + ") {\n");
        result.append("validate(fields()["+value+"], "+name+");\n");
        result.append("this." + name + " = " + name + ";\n");
        result.append("fieldSetFlags()["+value+"] = true;\n");
        result.append("return this;\n}\n\n");

        result.append("public boolean has" +nameUp+ "() {\n");
        result.append("return fieldSetFlags()["+value+"];\n}\n\n");

        result.append("public com.nutch.storage."+className+".Builder clear" + nameUp + "() {\n");
        if(!typeList.contains(type)){
            result.append(name + " = null;\n");
        }
        result.append("fieldSetFlags()["+value+"] = false;\n");
        result.append("return this;\n}\n\n");
        return result.toString();

    }

    //build 的私有化构造方法
    public static String codeHelp1(String name, String type, int value) {
        StringBuilder builder = new StringBuilder();
        builder.append("if(isValidValue(fields()["+value+"],other."+name+")) {\n");
        builder.append(" this."+name+" = ("+type+") data().deepCopy(fields()["+value+"].schema(),other."+name+");\n");
        builder.append("fieldSetFlags()["+value+"] = true;\n}\n\n");
        return builder.toString();
    }

    //set get is
    public static String codeHelp(String name, String type, int value) {
        char[] tempArray = name.toCharArray();
        tempArray[0] = (char) (tempArray[0] - 32);
        String nameUp = new String(tempArray);
        StringBuilder result = new StringBuilder();
        result.append("public " + type + " get" + nameUp + "() {\n");
        result.append(" return " + name + ";\n}\n\n");

        result.append("public void set" + nameUp + "(" + type + " " + name + ") {\n");
        if (type.contains("Map") || type.contains("List")) {
            result.append("this." + name + " = (" + name + " instanceof org.apache.gora.persistency.Dirtyable) ? " + name + "\n" +
                    "        : new org.apache.gora.persistency.impl.DirtyMapWrapper(" + name + ");\n");
        } else {
            result.append("this." + name + " = " + name + ";\n");
        }
        result.append("setDirty(" + value + ");\n}\n\n");

        result.append("public boolean is" + nameUp + "Dirty(" + type + " " + name + ") {\n");
        result.append("return isDirty(" + value + ");\n}\n\n");
        return result.toString();

    }
}
