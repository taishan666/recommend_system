package com.tarzan.recommend.controller;

import com.tarzan.recommend.recommender.RecommenderFactory;
import org.lenskit.api.ItemRecommender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lenovo
 */
@RestController
public class RecommenderController {


    @GetMapping("/recommender")
    public List<Long> getRecommendItem(int userId, int num){
        ItemRecommender recommender= RecommenderFactory.getItemRecommender("popular");
        return recommender.recommend(userId, num, null, null);
    }
}
