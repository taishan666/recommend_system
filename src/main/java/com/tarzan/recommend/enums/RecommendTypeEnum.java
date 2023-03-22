package com.tarzan.recommend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lenovo
 */
@Getter
@AllArgsConstructor
public enum RecommendTypeEnum {


    USER_CF("user-user", "基于用户的推荐算法"),
    ITEM_CF("item-item", "基于物品的推荐算法"),
    FUNK_SVD("funk-svd", "基于矩阵分解算法"),
    SLOPE_ONE("slope-one", "基于slope-one的推荐算法"),
    POPULAR("popular", "基于流行度的推荐算法"),
    ;

    private String type;
    private String name;
}
