package gui;

import javax.swing.JInternalFrame;
import config.ConfigManager;

/**
 * Класс для сохранения и восстановления состояний окон.
 * Реализует разделение логики работы с окнами и работы с файлами.
 */
public class WindowStateManager {
    private final ConfigManager configManager;

    public WindowStateManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Сохраняет состояние конкретного окна.
     * @param windowId Уникальный неизменяемый идентификатор окна (например, "log_window")
     * @param window Само окно JInternalFrame
     */
    public void saveInternalFrameState(String windowId, JInternalFrame window) {
        configManager.setValue(windowId + ".x", String.valueOf(window.getX()));
        configManager.setValue(windowId + ".y", String.valueOf(window.getY()));
        configManager.setValue(windowId + ".width", String.valueOf(window.getWidth()));
        configManager.setValue(windowId + ".height", String.valueOf(window.getHeight()));
        configManager.setValue(windowId + ".isIcon", String.valueOf(window.isIcon()));
        configManager.setValue(windowId + ".isMaximum", String.valueOf(window.isMaximum()));
    }

    /**
     * Восстанавливает состояние конкретного окна.
     * @param windowId Уникальный неизменяемый идентификатор окна
     * @param window Само окно JInternalFrame
     * @param defaultX Координаты по умолчанию, если программа запускается впервые
     */
    /**
     * Восстанавливает состояние конкретного окна.
     */
    public void restoreInternalFrameState(String windowId, JInternalFrame window, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        int x = Integer.parseInt(configManager.getValue(windowId + ".x", String.valueOf(defaultX)));
        int y = Integer.parseInt(configManager.getValue(windowId + ".y", String.valueOf(defaultY)));
        int width = Integer.parseInt(configManager.getValue(windowId + ".width", String.valueOf(defaultWidth)));
        int height = Integer.parseInt(configManager.getValue(windowId + ".height", String.valueOf(defaultHeight)));
        boolean isIcon = Boolean.parseBoolean(configManager.getValue(windowId + ".isIcon", "false"));
        boolean isMaximum = Boolean.parseBoolean(configManager.getValue(windowId + ".isMaximum", "false"));


        window.setBounds(x, y, width, height);
        try {
            window.setIcon(isIcon);
            window.setMaximum(isMaximum);
        } catch (Exception e) {
            System.err.println("Не удалось восстановить состояние свертывания/максимизации для " + windowId);
        }
    }
}