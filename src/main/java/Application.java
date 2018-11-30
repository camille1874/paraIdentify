import spark.servlet.SparkApplication;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.*;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Created by giiso on 2018/11/30.
 */
public class Application implements SparkApplication {
    @Override
    public void init() {
        port(21628);
        post("/paraIdentify", "application/json", (request, response) -> {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(request.body()).getAsJsonObject();
            String triggerSentence = TextUtil.cleanStr(String.valueOf(jsonObject.get("triggerSentence")));
//            String result = (String) ParaSearcher.searchParas(10, triggerSentence).get("target");
            System.out.println(triggerSentence);
            Map<String, Object> result = ParaSearcher.searchParas(10, triggerSentence);
            return new Gson().toJson(result);
        });
    }

    public static void main(String[] args){
        Application a = new Application();
        a.init();
    }
}

