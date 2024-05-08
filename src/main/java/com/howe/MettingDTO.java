package com.howe;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>@Author lu
 * <p>@Date 2024/4/25 下午5:34 星期四
 * <p>@Version 1.0
 * <p>@Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MettingDTO {

    public MettingDTO(String num) {
        this.num = num;
    }

    public MettingDTO(String num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    private String id;

    private String num;

    private DateTime time;

    private String cron;

    private String desc;

    private String status;

    public Object[] genArr() {
        return new Object[]{num, time, desc, status};
    }
}
