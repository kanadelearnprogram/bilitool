import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class AsyncTest {
    
    @Test
    public void testPlatformThreadPerformance() {
        System.out.println("=== Platform Thread Performance Test ===");
        long start = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        // 创建100个线程模拟并发任务
        for (int i = 0; i < 100; i++) {
            final int taskId = i;
            Thread thread = new Thread(() -> {
                // 模拟I/O操作
                try {
                    Thread.sleep(50);
                    System.out.println("Platform thread task " + taskId + "\t"+ " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(thread);
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Platform threads time taken: " + (end - start) + " ms");
    }
    
    @Test
    public void testVirtualThreadPerformance() {
        System.out.println("=== Virtual Thread Performance Test ===");
        long start = System.currentTimeMillis();

        List<Thread> virtualThreads = new ArrayList<>();
        // 创建100个虚拟线程模拟并发任务
        for (int i = 0; i < 100; i++) {
            final int taskId = i;
            Thread virtualThread = Thread.ofVirtual().name("virtual-thread-" + i).unstarted(() -> {
                // 模拟I/O操作
                try {
                    Thread.sleep(50);
                    System.out.println("Virtual thread task " + taskId + "\t" + " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            virtualThreads.add(virtualThread);
            virtualThread.start();
        }

        // 等待所有虚拟线程完成
        for (Thread thread : virtualThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Virtual threads time taken: " + (end - start) + " ms");
    }
    
    @Test
    public void testThreadCreationComparison() {
        System.out.println("=== Thread Creation Comparison ===");
        
        // 测试创建1000个平台线程所需时间
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                // 空任务
            });
        }
        long platformThreadCreationTime = System.currentTimeMillis() - start;
        
        // 测试创建1000个虚拟线程所需时间
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(() -> {
                // 空任务
            });
        }
        long virtualThreadCreationTime = System.currentTimeMillis() - start;
        
        System.out.println("Time to create 1000 platform threads: " + platformThreadCreationTime+ "\t" + " ms");
        System.out.println("Time to create 1000 virtual threads: " + virtualThreadCreationTime+ "\t" + " ms");
    }
}