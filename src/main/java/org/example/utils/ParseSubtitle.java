package org.example.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.example.model.Subtitle;

import java.util.ArrayList;
import java.util.List;


import static org.example.constant.Constant.COOKIE;
import static org.example.utils.FileUtils.saveSubtitleToFile;

public class ParseSubtitle {
    /**
     * 获取字幕信息。
     * @param bvid 视频的 bvid。
     * @param cid 视频的 cid。
     * @return 字幕信息的 JSON 字符串。
     */
    public static String getSubtitleInfo(String bvid, String cid) {
        String urlStr = "https://api.bilibili.com/x/player/wbi/v2?bvid=" + bvid + "&cid=" + cid;
        //System.out.println(urlStr);
        return HttpUtil.createGet(urlStr)
                // 添加Cookie实现登录状态
                .header("Cookie", COOKIE)
                .execute()
                .body();
    }

    /**
     * 下载字幕文件。
     * @param subtitleInfo 字幕信息的 JSON 字符串。
     */
    public static void downloadSubtitles(String subtitleInfo, String part) {
        JSONObject jsonObject = JSONUtil.parseObj(subtitleInfo);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject subtitle = data.getJSONObject("subtitle");
        JSONArray subtitles = subtitle.getJSONArray("subtitles");

        System.out.println(data);
        if (!subtitles.isEmpty()){
            System.out.println(subtitles);
        }else {
            //System.out.println(data);
            System.out.println(part + "无字幕");
            return;
        }

        for (int i = 0; i < subtitles.size(); i++) {
            JSONObject subtitleEntry = subtitles.getJSONObject(i);
            String lan = subtitleEntry.getStr("lan_doc");
            String subtitleUrl = "https:" + subtitleEntry.getStr("subtitle_url");
            String subtitleContent = getSubtitleContent(subtitleUrl);
            //System.out.println(subtitleContent);
            Subtitle subtitle1 = new Subtitle();
            subtitle1.setSubtitleList(extractContents(subtitleContent));
            subtitle1.setTitle(part);
            subtitle1.setType(lan);
            //System.out.println( extractContents(subtitleContent));
            saveSubtitleToFile(subtitle1);
        }
    }
    /**
     * 提取所有字幕
     * @param jsonString
     * @return
     */
    public static List<String> extractContents(String jsonString) {
        List<String> contents = new ArrayList<>();

        // 解析 JSON 字符串
        JSONObject jsonObject = new JSONObject(jsonString);

        // 获取 body 数组
        JSONArray bodyArray = jsonObject.getJSONArray("body");

        if (bodyArray != null) {
            for (int i = 0; i < bodyArray.size(); i++) {
                JSONObject item = bodyArray.getJSONObject(i);
                String content = item.getStr("content");
                if (content != null && !content.isEmpty()) {
                    contents.add(content);
                }
            }
        }

        return contents;
    }
    /**
     * 获取单个字幕的内容。
     * @param subtitleUrl 字幕的下载链接。
     * @return 字幕内容的 JSON 字符串。
     */
    public static String getSubtitleContent(String subtitleUrl) {
        return HttpUtil.createGet(subtitleUrl)
                // .header("User-Agent", USER_AGENT)
                .execute()
                .body();
    }
}
