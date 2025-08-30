package org.example.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class Config {
    // 配置文件路径
    public static final String CONFIG_PATH = "/config.json";
    private static Config instance;
    public static String cookie = "";
    public static String sk = "";
    public static String filePath = "";
    public static String prompt = "";
    public static String models = "";

    private Config() {
        loadConfig();
    }

    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }
    public static void loadConfig() {
        try {
            // 使用类加载器读取资源文件
            InputStream is = Config.class.getResourceAsStream(CONFIG_PATH);
            if (is == null) {
                throw new FileNotFoundException("配置文件未找到: " + CONFIG_PATH);
            }

            // 读取文件内容
            String content = IoUtil.readUtf8(is);
            JSONObject configJson = JSONUtil.parseObj(content);

            // 设置配置项
            cookie = configJson.getStr("SESSDATA");
            System.out.println("cookie " + cookie);
            sk = configJson.getStr("sk");
            System.out.println("sk " + sk);
            filePath = configJson.getStr("filepath");
            System.out.println("filePath " + filePath);
            prompt = configJson.getStr("prompt");
            System.out.println("prompt " + prompt);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("配置文件读取失败，请检查配置文件路径是否正确");
        }
    }

    public static void changeConfig() {
        try {
            // 创建JSON对象
            JSONObject configJson = new JSONObject();
            configJson.set("SESSDATA", cookie);
            configJson.set("sk", sk);

            // 写入文件
            Path path = Paths.get("src/main/resources/config.json");
            Files.write(path, configJson.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("配置文件写入失败: " + e.getMessage());
        }
    }
}