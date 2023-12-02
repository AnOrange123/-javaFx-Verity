package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    public static Properties loadConfig() {
        Properties properties = new Properties();
        Path configFilePath = Paths.get("conf", "config.properties");

        try (FileReader fileReader = new FileReader(configFilePath.toFile())) {
            properties.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }

        return properties;
    }
}
