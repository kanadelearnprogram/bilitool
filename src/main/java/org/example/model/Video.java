package org.example.model;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.naming.directory.InvalidAttributeIdentifierException;
import java.util.List;
import java.util.Map;
@Data
public class Video {
    public String bvid;
    public String aid;
    public int videoNums;
    public String title;
    public String pic;
    public Map<String,String> cidMap;

    @Override
    public String toString() {
        return "Video{" +
                "bvid='" + bvid + '\'' +
                ", aid='" + aid + '\'' +
                ", videoNums=" + videoNums +
                ", title='" + title + '\'' +
                ", pic='" + pic + '\'' +
                ", cidList=" + cidMap +
                '}';
    }
}
