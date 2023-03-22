package com.tarzan.recommend.controller;

import com.tarzan.recommend.enums.RecommendTypeEnum;
import com.tarzan.recommend.recommender.RecommenderFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lenskit.LenskitRecommender;
import org.lenskit.api.ItemBasedItemRecommender;
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


    @GetMapping("/user/recommend")
    @ApiOperation(value = "根据用户进行物品推荐",notes = "预置的type有 item-item;user-user;popular;slope-one;funk-svd;")
    public List<Long> getRecommendUser(int userId, int num,String type){
        LenskitRecommender recommender= RecommenderFactory.getRecommender(type);
        ItemRecommender itemRecommender=recommender.getItemRecommender();
        return itemRecommender.recommend(userId, num, null, null);
    }

    @GetMapping("item/recommend")
    @ApiOperation(value = "根据物品进行物品推荐")
    public List<Long> getRecommendItem(long itemId, int num){
        LenskitRecommender recommender= RecommenderFactory.getRecommender(RecommendTypeEnum.ITEM_CF.getType());
        ItemBasedItemRecommender itemRecommender=recommender.getItemBasedItemRecommender();
        return itemRecommender.recommendRelatedItems(itemId, num);
    }
}
