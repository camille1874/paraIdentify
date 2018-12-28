package com.giiso.text.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.giiso.config.SystemPropertiesUtils;
import com.giiso.elasticsearch.ElasticSearchUtils;
import com.giiso.elasticsearch.TianjiNewsClient;
import com.giiso.elasticsearch.TianjiNewsClientFactory;
import com.giiso.elasticsearch.visitlog.RestLogConsumer;
import com.giiso.keywords.textrank.TextRankForIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilder;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;


/**
 * Created by xuzh on 2018/11/23.
 */
public class ESUtil {
    private static Logger logger = LogManager.getLogger(ESUtil.class);
    private static TianjiNewsClient client = null;

    private ESUtil() {
    }

    synchronized public static TianjiNewsClient getInstance() {
        if (client == null) {
            try {
                String username = SystemPropertiesUtils.getString("es.username");
                String password = SystemPropertiesUtils.getString("es.password");
                String prefix = SystemPropertiesUtils.getString("es.prefix");
                logger.info("Is connecting to ES.");
                client = TianjiNewsClientFactory.initInstance(username, password, prefix);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    // 重写search函数，处理查询不稳定问题
    public static List<Map<String, Object>> search(int days, QueryBuilder query, SortBuilder sort,
                                                   int size, String... fields) throws IOException {
        String[] index = getInstance().getIndexesContains(days);
        if (query == null)
            return null;
        SearchRequest request = ElasticSearchUtils.prepareSearch(index, "news", query, sort, size, fields);
//        request.preference("primary_first"); //无效
        SearchResponse res = client.search(request);
        SearchHits hits = res.getHits();
        List<Map<String, Object>> result = new ArrayList<>(hits.getHits().length);
        for (SearchHit hit : hits) {
            result.add(ElasticSearchUtils.parseHit(hit));
        }
        return result;
    }

    public static List<Map<String, Object>> searchDocs(String[] tags, int size, String triggerSentence, List<String> keywords) {
        List<Map<String, Object>> res;
        try {
//            List<AnalyzeResponse.AnalyzeToken> words = client.analyze(triggerSentence, "nlp_query");
//            List<String> searchWords = new ArrayList<>();
//            for (AnalyzeResponse.AnalyzeToken w: words)
//            {
//                searchWords.add(w.getTerm());
//            }

//            res  = search(0, buildMatchQuery(tags, triggerSentence, keywords), null, size, "title", "textcontent", "time", "url", "site");
//            List<Map<String, Object>> res2 = search(0, buildMatchQuery(tags, triggerSentence, keywords), null, size, "title", "textcontent", "time", "url", "site");
//            res.removeAll(res2);
//            res.addAll(res2);
//            缓解查询不稳定问题

            res = search(0, buildMatchQuery(tags, triggerSentence, keywords), null, size, "title", "textcontent", "time", "url", "site");

        } catch (IOException e) {
            return null;
        }
        return res;
    }

    private static QueryBuilder buildMatchQuery(String[] tags, String query, List<String> keywords) {
        BoolQueryBuilder bqb = new BoolQueryBuilder();
        bqb.must(QueryBuilders.termsQuery("tags", tags)).must(QueryBuilders.rangeQuery("time")
                .from("2017-06-01 00:00:00")).must(QueryBuilders.matchQuery("textcontent",
                StringUtils.join(keywords, " ")));
        return bqb;
    }
}
