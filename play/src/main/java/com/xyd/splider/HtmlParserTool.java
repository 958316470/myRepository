package com.xyd.splider;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.HashSet;
import java.util.Set;

/**
 * 解析HTML页面中的URL
 * Created by DXM_0020 on 2017/5/26.
 */
class HtmlParserTool {

    static Set<String > extracLinks(String url, LinkFilter filter){
        Set<String> links = new HashSet<>();
        try {
            Parser parser = new Parser(url);
            parser.setEncoding(SpliederConfiger.encoding);
            NodeFilter nodeFilter = (NodeFilter) node -> {
                // 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性
                return node.getText().startsWith(SpliederConfiger.frameStart);
            };
            // OrFilter 来设置过滤 <a> 标签和 <frame> 标签
            OrFilter orFilter = new OrFilter(new NodeClassFilter(LinkTag.class),nodeFilter);
            // 得到所有经过过滤的标签
            NodeList nodeList = parser.extractAllNodesThatMatch(orFilter);
           for(int i=0;i<nodeList.size();i++){
               Node node = nodeList.elementAt(i);
               if(node instanceof LinkTag){// <a> 标签
                    LinkTag tag = (LinkTag) node;
                   String linkUrl = tag.getLink();
                   if(filter.accept(linkUrl)){
                       links.add(linkUrl);
                   }
               }else{// frame 标签
                   // 提取 frame 里 src 属性的链接，如 <frame src="test.html"/>
                   String frame = node.getText();
                   int start = frame.indexOf(SpliederConfiger.srcTag);
                   frame = frame.substring(start);
                   int end = frame.indexOf(" ");
                   if(end == -1){
                       end = frame.indexOf(">");
                   }
                   String frameUrl = frame.substring(5,end-1);
                   if(filter.accept(frameUrl)){
                       links.add(frameUrl);
                   }
               }
           }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return links;
    }
}
