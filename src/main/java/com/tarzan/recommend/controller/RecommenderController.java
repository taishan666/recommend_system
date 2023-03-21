package com.tarzan.recommend.controller;

import com.tarzan.recommend.recommender.RecommenderFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lenskit.api.ItemRecommender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author TARZAN
 */
@Api(tags = "物品推荐接口")
@RestController
public class RecommenderController {


    @GetMapping("/recommend")
    @ApiOperation(value = "物品推荐",notes = "预置的type有 item-item;user-user;popular;slope-one;funk-svd;")
    public List<Long> getRecommendItem(int userId, int num,String type){
        ItemRecommender recommender= RecommenderFactory.getItemRecommender(type);
        return recommender.recommend(userId, num, null, null);
    }
}
