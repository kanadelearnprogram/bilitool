package org.example.constant;

public enum AIModel {
    DEEPSEEK_CHAT("deepseek-chat");
    AIModel(String value) {
        this.value = value;
    }
    private final String value;
    public String getValue() {
        return value;
    }

    // 根据名称获取枚举实例
    public static AIModel fromValue(String value) {
        for (AIModel model : values()) {
            if (model.value.equalsIgnoreCase(value)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unsupported AI model: " + value);
    }

}
