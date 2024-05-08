package com.howe;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>@Author lu
 * <p>@Date 2024/4/24 下午3:12 星期三
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class Main {
    public static final JFrame FRAME = new JFrame();

    private static JPanel panel0, panel1, panel2, panel3, panel4;
    private static JLabel label0, label1, label2, label3, tip, minuteLabel;
    private static JTextField ahead, numText, timeText;
    private static JScrollPane scrollPane1;
    private static JTextPane textPane1;
    private static JButton button1, button2, button3, startNow;
    private static MenuItem exitItem;
    private static TaskListWindow taskListWindow;

    public static List<MettingDTO> mettingList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Main::init);

        MettingDTO mettingDTO = new MettingDTO("45661827839", "周例会");
        mettingDTO.setCron("0 53 16 * * 5");
        mettingDTO = createMettingTask(mettingDTO);
        mettingList.add(mettingDTO);
        CronUtil.start();
    }

    public static void init() {
        FRAME.setTitle("自动会议");
        FRAME.setSize(750, 500);
        FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        panel0 = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        panel4 = new JPanel();

        label0 = new JLabel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        tip = new JLabel();
        minuteLabel = new JLabel();

        ahead = new JTextField();
        numText = new JTextField();
        timeText = new JTextField();

        scrollPane1 = new JScrollPane();
        textPane1 = new JTextPane();

        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        startNow = new JButton();
        exitItem = new MenuItem("退出");

        FRAME.setLayout(new BoxLayout(FRAME.getContentPane(), BoxLayout.Y_AXIS));

        panel0.setMaximumSize(new Dimension(2147483647, 50));
        panel0.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel0.setLayout(new BoxLayout(panel0, BoxLayout.X_AXIS));

        label0.setText("提前时间：");
        minuteLabel.setText("分");
        ahead.setText("10");
        panel0.add(label0);
        panel0.add(ahead);
        panel0.add(minuteLabel);
        FRAME.add(panel0);

        panel1.setMaximumSize(new Dimension(2147483647, 50));
        panel1.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        label2.setText("会议号码：");
        panel1.add(label2);
        panel1.add(numText);
        FRAME.add(panel1);

        panel2.setMaximumSize(new Dimension(2147483647, 50));
        panel2.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        label3.setText("会议时间：");
        panel2.add(label3);
        panel2.add(timeText);
        FRAME.add(panel2);


        panel4.setMaximumSize(new Dimension(2147483647, 300));
        panel4.setMinimumSize(new Dimension(2147483647, 150));
        panel4.setPreferredSize(new Dimension(2147483647, 150));
        panel4.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setBorder(new EmptyBorder(1, 1, 1, 1));
        scrollPane1.setViewportView(textPane1);
        label1.setText("会议数据：");
        panel4.add(label1);
        panel4.add(scrollPane1);
        FRAME.add(panel4);

        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        button1.setText("查看列表");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskListWindow = new TaskListWindow();
            }
        });
        panel3.add(button1);

        button3.setText("解析数据");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textPane1.getText();
                String time = getTime(text);
                String num = getNum(text);
                numText.setText(num);
                timeText.setText(time);
                JOptionPane.showMessageDialog(FRAME, "解析完成，请注意页面是否已填充！");
            }
        });
        panel3.add(button3);

        button2.setText("提交任务");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = timeText.getText();
                try {
                    DateTime time = DateUtil.parse(text, "yyyy/MM/dd HH:mm");
                    if (time.isBefore(DateTime.now())) {
                        JOptionPane.showMessageDialog(FRAME, "这日子已经过去了，大哥");
                        return;
                    }
                    String num = numText.getText();
                    if (StrUtil.isBlank(num)) {
                        JOptionPane.showMessageDialog(FRAME, "会议号得有啊，大哥");
                        return;
                    }
                    if (mettingList.stream().anyMatch(m -> num.equals(m.getNum()))) {
                        JOptionPane.showMessageDialog(FRAME, "有这会议了，先删除了再添加吧");
                        return;
                    }

                    MettingDTO mettingDTO = new MettingDTO(num, textPane1.getText());
                    String aheadNum = ahead.getText();
                    if (StrUtil.isBlank(aheadNum)) {
                        aheadNum = "10";
                    }
                    DateTime offsetMinute = DateUtil.offsetMinute(time, (int) NumberUtil.sub(0, Integer.parseInt(aheadNum)));
                    mettingDTO.setTime(offsetMinute.isBeforeOrEquals(DateTime.now()) ? time : offsetMinute);
                    mettingDTO = createMettingTask(mettingDTO);
                    mettingList.add(mettingDTO);
                    numText.setText("");
                    timeText.setText("");
                    textPane1.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FRAME, "日期不对啊，大哥");
                }
            }
        });
        panel3.add(button2);

        startNow.setText("立即开始");
        startNow.addActionListener(e -> {
            String num = numText.getText();
            if (StrUtil.isBlank(num)) {
                JOptionPane.showMessageDialog(FRAME, "会议号为空");
                return;
            }
            startMetting(num);
        });
        panel3.add(startNow);

        FRAME.add(panel3);

        tip.setText("日期格式：yyyy/MM/dd HH:mm");
        tip.setForeground(Color.BLACK);
        tip.setHorizontalTextPosition(SwingConstants.LEFT);
        FRAME.add(tip);
        // 设置窗口属性
        FRAME.setLocationRelativeTo(null);
        FRAME.setVisible(true);

        if (SystemTray.isSupported()) {
            URL resource = ResourceUtil.getResource("1.png");
            ImageIcon icon = new ImageIcon(resource);
            PopupMenu pop = new PopupMenu();

            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            pop.add(exitItem);

            TrayIcon tray = new TrayIcon(icon.getImage(), "自动会议", pop);
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

    private static String getTime(String str) {
        String regex = "[1-2][0-9][0-9][0-9]/([1][0-2]|0?[1-9])/([12][0-9]|3[01]|0?[1-9]) ([01]?[0-9]|[2][0-3]):[0-5]?[0-9]";
        String s = ReUtil.get(regex, str, 0);
        if (StrUtil.isBlank(s)) {
            JOptionPane.showMessageDialog(FRAME, "时间解析失败！请确保时间格式为【yyyy/MM/dd HH:mm】");
        }
        return s;
    }

    private static String getNum(String str) {
        String regex = "(\\d{3}-\\d{3}-\\d{3}(\\d)?)|(\\d{11})|(\\d{10})|(\\d{9})";
        String s = ReUtil.get(regex, str, 0);
        if (StrUtil.isBlank(s)) {
            JOptionPane.showMessageDialog(FRAME, "会议号解析失败！");
        }
        return s.replace("-", "");
    }

    protected static MettingDTO createMettingTask(MettingDTO mettingDTO) {
        String cron = mettingDTO.getCron();
        if (StrUtil.isBlank(cron)) {
            DateTime time = DateTime.of(mettingDTO.getTime());
            cron = StrUtil.format("0 {} {} {} {} ?", time.minute(), time.hour(true), time.dayOfMonth(), time.monthBaseOne());
        }
        String id = CronUtil.schedule(cron, new Task() {
            @Override
            public void execute() {
                startMetting(mettingDTO.getNum());
                for (MettingDTO dto : mettingList) {
                    if (dto.getNum().equals(mettingDTO.getNum())) {
                        dto.setStatus("会议时间到，进入会议！");
                        CronUtil.remove(dto.getId());
                    }
                }
                JOptionPane.showMessageDialog(FRAME, StrUtil.format("开始会议：【{}】\r\n备注：【{}】", mettingDTO.getNum(), mettingDTO.getDesc()));
            }
        });
        mettingDTO.setId(id);
        mettingDTO.setStatus("添加完成，等待会议开始！");
        JOptionPane.showMessageDialog(FRAME, StrUtil.format("开始任务\r\n会议号：【{}】\r\n会议时间：{}\r\n会议CRON：{}", mettingDTO.getNum(),
                DateUtil.formatChineseDate(mettingDTO.getTime(), false, true), cron));
        if (taskListWindow != null) {
            taskListWindow.updateTable();
        }
        return mettingDTO;
    }

    private static void startMetting(String num) {
        RuntimeUtil.execForStr("cmd", "/c", "start wemeet://page/inmeeting?meeting_code=" + num);
    }
}
