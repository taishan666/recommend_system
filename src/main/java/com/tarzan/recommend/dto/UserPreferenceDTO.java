package com.tarzan.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Lenovo
 */
@Data
@AllArgsConstructor
public class UserPreferenceDTO {

    private Long userId;
    private Long itemId;
    private Float preference;
}
