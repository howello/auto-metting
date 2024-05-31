package com.howe.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import com.alibaba.fastjson2.JSONArray;
import com.formdev.flatlaf.util.StringUtils;
import com.howe.Main;
import com.howe.dto.MettingDTO;
import com.howe.enums.StatusEnum;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>@Author lu
 * <p>@Date 2024/5/17 上午10:51 星期五
 * <p>@Version 1.0
 * <p>@Description TODO
 */
public class ConfigUtils {
    private static final List<MettingDTO> METTING_LIST = new ArrayList<>();

    private static final String FILE_PATH = "./config.json";

    static {
        File file = FileUtil.touch(FILE_PATH);
        String s = FileUtil.readUtf8String(file);
        List<MettingDTO> list = JSONArray.parseArray(s, MettingDTO.class);
        if (CollUtil.isNotEmpty(list)) {
            for (MettingDTO metting : list) {
                if (StatusEnum.ADD.equals(metting.getStatus()) || "周例会".equals(metting.getDesc())) {
                    MettingDTO task = SwUtils.createMettingTask(metting);
                    METTING_LIST.add(task);
                } else {
                    METTING_LIST.add(metting);
                }
            }
        }
    }

    public static boolean contains(String num) {
        if (METTING_LIST.stream().anyMatch(m -> num.equals(m.getNum()))) {
            JOptionPane.showMessageDialog(null, "有这会议了，先删除了再添加吧");
            return true;
        }
        return false;
    }

    public static MettingDTO update(MettingDTO metting) {
        MettingDTO mettingDTO = METTING_LIST.stream().filter(m -> m.getNum().equals(metting.getNum()))
                .findFirst()
                .orElse(null);
        if (mettingDTO != null) {
            METTING_LIST.removeIf(m -> m.getNum().equals(mettingDTO.getNum()));
            BeanUtil.copyProperties(metting, mettingDTO);
            METTING_LIST.add(mettingDTO);
            write();
            return mettingDTO;
        }
        return metting;
    }

    public static void add(MettingDTO metting) {
        if (METTING_LIST.stream().noneMatch(m -> m.getNum().equals(metting.getNum()))) {
            METTING_LIST.add(metting);
            write();
        }
    }

    public static List<MettingDTO> read() {
        return METTING_LIST;
    }

    public static void remove(MettingDTO metting) {
        CronUtil.remove(metting.getId());
        METTING_LIST.removeIf(m -> StrUtil.isNotBlank(m.getNum()) && m.getNum().equals(metting.getNum()));
        METTING_LIST.removeIf(m -> StrUtil.isNotBlank(m.getId()) && m.getId().equals(metting.getId()));
        write();
    }

    private static void write() {
        FileUtil.writeUtf8String(JSONArray.toJSONString(METTING_LIST), FILE_PATH);
    }

}
