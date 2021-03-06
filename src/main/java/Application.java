import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.servlet.SparkApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Created by xuzh on 2018/11/30.
 */
public class Application implements SparkApplication {
    @Override
    public void init() {
        ESUtil.getInstance();
        port(21628);
        post("/paraIdentify", (request, response) -> {
            String triggerSentence = TextUtil.cleanStr(request.queryParams("triggerSentence"));
            System.out.println("查询内容：" + triggerSentence);
            List<String> seggedSentences = TextUtil.getSentences(triggerSentence);
            Map<String, Map<String, Object>> result = new HashMap<>();
            long start = System.currentTimeMillis();
            for (String s : seggedSentences) {
                result.put(s, ParaSearcher.searchParas(10, s));
            }
            long end = System.currentTimeMillis();
            System.out.println("分句数量：" + seggedSentences.size());
            System.out.println("处理时间：" + (end - start) / 1000.0 + "s");
            return new Gson().toJson(result);
        });
    }

    public static void main(String[] args) {
        Application a = new Application();
        a.init();
    }
}

