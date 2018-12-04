import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by giiso on 2018/11/30.
 */
public class test4J {
    public static void main(String[] args) {
        String[] data = {
                "18款奔驰GLS450表面计划充分融会了奔驰家属旗下 SUV和轿跑车的特征，" +
                        "车内乘坐空间方面，GLS的表示使人满足。" +
                        "身高183cm的体验者进入前排，头部另有一拳余量。" +
                        "车内为三排七座计划，所有座椅均由真皮打造。" +
                        "内饰还将有更多个性配色可供车主选择。",

                "侧面来看区别会十分明显，三厢版普通自然的造型，" +
                        "两厢版的侧面会有些争议。" +
                        "2019 款马自达3两厢版后门的车窗相对较短，" +
                        "并且采用上扬的设计，有点类似于丰田C - HR的风格。",

                "马自达作为一家小众且执拗的品牌，网友对其评价比较高，" +
                        "但真正要掏钱买车的却不多，这就是所谓的叫好不叫座。" +
                        "马自达3是其主力车型，呼声自然会比较高，" +
                        "今日，新一代马自达3官图终于来了。",

                "动力也有很大的变化，2019 款马自达3将搭载带有SPCCI火花塞" +
                        "控制压燃点火技术的新一代创驰蓝天SKYACTIV - X发动机，" +
                        "外媒称其2 .0 L排量版本机型的最大功率和峰值扭矩" +
                        "或分别达到189马力和230牛·米左右。",

                "为了让GTC敞篷版能够呈现出完美的姿态，除了车身烤漆以外，" +
                        "宾利还在新车全铝合金属蒙皮上采用了超塑成型技术，" +
                        "其亮点是将铝质材料高温加热至500摄氏度，再利用空气压力" +
                        "让铝材在外观模具中定型， 正是这种源自航空航天的技术为" +
                        "GTC敞篷版打造出复杂且锋利的表面线条。",

                "宾利全新欧陆GTC敞篷版拥有纯正的GT跑车造型，将古典与现代完美结合，" +
                        "并且新车的细节线条十分考究，在呈现出强烈视觉冲击力的背后，" +
                        "是诸多高科技制造工艺的加持，另外丰富的外观烤漆和顶棚颜色搭配，" +
                        "也让新车呈现出截然不同的情调， 如果你对此还不满意，" +
                        "那么宾利还可为你进行独特定制属于你独一无二的外观风格。"};
        Gson gson = new Gson();
        for (int i = 0; i < data.length; i++) {
            String resp = HttpRequest.post("http://localhost:21628/paraIdentify").form("triggerSentence", data[i]).body();
            JsonObject responseJson = new JsonParser().parse(resp).getAsJsonObject();
            Type type = new TypeToken<Map<String, Map<String, Object>>>() {
            }.getType();
            Map<String, Map<String, Object>> result = gson.fromJson(responseJson, type);
            Iterator<Map.Entry<String, Map<String, Object>>> iter = result.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = iter.next();
                String sourceSentence = entry.getKey();
                Map<String, Object> target = entry.getValue();
                String targetSentence = (String) target.get("target");
                String title = (String) target.get("title");
                String content = (String) target.get("content");
                String url = (String) target.get("url");
                String time = (String) target.get("time");
                System.out.println("原句：" + sourceSentence);
                System.out.println("相似句：" + targetSentence);
                System.out.println("文章：" + title);
                System.out.println("URL：" + url);
                System.out.println("时间：" + time);
                System.out.println("相似内容片段：" + content);
            }
            System.out.println();
        }
    }
}

