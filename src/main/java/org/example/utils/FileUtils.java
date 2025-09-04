package org.example.utils;

import cn.hutool.core.io.FileUtil;
import org.example.config.Config;
import org.example.model.Subtitle;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class FileUtils {
    /**
     * 将字幕内容保存到文件。
     */
    public static void saveSubtitleToFile(Subtitle subtitle) {
        // 使用配置路径或默认当前目录
        String savePath = Config.filePath;
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
    
    /**
     * 异步将字幕内容保存到文件。
     * @param subtitle 字幕对象
     */
    public static CompletableFuture<Void> saveSubtitleToFileAsync(Subtitle subtitle) {
        return CompletableFuture.runAsync(() -> saveSubtitleToFile(subtitle));
    }
    
    /**
     * 保存任意字符串内容到文件。
     * @param title 文件标题（用于生成文件名）
     * @param content 文件内容
     * @param prefix 文件前缀
     */
    public static void saveStrToFile(String title, String content, String prefix) {
        // 使用配置路径或默认当前目录
        String savePath = Config.filePath;
        if (savePath == null || savePath.isEmpty()) {
            savePath = ".";
        }

        // 清理文件名中的非法字符
        String safeTitle = prefix + "_" + title.replaceAll("[\\\\/:*?\"<>|]", "_");

        // 构建完整文件路径，使用File.separator确保跨平台兼容性
        String fileName = String.format("%s%s%s.txt", savePath, File.separator, safeTitle);
        
        // 确保目录存在
        FileUtil.mkdir(savePath);

        // 写入文件
        FileUtil.writeUtf8String(content, fileName);
        System.out.println("已保存文件: " + fileName);
    }
    
    /**
     * 异步保存任意字符串内容到文件。
     * @param title 文件标题（用于生成文件名）
     * @param content 文件内容
     * @param prefix 文件前缀
     */
    public static CompletableFuture<Void> saveStrToFileAsync(String title, String content, String prefix) {
        return CompletableFuture.runAsync(() -> saveStrToFile(title, content, prefix));
    }
}