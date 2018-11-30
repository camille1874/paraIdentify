import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (s.length() != 0) {
            result = getEditDistanceMatrix(s, triggerSentence)[s.length()][triggerSentence.length()] / s.length();
        }
        return result;
    }

    // 编辑距离矩阵内元素相减
    // TODO:待检测
    public static Map<String, Double> getMinEditDistance(String doc, String sen) {
        double[][] distancesMatrix = getEditDistanceMatrix(doc, sen);
        double min = Double.MAX_VALUE;
        Map<String, Double> result = new HashMap<>();
        if (distancesMatrix.length == 0 || distancesMatrix[0].length == 0) {
            return result;
        }
        int m = doc.length();
        int n = sen.length();
        int begin = 0;
        int end = 0;
        for (int i = 1; i < m + 1; ++i) {
            for (int j = 1; j < i; ++j) {
                double tmp = distancesMatrix[i][n] - distancesMatrix[j][1];
                if (tmp < min) {
                    min = tmp;
                    begin = j - 1;
                    end = i - 1;
                }
            }
        }
        result.put(doc.substring(begin, end + 1), min);
        return result;
    }

    public static double getWord2VecDistance(String s1, String s2) {
        PythonInterpreter interpreter = new PythonInterpreter();
        ;
        String basePath = TextUtil.class.getResource("").getPath();
        interpreter.execfile(basePath + "word2vec.py");
        PyFunction func = interpreter.get("get_sentence_sim", PyFunction.class);
        PyObject pyobj = func.__call__(new PyString(s1), new PyString(s2));
        System.out.println("anwser = " + pyobj.toString());
        return Double.parseDouble(pyobj.toString());
    }

    public static List<String> getSentences(String textcontent) {
//        String regEx = "[。|！|?|；|;|？|?]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(textcontent);
//        String[] words = p.split(textcontent);
//        //将句子结束符连接到相应的句子后
//        if(words.length > 0)
//        {
//            int count = 0;
//            while(count < words.length)
//            {
//                if(m.find())
//                {
//                    words[count] += m.group();
//                }
//                count++;
//            }
//        }
//        return Arrays.asList(words);

//        // 句子过长导致编辑距离过大，丢失最佳句子，逗号也加入进行分句
//        //  暂时不加，通过除以句长进行归一化了
//        return Arrays.asList(textcontent.split("[。|！|?|；|;|？|?|，|,]"));
        return Arrays.asList(textcontent.split("[。|！|?|；|;|？|?|“|”|\"|\"]"));
    }

    public static String cleanStr(String str) {
        String regEx = "[`~@#$%^&*()+=|{}':;'//[//]<>/~@#￥%&*（）——+|{}【】‘；：”“’\"\"'']";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

//    public static void main(String[] args) {
//        System.out.println(getMinEditDistance("嗨你好哇", "你好"));
//    }
}
