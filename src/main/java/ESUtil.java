package com.giiso.text.service;
import com.giiso.config.SystemPropertiesUtils;
import com.giiso.elasticsearch.TianjiNewsClient;
import com.giiso.elasticsearch.TianjiNewsClientFactory;
import com.giiso.elasticsearch.visitlog.RestLogConsumer;
import com.giiso.keywords.textrank.TextRankForIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public static List<Map<String, Object>> searchDocs(String[] tags, int size, String triggerSentence, List<String> keywords) {
        List<Map<String, Object>> res = null;
        try {
//            List<AnalyzeResponse.AnalyzeToken> words = client.analyze(triggerSentence, "nlp_query");
//            List<String> searchWords = new ArrayList<>();
//            for (AnalyzeResponse.AnalyzeToken w: words)
//            {
//                searchWords.add(w.getTerm());
//            }
            res = getInstance().search(0, buildMatchQuery(tags, triggerSentence, keywords), null, size, "title", "textcontent", "time", "url", "site");
////            res = client.search(0, buildMatchQuery(triggerSentence), null, size, "title", "textcontent", "time", "url");
        } catch (IOException e) {
            return null;
        }
        return res;
    }

    private static QueryBuilder buildMatchQuery(String[] tags, String query, List<String> keywords) {
        BoolQueryBuilder bqb = new BoolQueryBuilder();
        bqb.must(QueryBuilders.termsQuery("textcontent", keywords)).must(QueryBuilders.termsQuery("tags", tags));
        return bqb;
    }
}
