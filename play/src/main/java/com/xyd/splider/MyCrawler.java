package com.xyd.splider;

import java.util.Set;

/**
 * 爬虫主程序
 * Created by DXM_0020 on 2017/5/26.
 */
public class MyCrawler {

    /**
     * 使用种子初始化 URL 队列
     * @param seeds seeds 种子 URL
     */
    private void initCrawlerWithSeeds(String[] seeds){
        for(String seed : seeds){
            LinkQueue.addUnVisitedUrl(seed);
        }
    }

    /**
     * 执行抓取过程
     * @param seeds
     */
    public  void crawling(String[] seeds){
        //1.定义过滤的URL，只属于这个网站的接受。可改进为数组。
        LinkFilter linkFilter = new LinkFilter() {
            @Override
            public boolean accept(String url) {
                if(url.startsWith(SpliederConfiger.acceptUrl)){
                    return true;
                }
                return false;
            }
        };
        //2.初始化种子url
        initCrawlerWithSeeds(seeds);
        //3.循环条件：待抓取的链接不空且抓取的网页不多于 1000
        while (!LinkQueue.unVisitedUrlIsEmpty() && LinkQueue.getVisitedUrlNum()<=SpliederConfiger.num){
            //出队列
            String url = (String) LinkQueue.unVisitedUrlDeQueue();
            if(url == null){
                continue;
            }
            DownloadFile downloadFile = new DownloadFile();
            //下载网页
            downloadFile.downLoadFile(url);
            //放入访问过的URL
            LinkQueue.addVisitedUrl(url);
            //提取出下载页中的URL
            Set<String> urlSet = HtmlParserTool.extracLinks(url,linkFilter);
            for(String newUrl : urlSet){
                LinkQueue.addUnVisitedUrl(newUrl);
            }
        }
    }

    //程序入口
    public static void main(String[] args){
        MyCrawler myCrawler = new MyCrawler();
        myCrawler.crawling(new String[]{SpliederConfiger.seedUrl});
    }
}
