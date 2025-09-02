package org.example.model;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import okhttp3.*;
import okio.BufferedSource;
import org.example.config.Config;
import org.example.constant.AIModel;
import org.example.constant.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    public String streamSend() {
        String url = "https://api.deepseek.com/chat/completions"; // 修复URL空格问题
        StringBuilder content = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1); // 用于同步等待流结束

        // 构建请求头
        Map<String, String> headers = JSONUtil.createObj()
                .put("Content-Type", "application/json")
                .put("Authorization", "Bearer " + this.getApiKey())
                .toBean(Map.class);

        // 构建请求体
        JSONArray messageArray = JSONUtil.createArray();
        for (Message message : this.messages) {
            messageArray.add(JSONUtil.createObj()
                    .put("role", message.getRole())
                    .put("content", message.getContent()));
        }

        JSONObject requestBody = JSONUtil.createObj()
                .put("model", this.getModel().getValue())
                .put("stream", true)
                .put("messages", messageArray);

        // 创建OkHttpClient（带超时设置）
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES) // 重要：流式响应需要长超时
                .build();

        // 构建请求
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build();

        // 发送请求并处理流式响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("SSE请求失败: " + e.getMessage());
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body();
                     BufferedSource source = responseBody.source()) {

                    // SSE流式处理
                    while (response.isSuccessful()) {
                        // 读取一行（SSE事件以\n\n分隔）
                        String line = source.readUtf8Line();
                        if (line == null) break;

                        // 处理SSE数据块
                        if (line.startsWith("data: ")) {
                            String jsonData = line.substring(6);
                            if ("[DONE]".equals(jsonData)) {
                                System.out.println("\n流式响应结束");
                                break;
                            }

                            try {
                                JSONObject chunk = JSONUtil.parseObj(jsonData);
                                JSONArray choices = chunk.getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");
                                    if (delta != null && delta.containsKey("content")) {
                                        String deltaContent = delta.getStr("content");
                                        for (char c : deltaContent.toCharArray()) {
                                            System.out.print(c);
                                            System.out.flush();
                                        }
                                        content.append(deltaContent);
                                    }
                                }
                            } catch (Exception e) {
                                // 忽略解析错误（如部分响应不完整）
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("SSE处理异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }
        });

        try {
            // 等待流结束（最多5分钟）
            if (!latch.await(5, TimeUnit.MINUTES)) {
                System.err.println("SSE处理超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("SSE等待中断");
        }

        return content.toString();
    }

}
