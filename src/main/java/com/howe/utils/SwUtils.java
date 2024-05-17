package com.howe.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.howe.Main;
import com.howe.dto.MettingDTO;
import com.howe.enums.StatusEnum;

import javax.swing.*;

/**
 * <p>@Author lu
 * <p>@Date 2024/5/17 上午11:22 星期五
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class SwUtils {

    public static void addWeekMetting(String num) {
        if (ConfigUtils.read().stream().anyMatch(m -> num.equals(m.getNum()))) {
            return;
        }
        MettingDTO mettingDTO = new MettingDTO("45661827839", "周例会");
        mettingDTO.setCron("0 53 16 * * 5");
        mettingDTO = createMettingTask(mettingDTO);
        ConfigUtils.add(mettingDTO);
    }


    public static MettingDTO createMettingTask(MettingDTO mettingDTO) {
        String num = mettingDTO.getNum();
        if (ConfigUtils.contains(num)) {
            return mettingDTO;
        }
        String cron = mettingDTO.getCron();
        if (StrUtil.isBlank(cron)) {
            DateTime time = DateTime.of(mettingDTO.getTime());
            cron = StrUtil.format("0 {} {} {} {} ?", time.minute(), time.hour(true), time.dayOfMonth(), time.monthBaseOne());
        }
        String id = CronUtil.schedule(cron, new Task() {
            @Override
            public void execute() {
                mettingDTO.setStatus(StatusEnum.TIME_UP);
                MettingDTO metting = ConfigUtils.update(mettingDTO);
                if (StrUtil.isNotBlank(metting.getNum())) {
                    CronUtil.remove(metting.getId());
                }
                startMetting(num);
                JOptionPane.showMessageDialog(null, StrUtil.format("开始会议：【{}】\r\n备注：【{}】", num, mettingDTO.getDesc()));
                mettingDTO.setStatus(StatusEnum.JOIN);
                ConfigUtils.update(mettingDTO);
            }
        });
        mettingDTO.setId(id);
        mettingDTO.setStatus(StatusEnum.ADD);
        JOptionPane.showMessageDialog(null, StrUtil.format("开始任务\r\n会议号：【{}】\r\n会议时间：{}\r\n会议CRON：{}", num,
                DateUtil.formatChineseDate(mettingDTO.getTime(), false, true), cron));
        return mettingDTO;
    }

    public static void startMetting(String num) {
        RuntimeUtil.execForStr("cmd", "/c", "start wemeet://page/inmeeting?meeting_code=" + num);
    }

}
