package org.example.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.example.model.Video;

import java.util.HashMap;
import java.util.Map;

public class ParseVideo {
    /**
     * 获取视频信息
     * @param bvid 视频的 bvid
     *
     * @return
     */
    public static String getVideoInfo(String bvid) {
        String urlStr = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid ;
        //System.out.println(urlStr);
        return HttpUtil.createGet(urlStr)
                .execute()
                .body();
    }
    /**
     * 从 JSON 字符串创建 Video 对象
     *
     * @param json JSON 字符串
     * @return Video 对象
     */
    public static Video fromJson(String json) {
        Video video = new Video();
        JSONObject jsonObject = JSONUtil.parseObj(json);

        // 获取data字段对象
        JSONObject dataObject = jsonObject.getJSONObject("data");
        if (dataObject == null) {
            throw new IllegalArgumentException("JSON数据中缺少data字段");
        }

        // 解析基础字段
        video.bvid = getJsonString(dataObject, "bvid");
        video.aid = getJsonString(dataObject, "aid");
        video.videoNums = Integer.parseInt(getJsonString(dataObject, "videos"));
        video.title = getJsonString(dataObject, "title");
        video.pic = getJsonString(dataObject, "pic");

        // 初始化分集Map
        video.cidMap = new HashMap<>();
        // 解析分集信息
        JSONArray pages = dataObject.getJSONArray("pages");
        if (pages != null) {
            for (int i = 0; i < pages.size(); i++) {
                JSONObject page = pages.getJSONObject(i);

                // 提取part和cid
                String part = getJsonString(page, "part");
                String cid = getJsonString(page, "cid");
                video.cidMap.put(cid, part);

            }
        }

        return video;
    }

    /**
     * 静态方法：安全获取 JSON 字符串字段
     */
    private static String getJsonString(JSONObject obj, String key) {
        return obj.containsKey(key) ? obj.getStr(key) : "";
    }

}