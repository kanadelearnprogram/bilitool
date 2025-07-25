package org.example.model;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.example.config.Config;
import org.example.constant.AIModel;
import org.example.constant.Constant;

import java.util.List;
import java.util.Map;

import static org.example.constant.AIModel.DEEPSEEK_CHAT;

@Data
public class Client {
    private String apiKey;
    private AIModel model;
    String prompt;
    List<Message> messages = new java.util.ArrayList<>();

    public Client(){
        this.apiKey = Constant.apikey;
        this.model = DEEPSEEK_CHAT;
        this.prompt = Config.prompt;
        this.messages.add(new Message("system", prompt));
    }
    public void addUserMessage(String content){
        this.messages.add(new Message("user", content));
    }
    public String send(){
        String url = "https://api.deepseek.com/chat/completions";
        // 构建请求头
        JSONObject headers = JSONUtil.createObj()
                .put("Content-Type", "application/json")
                .put("Authorization", "Bearer " + this.getApiKey());
        // System.out.println(headers);
        Map<String, String> headersMap = headers.toBean(Map.class);

        // 构建请求体
        JSONArray messageArray = JSONUtil.createArray();
        for (Message message : this.messages) {
            JSONObject messageObj = JSONUtil.createObj()
                    .put("role", message.getRole())
                    .put("content", message.getContent());
            messageArray.add(messageObj);
        }

        JSONObject requestBody = JSONUtil.createObj()
                .put("model", this.getModel().getValue())
                .put("stream", false)
                .put("messages", messageArray);

        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(headersMap)
                .body(requestBody.toString())
                .execute();
        
        JSONObject jsonObject = JSONUtil.parse(response.body()).toBean(JSONObject.class);
        
        // 提取content内容
        JSONArray choices = jsonObject.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        String content = message.getStr("content");

        // 返回响应结果
        return content;
    }

}
