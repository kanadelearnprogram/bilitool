package org.example.constant;

import static org.example.config.Config.cookie;
import static org.example.config.Config.sk;

public interface Constant {
    String COOKIE =
            "SESSDATA=" + cookie + ";" +
            "path=/; domain=.bilibili.com";
    String apikey = sk;
}
