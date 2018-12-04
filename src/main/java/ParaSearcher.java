import java.util.List;
import java.util.Map;

/**
 * Created by xuzh on 2018/11/30.
 */
public class ParaSearcher {
    ParaSearcher() {

    }

    public static Map<String, Object> searchParas(int size, String triggerSentence) {
        return searchSentences(ESUtil.searchDocs(size, triggerSentence), triggerSentence);
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

//            double dis = TextUtil.getEditDistance(textcontent, triggerSentence);
//            double dis = TextUtil.getWord2VecDistance(textcontent, triggerSentence);

//            double dis = TextUtil.getEditDistance(textcontent, triggerSentence);
//            if (dis < min)
//            {
//                min = dis;
//                result = source;
//            }

            int idx = textcontent.indexOf(tmp);
            int start = Integer.max(0, textcontent.lastIndexOf("。", Integer.max(0, idx - 2)) + 1);
            int end = Integer.min(textcontent.length(),
                    textcontent.indexOf("。", idx + tmp.length() + 1) + 1);
            String content = textcontent.substring(start, end);
            if (content.indexOf(tmp) >= 0) {
                result.put("content", TextUtil.cleanStr(content));
            } else {
                result.put("content", TextUtil.cleanStr(tmp));
            }
            result.put("target", TextUtil.cleanStr(tmp));
        }
        return result;
    }

    public static void main(String[] args) {
        ESUtil.getInstance();
        String target = (String) searchParas(10, "深圳天气怎样").get("target");
        System.out.println(target);
    }
}
