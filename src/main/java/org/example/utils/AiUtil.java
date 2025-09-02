package org.example.utils;

import cn.hutool.core.io.FileUtil;
import org.example.model.Client;
import org.example.model.Message;
import org.example.model.Subtitle;

import java.io.File;
import java.util.List;

import static org.example.utils.FileUtils.saveStrToFileAsync;

public class AiUtil {
    public static String AISumarize (Subtitle subtitle){

        //String title = subtitle.getTitle();

        StringBuilder str = new StringBuilder();
        for (String s : subtitle.getSubtitleList()) {
            str.append(s).append("\n");
        }
        //String fileName2 = String.format("%s%s ai_summarize_subtitle_%s%s.md", savePath, File.separator, safeType, safeTitle);
        try {
            Client client = new Client();
            client.addUserMessage(str.toString());
            String result = client.streamSend();

            //System.out.println(result);

            return result;
            //saveStrToFileAsync(title,result);
            /*FileUtil.writeUtf8String(result, fileName2);
            System.out.println("已保存AI总结字幕文件: " + fileName2);*/
        } catch (Exception e) {
            System.err.println("AI总结处理失败: " + e.getMessage());
        }
        return null;
    }



}
