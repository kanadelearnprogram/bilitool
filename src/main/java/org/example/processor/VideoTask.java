package org.example.processor;

import org.example.model.Video;

/**
 * 视频处理任务类
 */
public class VideoTask {
    private final int taskId;
    private final Video video;
    private TaskStatus status;
    private int completedSteps;
    
    public VideoTask(int taskId, Video video) {
        this.taskId = taskId;
        this.video = video;
        this.status = TaskStatus.PENDING;
        this.completedSteps = 0;
    }
    
    // Getters and setters
    public int getTaskId() {
        return taskId;
    }
    
    public Video getVideo() {
        return video;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public int getCompletedSteps() {
        return completedSteps;
    }
    
    public void setCompletedSteps(int completedSteps) {
        this.completedSteps = completedSteps;
    }
    
    public void incrementCompletedSteps() {
        this.completedSteps++;
    }
}