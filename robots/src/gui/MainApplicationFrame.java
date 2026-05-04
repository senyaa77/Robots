package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;

import log.Logger;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
        restoreWindowState(frame);
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createActionMenu()); // Добавляем наше новое меню

        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    private JMenu createActionMenu() {
        JMenu actionMenu = new JMenu("Управление");
        actionMenu.setMnemonic(KeyEvent.VK_A);

        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.addActionListener((event) -> {
            // Вызываем нашу готовую логику из Шага №2
            exitApplication();
        });
        actionMenu.add(exitItem);

        return actionMenu;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private final WindowConfigManager configManager = new WindowConfigManager();

    private void saveWindowStates() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            // префикс для ключей
            String prefix = frame.getTitle() + ".";

            configManager.setProperty(prefix + "x", String.valueOf(frame.getX()));
            configManager.setProperty(prefix + "y", String.valueOf(frame.getY()));
            configManager.setProperty(prefix + "width", String.valueOf(frame.getWidth()));
            configManager.setProperty(prefix + "height", String.valueOf(frame.getHeight()));
            configManager.setProperty(prefix + "isIcon", String.valueOf(frame.isIcon())); // Свернуто
            configManager.setProperty(prefix + "isMaximum", String.valueOf(frame.isMaximum())); // Развернуто
        }
        configManager.save();
    }

    private void exitApplication() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (n == JOptionPane.YES_OPTION) {
            saveWindowStates();

            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                frame.dispose(); // Это вызовет метод dispose у GameWindow и остановит таймер
            }

            this.dispose(); // Закрываем главное окно
        }
    }

    private void restoreWindowState(JInternalFrame frame) {
        String prefix = frame.getTitle() + ".";

        // 1. Читаем данные заранее (вне потока отрисовки)
        String xStr = configManager.getProperty(prefix + "x");
        String yStr = configManager.getProperty(prefix + "y");
        String wStr = configManager.getProperty(prefix + "width");
        String hStr = configManager.getProperty(prefix + "height");
        String isIconStr = configManager.getProperty(prefix + "isIcon");
        String isMaxStr = configManager.getProperty(prefix + "isMaximum");

        // Если данных в файле нет, ничего не делаем и выходим
        if (xStr == null || yStr == null) return;

        // 2. Используем invokeLater, чтобы дождаться полной инициализации GUI
        SwingUtilities.invokeLater(() -> {
            try {
                // Устанавливаем границы окна
                frame.setBounds(
                        Integer.parseInt(xStr),
                        Integer.parseInt(yStr),
                        Integer.parseInt(wStr),
                        Integer.parseInt(hStr)
                );

                // Состояния сворачивания/разворачивания делаем через еще один вложенный цикл
                // Это гарантирует, что Swing уже "знает" финальный размер окна
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (Boolean.parseBoolean(isMaxStr)) frame.setMaximum(true);
                        if (Boolean.parseBoolean(isIconStr)) frame.setIcon(true);
                    } catch (Exception e) {
                        // Игнорируем вето на изменение состояния
                    }
                });
            } catch (Exception e) {
                System.err.println("Ошибка восстановления окна " + frame.getTitle());
            }
        });
    }

}
