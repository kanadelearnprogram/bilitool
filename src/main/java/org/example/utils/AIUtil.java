package org.example.utils;

import org.example.model.Client;
import org.example.model.Subtitle;

import java.util.concurrent.CompletableFuture;

public class AIUtil {
    public static String summarize(Subtitle subtitle) {
        StringBuilder str = new StringBuilder();
        for (String s : subtitle.getSubtitleList()) {
            str.append(s).append("\n");
        }
        
        try {
            Client client = new Client();
            client.addUserMessage(str.toString());
            return client.send();
        } catch (Exception e) {
            System.err.println("AI总结处理失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 异步进行AI总结
     * @param subtitle 字幕对象
     * @return CompletableFuture包装的总结结果
     */
    public static CompletableFuture<String> summarizeAsync(Subtitle subtitle) {
        return CompletableFuture.supplyAsync(() -> summarize(subtitle));
    }
}