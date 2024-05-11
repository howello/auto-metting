package com.howe;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>@Author lu
 * <p>@Date 2024/5/11 下午2:30 星期六
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class WindowFrame extends JFrame {
    private static JPanel aheadTimePanel, mettingNumPanel, mettingTimePanel, buttonPanel, mettingDataPanel;
    private static JLabel aheadTimeLabel, mettingDataLabel, mettingNumLabel, mettingTimeLabel, tipLabel, minuteLabel;
    private static JTextField aheadField, mettingNumField, mettingTimeField;
    private static JScrollPane mettingDataScrollPanel;
    private static JTextPane mettingDataField;
    private static JButton queryListBtn, submitBtn, parseDataBtn, startNowBtn;
    private static TaskListWindow taskListWindow;

    static {
        aheadTimePanel = new JPanel();
        mettingNumPanel = new JPanel();
        mettingTimePanel = new JPanel();
        buttonPanel = new JPanel();
        mettingDataPanel = new JPanel();

        aheadTimeLabel = new JLabel();
        mettingDataLabel = new JLabel();
        mettingNumLabel = new JLabel();
        mettingTimeLabel = new JLabel();
        tipLabel = new JLabel();
        minuteLabel = new JLabel();

        aheadField = new JTextField();
        mettingNumField = new JTextField();
        mettingTimeField = new JTextField();

        mettingDataScrollPanel = new JScrollPane();
        mettingDataField = new JTextPane();

        queryListBtn = new JButton();
        submitBtn = new JButton();
        parseDataBtn = new JButton();
        startNowBtn = new JButton();
    }


    public WindowFrame() throws HeadlessException {
        setTitle("自动会议");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(false);

        aheadTimePanel.setMaximumSize(new Dimension(2147483647, 50));
        aheadTimePanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        aheadTimePanel.setLayout(new BoxLayout(aheadTimePanel, BoxLayout.X_AXIS));

        aheadTimeLabel.setText("提前时间：");
        minuteLabel.setText("分");
        aheadField.setText("10");
        aheadTimePanel.add(aheadTimeLabel);
        aheadTimePanel.add(aheadField);
        aheadTimePanel.add(minuteLabel);
        add(aheadTimePanel);

        mettingNumPanel.setMaximumSize(new Dimension(2147483647, 50));
        mettingNumPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mettingNumPanel.setLayout(new BoxLayout(mettingNumPanel, BoxLayout.X_AXIS));

        mettingNumLabel.setText("会议号码：");
        mettingNumPanel.add(mettingNumLabel);
        mettingNumPanel.add(mettingNumField);
        add(mettingNumPanel);

        mettingTimePanel.setMaximumSize(new Dimension(2147483647, 50));
        mettingTimePanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mettingTimePanel.setLayout(new BoxLayout(mettingTimePanel, BoxLayout.X_AXIS));

        mettingTimeLabel.setText("会议时间：");
        mettingTimePanel.add(mettingTimeLabel);
        mettingTimePanel.add(mettingTimeField);
        add(mettingTimePanel);


        mettingDataPanel.setMaximumSize(new Dimension(2147483647, 300));
        mettingDataPanel.setMinimumSize(new Dimension(2147483647, 150));
        mettingDataPanel.setPreferredSize(new Dimension(2147483647, 150));
        mettingDataPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mettingDataPanel.setLayout(new BoxLayout(mettingDataPanel, BoxLayout.X_AXIS));
        mettingDataScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mettingDataScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mettingDataScrollPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
        mettingDataScrollPanel.setViewportView(mettingDataField);
        mettingDataLabel.setText("会议数据：");
        mettingDataPanel.add(mettingDataLabel);
        mettingDataPanel.add(mettingDataScrollPanel);
        add(mettingDataPanel);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        queryListBtn.setText("查看列表");
        queryListBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskListWindow = new TaskListWindow();
                taskListWindow.setVisible(true);
                setVisible(false);
            }
        });
        buttonPanel.add(queryListBtn);

        parseDataBtn.setText("解析数据");
        parseDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = mettingDataField.getText();
                String time = getTime(text);
                String num = getNum(text);
                mettingNumField.setText(num);
                mettingTimeField.setText(time);
                JOptionPane.showMessageDialog(getContentPane(), "解析完成，请注意页面是否已填充！");
            }
        });
        buttonPanel.add(parseDataBtn);

        submitBtn.setText("提交任务");
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = mettingTimeField.getText();
                try {
                    DateTime time = DateUtil.parse(text, "yyyy/MM/dd HH:mm");
                    if (time.isBefore(DateTime.now())) {
                        JOptionPane.showMessageDialog(getContentPane(), "这日子已经过去了，大哥");
                        return;
                    }
                    String num = mettingNumField.getText();
                    if (StrUtil.isBlank(num)) {
                        JOptionPane.showMessageDialog(getContentPane(), "会议号得有啊，大哥");
                        return;
                    }
                    if (Main.mettingList.stream().anyMatch(m -> num.equals(m.getNum()))) {
                        JOptionPane.showMessageDialog(getContentPane(), "有这会议了，先删除了再添加吧");
                        return;
                    }

                    MettingDTO mettingDTO = new MettingDTO(num, mettingDataField.getText());
                    String aheadNum = aheadField.getText();
                    if (StrUtil.isBlank(aheadNum)) {
                        aheadNum = "10";
                    }
                    DateTime offsetMinute = DateUtil.offsetMinute(time, (int) NumberUtil.sub(0, Integer.parseInt(aheadNum)));
                    mettingDTO.setTime(offsetMinute.isBeforeOrEquals(DateTime.now()) ? time : offsetMinute);
                    mettingDTO = createMettingTask(mettingDTO);
                    Main.mettingList.add(mettingDTO);
                    mettingNumField.setText("");
                    mettingTimeField.setText("");
                    mettingDataField.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(getContentPane(), "日期不对啊，大哥");
                }
            }
        });
        buttonPanel.add(submitBtn);

        startNowBtn.setText("立即开始");
        startNowBtn.addActionListener(e -> {
            String num = mettingNumField.getText();
            if (StrUtil.isBlank(num)) {
                JOptionPane.showMessageDialog(getContentPane(), "会议号为空");
                return;
            }
            startMetting(num);
        });
        buttonPanel.add(startNowBtn);

        add(buttonPanel);

        tipLabel.setText("日期格式：yyyy/MM/dd HH:mm");
        tipLabel.setForeground(Color.BLACK);
        tipLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        add(tipLabel);
        // 设置窗口属性
        setLocationRelativeTo(null);
        setVisible(true);


    }


    private String getTime(String str) {
        String regex = "[1-2][0-9][0-9][0-9]/([1][0-2]|0?[1-9])/([12][0-9]|3[01]|0?[1-9]) ([01]?[0-9]|[2][0-3]):[0-5]?[0-9]";
        String s = ReUtil.get(regex, str, 0);
        if (StrUtil.isBlank(s)) {
            JOptionPane.showMessageDialog(getContentPane(), "时间解析失败！请确保时间格式为【yyyy/MM/dd HH:mm】");
        }
        return s;
    }

    private String getNum(String str) {
        String regex = "(\\d{3}-\\d{3}-\\d{3}(\\d)?)|(\\d{11})|(\\d{10})|(\\d{9})";
        String s = ReUtil.get(regex, str, 0);
        if (StrUtil.isBlank(s)) {
            JOptionPane.showMessageDialog(getContentPane(), "会议号解析失败！");
        }
        return s.replace("-", "");
    }

    protected MettingDTO createMettingTask(MettingDTO mettingDTO) {
        String cron = mettingDTO.getCron();
        if (StrUtil.isBlank(cron)) {
            DateTime time = DateTime.of(mettingDTO.getTime());
            cron = StrUtil.format("0 {} {} {} {} ?", time.minute(), time.hour(true), time.dayOfMonth(), time.monthBaseOne());
        }
        String id = CronUtil.schedule(cron, new Task() {
            @Override
            public void execute() {
                startMetting(mettingDTO.getNum());
                for (MettingDTO dto : Main.mettingList) {
                    if (dto.getNum().equals(mettingDTO.getNum())) {
                        dto.setStatus("会议时间到，进入会议！");
                        CronUtil.remove(dto.getId());
                    }
                }
                JOptionPane.showMessageDialog(getContentPane(), StrUtil.format("开始会议：【{}】\r\n备注：【{}】", mettingDTO.getNum(), mettingDTO.getDesc()));
            }
        });
        mettingDTO.setId(id);
        mettingDTO.setStatus("添加完成，等待会议开始！");
        JOptionPane.showMessageDialog(getContentPane(), StrUtil.format("开始任务\r\n会议号：【{}】\r\n会议时间：{}\r\n会议CRON：{}", mettingDTO.getNum(),
                DateUtil.formatChineseDate(mettingDTO.getTime(), false, true), cron));
        if (taskListWindow != null) {
            taskListWindow.updateTable();
        }
        return mettingDTO;
    }

    public void startMetting(String num) {
        RuntimeUtil.execForStr("cmd", "/c", "start wemeet://page/inmeeting?meeting_code=" + num);
    }

}
