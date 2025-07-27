import org.junit.Test;
import org.example.model.Subtitle;
import org.example.utils.FileUtils;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ConcurrentProcessingTest {
    
    @Test
    public void testConcurrentSubtitleProcessing() {
        System.out.println("=== Testing Concurrent Subtitle Processing ===");
        
        long start = System.currentTimeMillis();
        
        // 创建多个字幕处理任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Subtitle subtitle = new Subtitle();
                subtitle.setTitle("测试视频" + index);
                subtitle.setType("语言" + index);
                List<String> testContent = Arrays.asList(
                    "这是第1条字幕 - " + index,
                    "这是第2条字幕 - " + index,
                    "这是第3条字幕 - " + index
                );
                subtitle.setSubtitleList(testContent);
                
                FileUtils.saveSubtitleToFileAsync(subtitle);
            });
            futures.add(future);
        }
        
        // 等待所有异步任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        long end = System.currentTimeMillis();
        System.out.println("Concurrent processing time: " + (end - start) + " ms");
        System.out.println("所有并发任务已完成");
    }
}