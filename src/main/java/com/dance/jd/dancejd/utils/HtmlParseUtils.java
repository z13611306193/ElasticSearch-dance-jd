package com.dance.jd.dancejd.utils;

import com.dance.jd.dancejd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZYGisComputer
 */
@Component
public class HtmlParseUtils {

    public static void main(String[] args) throws Exception {

        new HtmlParseUtils().searchByJd("c++").forEach(System.out::println);

    }

    public List<Content> searchByJd(String keywords) throws Exception{
        // 获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keywords;

        // 解析网页
        Document document = Jsoup.parse(new URL(url), 30000);

        // 获取到京东的商品框
        Element element = document.getElementById("J_goodsList");

        // 获取其中所有的li
        Elements lis = element.getElementsByTag("li");

        ArrayList<Content> contents = new ArrayList<>();

        for (Element li : lis) {
            // 获取图片
            String imgSrc = li.getElementsByTag("img").get(0).attr("src");
            // 获取价格
            String price = li.getElementsByClass("p-price").get(0).text();
            // 获取标题
            String title = li.getElementsByClass("p-name").get(0).text();

            Content content = new Content(title,imgSrc,price);

            contents.add(content);

//            System.out.println("===========================");
//            System.out.println(imgSrc);
//            System.out.println(price);
//            System.out.println(title);
        }

//        System.out.println(element.html());
        return contents;
    }

}
