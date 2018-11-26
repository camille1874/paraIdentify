import com.giiso.elasticsearch.TianjiNewsClient;
import com.giiso.elasticsearch.TianjiNewsClientFactory;
import com.giiso.elasticsearch.visitlog.RestLogConsumer;
import com.giiso.keywords.textrank.TextRankForIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by xuzh on 2018/11/23.
 */
public class ESUtil {
    private static Logger logger = LogManager.getLogger(ESUtil.class);
    private static TianjiNewsClient client = connect();

    public static TianjiNewsClient connect() {
        Properties pro = new Properties();
        try {
            pro.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("es_config.properties"));
            String rest = pro.getProperty("log.rest");
            String username = pro.getProperty("username");
            String password = pro.getProperty("password");
            String prefix = pro.getProperty("prefix");
            logger.info("Is connecting to ES.");
            TianjiNewsClient client = TianjiNewsClientFactory.initInstance(username, password, prefix);
            client.setLogConsumer(new RestLogConsumer(rest));

            return client;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TianjiNewsClient getInstance() {
        return client;
    }

    public static Map<String, Object> searchParas(int size, String triggerSentence) {
        return searchSentences(searchDocs(size, triggerSentence), triggerSentence);
    }

    public static List<Map<String, Object>> searchDocs(int size, String triggerSentence) {
        List<Map<String, Object>> res;
        while (true) {
            try {
                res = client.search(0, buildMatchQuery(triggerSentence), null, size, "title", "textcontent");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return res;
    }

    private static Map<String, Object> searchSentences(List<Map<String, Object>> res, String triggerSentence) {
        double min = Integer.MAX_VALUE;
        String tmp = "";
        Map<String, Object> result;
        if (res.size() > 0)
        {
            result = res.get(0);
        }
        else
        {
            return null;
        }
        for (Map<String, Object> source : res) {
            String textcontent = (String) source.get("textcontent");
            if (textcontent == null)
                continue;
            textcontent = textcontent.trim();
//            List<String> sentences = TextUtil.getSentences(textcontent);
//            for (String s: sentences)
//            {
//                double dis = TextUtil.getEditDistance(s, triggerSentence);
//                if (dis < min)
//                {
//                    min = dis;
//                    result = source;
//                    tmp = s;
//                }
//            }
            double dis = TextUtil.getEditDistance(textcontent, triggerSentence);
            if (dis < min)
            {
                min = dis;
                result = source;
            }
        }
        return result;
    }

    private static QueryBuilder buildMatchQuery(String query) {
        BoolQueryBuilder bqb = new BoolQueryBuilder();
//        bqb.should(QueryBuilders.matchPhraseQuery("textcontent", query));
//        bqb.should(QueryBuilders.queryStringQuery(query));
        bqb.should(QueryBuilders.matchQuery("textcontent", query));
        return bqb;
    }

    public static void main(String[] args) {
        System.out.println(searchParas(10, "深圳天气"));
    }
}
