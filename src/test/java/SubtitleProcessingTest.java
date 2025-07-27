import org.junit.Test;
import org.example.model.Subtitle;
import org.example.utils.FileUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SubtitleProcessingTest {
    
    @Test
    public void testSaveSubtitleToFile() {
        System.out.println("=== Testing Save Subtitle to File ===");
        
        // 创建测试字幕数据
        Subtitle subtitle = new Subtitle();
        subtitle.setTitle("测试视频");
        subtitle.setType("中文");
        List<String> testContent = Arrays.asList(
            "这是第一条字幕",
            "这是第二条字幕",
            "这是第三条字幕"
        );
        subtitle.setSubtitleList(testContent);
        
        long start = System.currentTimeMillis();
        FileUtils.saveSubtitleToFile(subtitle);
        long end = System.currentTimeMillis();
        
        System.out.println("Save subtitle time: " + (end - start) + " ms");
    }
    
    @Test
    public void testSaveSubtitleToFileAsync() {
        System.out.println("=== Testing Save Subtitle to File Async ===");
        
        // 创建测试字幕数据
        Subtitle subtitle = new Subtitle();
        subtitle.setTitle("测试视频异步");
        subtitle.setType("英文");
        List<String> testContent = Arrays.asList(
            "This is the first subtitle",
            "This is the second subtitle",
            "This is the third subtitle"
        );
        subtitle.setSubtitleList(testContent);
        
        long start = System.currentTimeMillis();
        CompletableFuture<Void> future = FileUtils.saveSubtitleToFileAsync(subtitle);
        long end = System.currentTimeMillis();
        
        System.out.println("Save subtitle async time: " + (end - start) + " ms");
        
        // 等待异步任务完成
        try {
            future.join(); // 等待AI处理完成
            System.out.println("异步任务已完成");
        } catch (Exception e) {
            System.err.println("异步任务执行出错: " + e.getMessage());
        }
    }
}