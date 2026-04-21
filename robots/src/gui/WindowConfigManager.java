package gui;

import java.io.*;
import java.util.Properties;

public class WindowConfigManager {
    // Файл будет храниться в домашней папке пользователя: /Users/имя/.robots_windows.properties
    private static final File CONFIG_FILE = new File(System.getProperty("user.home"), ".robots_windows.properties");
    private final Properties properties = new Properties();

    public WindowConfigManager() {
        load();
    }

    // Загрузка данных из файла в память
    private void load() {
        if (CONFIG_FILE.exists()) {
            try (InputStream is = new FileInputStream(CONFIG_FILE)) {
                properties.load(is);
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке конфига: " + e.getMessage());
            }
        }
    }

    // Сохранение данных из памяти в файл
    public void save() {
        try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
            properties.store(os, "Window States Configuration");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении конфига: " + e.getMessage());
        }
    }

    // Установка значения
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    // Получение значения
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}