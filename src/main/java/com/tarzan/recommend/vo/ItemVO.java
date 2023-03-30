package com.tarzan.recommend.vo;

import lombok.Data;

/**
 * @author TARZAN
 */
@Data
public class ItemVO {
    private Long id;
    private String name;
    private String genres;
    private Double rating;
}
