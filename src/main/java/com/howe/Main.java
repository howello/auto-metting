package com.howe;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.cron.CronUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.howe.utils.SwUtils;
import com.howe.view.WindowFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * <p>@Author lu
 * <p>@Date 2024/4/24 下午3:12 星期三
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class Main {

    public static WindowFrame FRAME;

    public static final Image ICON;

    static {
        URL resource = ResourceUtil.getResource("1.png");
        ICON = new ImageIcon(resource).getImage();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            if (FRAME == null) {
                FRAME = new WindowFrame();
            }
            FRAME.setVisible(true);
            String num = JOptionPane.showInputDialog(null, "输入周例会会议号：", "45661827839");
            SwUtils.addWeekMetting(num);
            CronUtil.start();
        });
        initTray();
    }

    private static void initTray() {
        if (SystemTray.isSupported()) {
            PopupMenu pop = new PopupMenu();
            MenuItem exitItem = new MenuItem("  退出  ");
            exitItem.addActionListener(e -> System.exit(0));

            pop.add(exitItem);

            TrayIcon tray = new TrayIcon(ICON, "自动会议", pop);
            tray.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if ((e.getButton() == 1) && (e.getClickCount() == 2)) {
                        FRAME.setVisible(true);
                        FRAME.setExtendedState(Frame.NORMAL);
                    }
                }
            });

            tray.setImageAutoSize(true);
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                systemTray.add(tray);
            } catch (AWTException e1) {
            }
        }
    }
}
