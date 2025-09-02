package org.example.utils;

import cn.hutool.core.io.FileUtil;
import org.example.config.Config;
import org.example.model.Client;
import org.example.model.Subtitle;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.SubtitleDownloader.subtitleList;

public class FileUtils {
    /**
     * 将字幕内容保存到文件。
     */
    public static void saveSubtitleToFile(Subtitle subtitle) {
        // 使用配置路径或默认当前目录
        String savePath = Config.filePath;
        //System.out.println(savePath);
        // 如果路径为空，使用当前目录
        if (savePath == null || savePath.isEmpty()) {
            savePath = ".";
        }

        // 生成带索引的唯一文件名
        StringBuilder str = new StringBuilder();
        for (String s : subtitle.getSubtitleList()) {
            str.append(s).append("\n");
        }

        Client client = new Client();
        client.addUserMessage(str.toString());
        String result = client.send();

        System.out.println(result);
        // 清理文件名中的非法字符
        String safeTitle = subtitle.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");
        String safeType = subtitle.getType().replaceAll("[\\\\/:*?\"<>|]", "_");

        // 构建完整文件路径，使用File.separator确保跨平台兼容性
        String fileName = String.format("%s%s subtitle_%s%s.txt", savePath, File.separator, safeType, safeTitle);
        String fileName2 = String.format("%s%s ai_summarize_subtitle_%s%s.txt", savePath, File.separator, safeType, safeTitle);

        // 确保目录存在
        FileUtil.mkdir(savePath);

        // 写入文件
        FileUtil.writeUtf8String(str.toString(), fileName);
        System.out.println("已保存字幕文件: " + fileName);
        FileUtil.writeUtf8String(result, fileName2);
        System.out.println("已保存字幕文件: " + fileName2);
    }
    
    /**
     * 异步将字幕内容保存到文件。
     * @param subtitle 字幕对象
     * @return CompletableFuture 用于等待异步任务完成
     */
    public static void saveSubtitleToFileAsync(Subtitle subtitle) {
        // 使用配置路径或默认当前目录
        String savePath = Config.filePath;
        //System.out.println(savePath);
        // 如果路径为空，使用当前目录
        if (savePath == null || savePath.isEmpty()) {
            savePath = ".";
        }

        // 生成带索引的唯一文件名
        StringBuilder str = new StringBuilder();
        for (String s : subtitle.getSubtitleList()) {
            str.append(s).append("\n");
        }

        // 清理文件名中的非法字符
        String safeTitle = subtitle.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");
        String safeType = subtitle.getType().replaceAll("[\\\\/:*?\"<>|]", "_");

        // 构建完整文件路径，使用File.separator确保跨平台兼容性
        String fileName = String.format("%s%s subtitle_%s%s.txt", savePath, File.separator, safeType, safeTitle);


        // 确保目录存在
        FileUtil.mkdir(savePath);

        // 写入原始字幕文件
        FileUtil.writeUtf8String(str.toString(), fileName);
        System.out.println("已保存字幕文件: " + fileName);

    }
    public static void saveStrToFileAsync(String title,String content) {
        // 使用配置路径或默认当前目录
        String savePath = Config.filePath;
        //System.out.println(savePath);
        // 如果路径为空，使用当前目录
        if (savePath == null || savePath.isEmpty()) {
            savePath = ".";
        }

        // 清理文件名中的非法字符
        String safeTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
        String safeType = content.replaceAll("[\\\\/:*?\"<>|]", "_");

        // 构建完整文件路径，使用File.separator确保跨平台兼容性
        String fileName = String.format("%s%s subtitle_%s%s.txt", savePath, File.separator, safeType, safeTitle);

        // 确保目录存在
        FileUtil.mkdir(savePath);

        // 写入原始字幕文件
        FileUtil.writeUtf8String(content, fileName);
        System.out.println("已保存字幕文件: " + fileName);

    }
}