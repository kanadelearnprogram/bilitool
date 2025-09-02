import okhttp3.OkHttpClient;
import org.example.model.Client;
import org.junit.Test;

import static org.example.config.Config.loadConfig;

public class StreamTest {
    private final OkHttpClient client = new OkHttpClient();
    @Test
    public void test(){
        loadConfig();
        String str = "随机生成字符串";

        Client client = new Client();
        client.addUserMessage(str);
        String result = client.streamSend();
        System.out.println(result);
    }
}
