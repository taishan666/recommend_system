package com.tarzan.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author TARZAN
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
   private  Long movieId;
   private  String title;
   private String genres;

}
