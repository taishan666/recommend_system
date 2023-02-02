package com.tarzan.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系数据
 *
 * @author tarzan
 * @version 1.0
 * @date 2020/7/31$ 14:51$
 * @since JDK1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelateDTO {
    /** 用户id */
    private Integer useId;
    /** 业务id */
    private Integer moduleId;
    /** 指数 */
    private Integer index;


}
