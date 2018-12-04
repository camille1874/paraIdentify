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
            Properties pro = new Properties();
            try {
                pro.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("es_config.properties"));
                String username = pro.getProperty("username");
                String password = pro.getProperty("password");
                String prefix = pro.getProperty("prefix");
                logger.info("Is connecting to ES.");
                client = TianjiNewsClientFactory.initInstance(username, password, prefix);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public static List<Map<String, Object>> searchDocs(int size, String triggerSentence) {
        List<Map<String, Object>> res = null;
        try {
            List<AnalyzeResponse.AnalyzeToken> words = client.analyze(triggerSentence, "nlp_query");
            List<String> searchWords = new ArrayList<>();
            for (AnalyzeResponse.AnalyzeToken w: words)
            {
                searchWords.add(w.getTerm());
            }
//            System.out.println(searchWords);
            res = client.search(0, termsQuery("textcontent", searchWords), null, size, "title", "textcontent", "time", "url");
//            res = client.search(0, buildMatchQuery(triggerSentence), null, size, "title", "textcontent", "time", "url");
        } catch (IOException e) {
            return null;
        }
        return res;
    }

    private static QueryBuilder buildMatchQuery(String query) {
        BoolQueryBuilder bqb = new BoolQueryBuilder();
        bqb.should(QueryBuilders.matchQuery("textcontent", query));
        return bqb;
    }
}
