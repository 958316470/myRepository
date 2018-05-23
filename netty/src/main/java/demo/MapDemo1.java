package demo;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 95831
 * 测试高德地图地理编码使用
 */
public class MapDemo1 {
    public static final String MAP_KEY = "8fa5cf964a426d804fcc5c8e20159f0d";
    public static final String MAP_ADDRESS = "http://restapi.amap.com/v3/geocode/geo?" +
            "address=INPUT_ADDRESS&output=XML&key=USER_KEY";

    public static void main(String[] args) {
       new MapDemo1().testJson();
    }

    public void getLocationStr(){
        String address = "北京市西城区西直门西环广场";
        String url = MAP_ADDRESS.replace("INPUT_ADDRESS",address).replace("USER_KEY",MAP_KEY);
        Call call = OkHttpClientUtil.toJsonStringGetSend(url);
        Response response;
        try {
            response = call.execute();
            if (response!=null&&response.isSuccessful()){
                System.out.println(response.body().string());
            }else {
                System.out.println("失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJson() {
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "    <status>1</status>\n" +
                "    <info>OK</info>\n" +
                "    <infocode>10000</infocode>\n" +
                "    <count>1</count>\n" +
                "    <geocodes type=\"list\">\n" +
                "        <geocode>\n" +
                "            <formatted_address>北京市西城区西环广场|T|2座</formatted_address>\n" +
                "            <province>北京市</province>\n" +
                "            <citycode>010</citycode>\n" +
                "            <city>北京市</city>\n" +
                "            <district>西城区</district>\n" +
                "            <township></township>\n" +
                "            <neighborhood>\n" +
                "                <name></name>\n" +
                "                <type></type>\n" +
                "            </neighborhood>\n" +
                "            <building>\n" +
                "                <name></name>\n" +
                "                <type></type>\n" +
                "            </building>\n" +
                "            <adcode>110102</adcode>\n" +
                "            <street></street>\n" +
                "            <number></number>\n" +
                "            <location>116.352425,39.941568</location>\n" +
                "            <level>门牌号</level>\n" +
                "        </geocode>\n" +
                "    </geocodes>\n" +
                "</response>";

        int start = xmlStr.indexOf("<location>");
        int end = xmlStr.indexOf("</location>");
        String location = xmlStr.substring(start + "<location>".length(),end);
        System.out.println(location);
    }

    /**
     * 标签唯一的时候适合使用
     *
     * @param xml
     * @param sign
     * @return
     */
    public String getXmlContent(String xml, String sign) {
        if (!xml.contains(sign)) {
            return "";
        }
        int start = xml.indexOf("<"+sign+">");
        int end = xml.indexOf("</"+sign+">");
        return xml.substring(start + ("<"+sign+">").length(),end);
    }
}
