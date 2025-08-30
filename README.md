# Bilitool - B站字幕处理工具

## 项目简介
Bilitool 是一个基于Java的B站字幕处理工具，支持字幕下载、AI内容总结和自定义保存路径配置。

## 技术栈
- Java 21
- Maven 3.x
- [Hutool](https://github.com/dromara/hutool) (文件和JSON处理)
- [Lombok](https://projectlombok.org/) (简化POJO开发)
- [DeepSeek API](https://www.deepseek.com/) (AI模型交互)

## 快速开始
1. 安装 [JDK 21](https://jdk.java.net/archive/)
2. 配置 [Maven 3.x](https://maven.apache.org/) 环境
3. 修改 `config.json` 配置项
4. 构建项目：`mvn clean package`
5. 运行程序：`java -jar bilitool.jar bvid BVXXXXXX`

## 核心功能
- 从B站视频页面提取字幕数据
- 使用AI模型进行内容总结
- 生成两个文件：
  - 原始字幕文件 `subtitle_<类型><标题>.txt`
  - AI总结文件 `ai_summarize_subtitle_<类型><标题>.txt`

## 配置说明
查看 [config.json](src/main/resources/config.json) 配置文件：
```json
{
  "SESSDATA": "B站会话令牌",
  "sk": "DeepSeek API 密钥",
  "filepath": "E:\\tools",  // 字幕保存路径
  "prompt": "少保留习题内容,详细总结解题方法 使用markdown格式,不多于三个子标签"
}
```

## 文件命名规范
- 自动清理非法字符（`\\ / : * ? " < > |`）
- 使用下划线 `_` 替代非法字符
- 保持跨平台兼容性

todo
多线程获取字幕并总结