package com.giiso.text.service;

import com.giiso.config.SystemPropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by xuzh on 2018/11/30.
 */
public class ParaSearcher {
    private static final Logger logger = LoggerFactory.getLogger(ParaSearcher.class);


    public static Map<String, Object> searchParas(String[] tags, int size, String triggerSentence, List<String> keywords) {
        if (triggerSentence == null || triggerSentence.length() == 0) {
            return null;
        }

        return searchSentences(ESUtil.searchDocs(tags, size, triggerSentence, keywords), triggerSentence);
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
//            System.out.println((String) source.get("_id") + source.get("title"));
            String textcontent = (String) source.get("textcontent");
            if (textcontent == null)
                continue;
            textcontent = textcontent.trim();
            List<String> sentences = TextUtil.processTriggerSentence(textcontent);
            for (String s : sentences) {
                double dis = TextUtil.getEditDistance(TextUtil.cleanStr(s), TextUtil.cleanStr(triggerSentence));
                if (dis < min) {
                    min = dis;
                    result = source;
                    tmp = s;
                }
            }
        }
        logger.info(String.valueOf(min));
        String resultContent = (String) result.get("textcontent");
        //不可以static，否则配置中心修改时，无法及时生效
        Double distanceValue = Double.valueOf(SystemPropertiesUtils.getString("distance.value", "0.6"));
        if (distanceValue.compareTo(min) < 0) {
            return null;
        } else {
            try {
                int idx = resultContent.indexOf(tmp);
//                int start = resultContent.lastIndexOf("。", Integer.max(0, idx - 2)) + 1;
//                int end = resultContent.indexOf("。", idx + tmp.length() + 1) + 1;
                Integer contentLengthValue = Integer.valueOf(SystemPropertiesUtils.getString("maxcontentlength.value", "30"));
                int start = Integer.max(0, idx - contentLengthValue);
                int end = Integer.min(resultContent.length(), idx + tmp.length() + contentLengthValue);
                if (idx == -1 || start < 0 || end > resultContent.length()) {
                    result.put("content", TextUtil.cleanStrTmp(tmp));
                } else {
                    String content = resultContent.substring(start, end);
                    result.put("content", TextUtil.cleanStrTmp(content));
                }
            } catch (Exception e) {
                return null;
            }
            result.put("target", TextUtil.cleanStrTmp(tmp));
        }
        if (result.get("target").equals("") || result.get("content").equals(""))
        {
            return null;
        }
        result.remove("_type");
        result.remove("_index");
        result.remove("textcontent");
        return result;
    }
}
