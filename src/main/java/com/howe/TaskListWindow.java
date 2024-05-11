package com.howe;

import cn.hutool.cron.CronUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * <p>@Author lu
 * <p>@Date 2024/4/25 下午6:07 星期四
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class TaskListWindow extends JFrame {
    private JTable table;

    private JPopupMenu popupMenu;

    public TaskListWindow() {
        createPopupMenu();
        setTitle("任务列表");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel defaultTableModel = updateTable();
        // 创建表格
        table = new JTable(defaultTableModel);
        table.setPreferredScrollableViewportSize(new Dimension(750, 500));
        table.setFillsViewportHeight(true);
        table.setDragEnabled(false);

        table.setDefaultRenderer(Object.class, new TableViewRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    int focusedRowIndex = table.rowAtPoint(evt.getPoint());
                    if (focusedRowIndex >= 0) {
                        table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
                    }
                    popupMenu.show(table, evt.getX(), evt.getY());
                } else {
                    Point point = evt.getPoint();
                    int row = table.rowAtPoint(point);
                    int col = table.columnAtPoint(point);
                    if (row >= 0 && col >= 0) {
                        Object value = table.getValueAt(row, col);
                        if (value != null) {
                            JOptionPane.showMessageDialog(table, value.toString());
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        pack();
        setLocationRelativeTo(Main.FRAME);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                Main.FRAME.setVisible(true);
            }
        });
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem addWeekMenItem = new JMenuItem();
        addWeekMenItem.setText("  添加周例会  ");
        addWeekMenItem.addActionListener(evt -> {
            String num = JOptionPane.showInputDialog(null, "输入周例会会议号：", "45661827839");
            if (Main.mettingList.stream().anyMatch(m -> num.equals(m.getNum()))) {
                JOptionPane.showMessageDialog(null, "有这会议了，先删除了再添加吧");
                return;
            }

            MettingDTO mettingDTO = new MettingDTO("45661827839", "周例会");
            mettingDTO.setCron("0 53 16 * * 5");
            mettingDTO = Main.FRAME.createMettingTask(mettingDTO);
            Main.mettingList.add(mettingDTO);
            updateTable();
        });
        popupMenu.add(addWeekMenItem);

        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText("  删除  ");
        delMenItem.addActionListener(evt -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                MettingDTO mettingDTO = Main.mettingList.get(selectedRow);
                CronUtil.remove(mettingDTO.getId());
                Main.mettingList.remove(selectedRow);
                ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
            }
        });
        popupMenu.add(delMenItem);

        JMenuItem startNowMenItem = new JMenuItem();
        startNowMenItem.setText("立即开始");
        startNowMenItem.addActionListener(evt -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                MettingDTO mettingDTO = Main.mettingList.get(selectedRow);
                int confirmDialog = JOptionPane.showConfirmDialog(getContentPane(), "要立即开始这个会议吗？\r\n会议号：" + mettingDTO.getNum(), "提示",
                        JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    Main.FRAME.startMetting(mettingDTO.getNum());
                }
            }
        });
        popupMenu.add(startNowMenItem);
    }

    public DefaultTableModel updateTable() {
        // 创建表格模型
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("会议号码");
        tableModel.addColumn("会议时间");
        tableModel.addColumn("描述");
        tableModel.addColumn("状态");

        List<MettingDTO> mettingList = Main.mettingList;
        for (MettingDTO mettingDTO : mettingList) {
            tableModel.addRow(mettingDTO.genArr());
        }
        if (table != null) {
            table.setModel(tableModel);
        }
        return tableModel;
    }

    class TableViewRenderer extends JTextArea implements TableCellRenderer {
        public TableViewRenderer() {
            //将表格设为自动换行
            setLineWrap(true); //利用JTextArea的自动换行方法
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        public Component getTableCellRendererComponent(JTable jtable, Object obj,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(obj == null ? "" : obj.toString());
            return this;
        }
    }
}
