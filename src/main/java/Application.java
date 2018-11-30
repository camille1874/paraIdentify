import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.servlet.SparkApplication;

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
//        post("/paraIdentify", "application/json", (request, response) -> {
//            JsonParser parser = new JsonParser();
//            JsonObject jsonObject = parser.parse(request.body()).getAsJsonObject();
//            String triggerSentence = TextUtil.cleanStr(String.valueOf(jsonObject.get("triggerSentence")));
//            String triggerSentence = TextUtil.cleanStr(request.body());
            String triggerSentence = TextUtil.cleanStr(request.queryParams("triggerSentence"));
            System.out.println(triggerSentence);
            Map<String, Object> result = ParaSearcher.searchParas(10, triggerSentence);
            return new Gson().toJson(result);
        });
    }

    public static void main(String[] args) {
        Application a = new Application();
        a.init();
    }
}

