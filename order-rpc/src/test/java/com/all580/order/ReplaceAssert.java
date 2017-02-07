package com.all580.order;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 16:56
 */
public class ReplaceAssert {
    public static void main(String[] args) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File("E:\\Gary\\working\\3.0\\base\\order-rpc\\src\\main\\java\\com\\all580\\order"), new String[]{"java"}, true);
        Pattern pattern = Pattern.compile("[if ]+\\(+[^\\s]+[ == null]+\\)+[ ]+\\{[^\\}]+\\}");
        Pattern ifPattern = Pattern.compile("[if ]+\\(+[^\\s]+[ == null]+\\)+[ ]+\\{");
        Pattern throwPattern = Pattern.compile("[throw new ]+[\\s]");
        Pattern endPattern = Pattern.compile("\\}");
        for (File file : files) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                System.out.println(matcher.group());
            }
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            boolean start = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (ifPattern.matcher(line).matches()) {
                    System.out.println("----" + line);
                }
            }
            System.out.println(file.getAbsoluteFile());
        }
    }
}
