import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giiso on 2018/11/30.
 */
public class test4J {
    public static void main(String[] args)
    {
        String data = "深圳天气怎样";
//        data.put("triggerSentence", "深圳天气怎样");
        String resp = HttpRequest.post("http://localhost:21628/paraIdentify").form("triggerSentence", data).body();
        JsonObject responseJson = new JsonParser().parse(resp).getAsJsonObject();;
        String result = String.valueOf(responseJson.get("target"));
        System.out.println(result);
    }
}

