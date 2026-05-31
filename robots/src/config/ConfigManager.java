package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Универсальный менеджер конфигурации.
 * Отвечает только за сохранение и загрузку пар "ключ-значение" в файл.
 * Никак не связан с GUI.
 */
public class ConfigManager {
    // Имя файла настроек, который будет создаваться в корне проекта
    private static final String CONFIG_FILE_NAME = "window_settings.properties";
    private final Properties properties;
    private final File configFile;

    public ConfigManager() {
        this.properties = new Properties();
        // Находим домашнюю директорию пользователя, чтобы сохранять конфиг туда
        String userHome = System.getProperty("user.home");
        this.configFile = new File(userHome, CONFIG_FILE_NAME);
        load();
    }

    /**
     * Возвращает значение по ключу. Если ключа нет, вернет defaultValue.
     */
    public String getValue(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Устанавливает значение для ключа.
     */
    public void setValue(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Загружает настройки из файла.
     */
    private void load() {
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке файла конфигурации: " + e.getMessage());
            }
        }
    }

    /**
     * Сохраняет текущие настройки в файл на диск.
     */
    public void save() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "Application Window Settings");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла конфигурации: " + e.getMessage());
        }
    }
}