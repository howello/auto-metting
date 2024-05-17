package com.howe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>@Author lu
 * <p>@Date 2024/5/17 上午10:44 星期五
 * <p>@Version 1.0
 * <p>@Description TODO
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    ADD(0,"添加完成，等待会议开始！"),
    TIME_UP(1,"会议时间到，准备进入会议！"),
    JOIN(2,"入会成功！")
    ;

    private int code;

    private String desc;
}
