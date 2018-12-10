package com.giiso.text.service;

import com.giiso.config.SystemPropertiesUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

/**
 * Created by xuzh on 2018/11/26.
 */
public class TextUtil {
    public static double[][] getEditDistanceMatrix(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        double[][] distance = new double[m + 1][n + 1];
        for (int i = 0; i <= m; ++i) {
            for (int j = 0; j <= n; ++j) {
                if (i == 0) {
                    distance[i][j] = j;
                    continue;
                }
                if (j == 0) {
                    distance[i][j] = i;
                    continue;
                }
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    distance[i][j] = distance[i - 1][j - 1];
                } else {
                    distance[i][j] = min(min(distance[i - 1][j], distance[i][j - 1]), distance[i - 1][j - 1]) + 1;
                }
            }
        }
        return distance;
    }

    public static double getEditDistance(String s, String triggerSentence) {
        double result = Double.MAX_VALUE;
        double result_left;
        double result_right;
        double result_raw;
        int strLen = s.length();
        int triggerLen = triggerSentence.length();
        int testLen = 30;
        int defaultLen = 40;
        int pos = defaultLen - testLen;
        if (s.length() != 0) {
            String strLeft = s.substring(0, min(testLen, strLen));
            String triggerLeft = triggerSentence.substring(0, min(testLen, triggerLen));
            String strRight = s.substring((strLen > pos) ? pos: 0, strLen);
            String triggerRight = triggerSentence.substring((triggerLen > pos) ? pos: 0, triggerLen);
            result_left = getEditDistanceMatrix(strLeft, triggerLeft)[strLeft.length()][triggerLeft.length()] / strLeft.length();
            result_right = getEditDistanceMatrix(strRight, triggerRight)[strRight.length()][triggerRight.length()] / strRight.length();
            result_raw = getEditDistanceMatrix(s, triggerSentence)[strLen][triggerLen] / strLen;
            result = min(result_raw, min(result_left, result_right));
        }
        return result;
//        double result = Double.MAX_VALUE;
//        if (s.length() != 0) {
//            result = getEditDistanceMatrix(s, triggerSentence)[s.length()][triggerSentence.length()] / s.length();
//        }
//        return result;
    }

    public static List<String> processTriggerSentence(String text) {
        List<String> result = new ArrayList<>();
        List<String> sens = getSentences(text);
        int minlength = Integer.valueOf(SystemPropertiesUtils.getString("minlength.value", "15"));
        int maxlength = Integer.valueOf(SystemPropertiesUtils.getString("maxlength.value", "40"));

        for (String s : sens) {
            if (s.length() > maxlength) {
                String[] tmp = s.split("[，,]");
                int i = 0;
                for (; i < tmp.length - 1; i += 2) {
                    result.add(tmp[i] + "，" + tmp[i + 1]);
                }
                if (i == tmp.length - 1) {
                    result.add(tmp[i] + "，");
                }
            } else if (s.length() > minlength){
                result.add(s);
            }
        }
        return result;
    }

    public static List<String> getSentences(String textcontent) {
//        List<String> result = new ArrayList<>();
//         不采用，对文章长句按两句断句容易因断句不当错失最佳片段
//        for (String s : textcontent.split("[。|！|?|；|;|？|?|“|”|\"]")) {
//            if (s.length() < 40) {
//                result.add(s);
//            } else {
//                String[] tmp = s.split("，");
//                int i = 0;
//                for (; i < tmp.length - 1; i += 2) {
//                    result.add(tmp[i] + "，" + tmp[i + 1]);
//                }
//                if (i == tmp.length - 1) {
//                    result.add(tmp[i] + "，");
//                }
//            }
        return Arrays.asList(textcontent.split("[。|！|!|?|；|;|？|?|“|”|\"]"));
    }

    public static String cleanStr(String str) {
        String regEx = "[`~@#$%^&*()+=|{}':;'//[//]<>/~@#￥%&*（）——+|{}【】‘；：”“’\"\'\\s*|\t|\r|\n]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").replaceAll("[ 　  ]", ""); //去掉全角空格等
    }
}
