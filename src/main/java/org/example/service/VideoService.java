package org.example.service;

import cn.hutool.http.HttpUtil;
import org.example.model.Video;
import org.example.utils.ParseVideo;

public class VideoService {
    public Video getVideoInfo(String bvid) {
        String urlStr = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
        String response = HttpUtil.createGet(urlStr)
                .execute()
                .body();
        return ParseVideo.fromJson(response);
    }
}
