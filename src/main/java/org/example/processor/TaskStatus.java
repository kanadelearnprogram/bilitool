package org.example.processor;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    PENDING,     // 待处理
    PROCESSING,  // 处理中
    COMPLETED,   // 已完成
    FAILED       // 失败
}