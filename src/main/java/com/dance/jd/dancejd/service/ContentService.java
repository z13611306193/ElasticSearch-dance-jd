package com.dance.jd.dancejd.service;

import com.alibaba.fastjson.JSON;
import com.dance.jd.dancejd.pojo.Content;
import com.dance.jd.dancejd.utils.HtmlParseUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ZYGisComputer
 */
@Service
public class ContentService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private HtmlParseUtils htmlParseUtils;

    /**
     * 解析JD的数据存入ES中
     * @param keywords
     * @return
     * @throws Exception
     */
    public boolean parseContent(String keywords) throws Exception {
        // 爬取JD的索引
        List<Content> contents = htmlParseUtils.searchByJd(keywords);
        // 创建批量对象
        BulkRequest bulkRequest = new BulkRequest();
        // 加入到批量对象中
        for (int i = 0; i < contents.size(); i++) {
            // 随机生成ID
            bulkRequest.add(
              new IndexRequest("jd").source(JSON.toJSONString(contents.get(i)), XContentType.JSON)
            );
        }
        // 执行批量添加
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    /**
     * 根据关键词查询并分页
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> searchPage(String keyword,Integer pageNo,Integer pageSize) throws IOException {
        // 起始坐标判断
        if(pageNo<=1){
            pageNo = 1;
        }

        // 索引库定位
        SearchRequest jd = new SearchRequest("jd");

        // 构造条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 精确匹配
        TermQueryBuilder query = QueryBuilders.termQuery("title", keyword);

        searchSourceBuilder.query(query);

        //-- 添加高亮展示关键字 --
        // 高亮构建器
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        // 高亮字段
        highlightBuilder.field("title");

        // 高亮前缀标签
        highlightBuilder.preTags("<span style='color:red;'>");

        // 高亮后缀标签
        highlightBuilder.postTags("</span>");

        // 是否多个高亮
        highlightBuilder.requireFieldMatch(false);

        // 关联高亮
        searchSourceBuilder.highlighter(highlightBuilder);

        //-- 添加高亮展示关键字 --

        // 设置超时时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 分页
        searchSourceBuilder.from(pageNo);

        searchSourceBuilder.size(pageSize);

        jd.source(searchSourceBuilder);

        // 搜索
        SearchResponse search = restHighLevelClient.search(jd, RequestOptions.DEFAULT);

        ArrayList<Map<String,Object>> result = new ArrayList<>();

        // 结果添加
        for (SearchHit hit : search.getHits().getHits()) {

            // 原结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //--解析高亮的字段--

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField title = highlightFields.get("title");

            if(null != title){

                Text[] fragments = title.fragments();

                String n_title = "";

                for (Text text : fragments) {
                    n_title += text;
                }

                sourceAsMap.put("title",n_title);

            }

            //--解析高亮的字段--

            result.add(sourceAsMap);
        }

        return result;
    }

}
