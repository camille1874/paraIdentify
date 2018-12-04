import java.util.List;
import java.util.Map;

/**
 * Created by xuzh on 2018/11/30.
 */
public class ParaSearcher {
    ParaSearcher() {

    }

    public static Map<String, Object> searchParas(int size, String triggerSentence) {
        long t1 = System.currentTimeMillis();
        List<Map<String, Object>> docs = ESUtil.searchDocs(size, triggerSentence);
        long t2 = System.currentTimeMillis();
        System.out.println("查询十篇文章时间" + (t2 - t1) / 1000.0 + "s");
        Map<String, Object> result = searchSentences(docs, triggerSentence);
        long t3 = System.currentTimeMillis();
        System.out.println("查询最相似句子时间" + (t3 - t2) / 1000.0 + "s");
        return result;
    }

    private static Map<String, Object> searchSentences(List<Map<String, Object>> res, String triggerSentence) {
        double min = Integer.MAX_VALUE;
        String tmp = "";
        Map<String, Object> result;
        if (res.size() > 0) {
            result = res.get(0);
        } else {
            return null;
        }
        for (Map<String, Object> source : res) {
            String textcontent = (String) source.get("textcontent");
            if (textcontent == null)
                continue;
            textcontent = textcontent.trim();
            List<String> sentences = TextUtil.getSentences(textcontent);
            for (String s : sentences) {
                double dis = TextUtil.getEditDistance(TextUtil.cleanStr(s), triggerSentence);
                if (dis < min) {
                    min = dis;
                    result = source;
                    tmp = s;
                }
            }
        }
        String resultContent = (String) result.get("textcontent");
        try {
            int idx = resultContent.indexOf(tmp);
            int start = Integer.max(0, resultContent.lastIndexOf("。", Integer.max(0, idx - 2)) + 1);
            int end = Integer.min(resultContent.length(),
                    resultContent.indexOf("。", idx + tmp.length() + 1) + 1);
            String content = resultContent.substring(start, end);
            result.put("content", TextUtil.cleanStr(content));
        } catch (Exception e) {
            result.put("content", TextUtil.cleanStr(tmp));
        }
        result.put("target", TextUtil.cleanStr(tmp));
        return result;
    }

    public static void main(String[] args) {
        ESUtil.getInstance();
        String[] data = {
                "对于新一代奥迪A6L来说，能够让人眼前一亮的外观，是让人一眼区分出" +
                        "它与现款车型不同的关键改变，平直犀利、棱角分明的车身线条" +
                        "一改此前庄严肃穆的官车形象，这样的变化令全新奥迪A6L不光" +
                        "可以胜任豪华行政用途，更让它成为了一款有温度的运动家轿",
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
        for (String s:data) {
            String target = (String) searchParas(10, s).get("target");
            System.out.println("原句：");
            System.out.println(s);
            System.out.println("目标句：");
            System.out.println(target);
            System.out.println();
        }
    }
}
