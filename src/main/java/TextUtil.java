import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

/**
 * Created by xuzh on 2018/11/26.
 */
public class TextUtil {
    public static double getEditDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        double distance[][] = new double[m + 1][n + 1];
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
        return distance[m][n];
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
        return Arrays.asList(textcontent.split("[。|！|?|；|;|？|?]"));
    }

    public static void main(String[] args)
    {
        System.out.println(getEditDistance("你好", "嗨你好哇"));
    }
}
