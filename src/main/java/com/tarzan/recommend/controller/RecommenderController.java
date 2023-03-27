package com.tarzan.recommend.controller;

import com.tarzan.recommend.dao.FileDataDao;
import com.tarzan.recommend.enums.RecommendTypeEnum;
import com.tarzan.recommend.recommender.RecommenderFactory;
import com.tarzan.recommend.utils.NeighborUtil;
import com.tarzan.recommend.vo.ItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.unimi.dsi.fastutil.longs.*;
import lombok.AllArgsConstructor;
import org.lenskit.LenskitRecommender;
import org.lenskit.api.ItemBasedItemRecommender;
import org.lenskit.api.ItemRecommender;
import org.lenskit.data.dao.*;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.entities.TypedName;
import org.lenskit.knn.user.*;
import org.lenskit.util.io.ObjectStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TARZAN
 */
@Api(tags = "物品推荐接口")
@RestController
@AllArgsConstructor
public class RecommenderController {

    private final NeighborUtil neighborUtil;


    @GetMapping("/user/recommend")
    @ApiOperation(value = "根据用户进行物品推荐",notes = "预置的type有 item-item;user-user;popular;slope-one;funk-svd;")
    public List<ItemVO> getRecommendUser(int userId, int num, String type){
        DataAccessObject  dao=FileDataDao.get();
        LenskitRecommender recommender= RecommenderFactory.getRecommender(type);
        ItemRecommender itemRecommender=recommender.getItemRecommender();
        List<Long> itemIds= itemRecommender.recommend(userId, num, null, null);
        List<ItemVO> items=new ArrayList<>(10);
        itemIds.forEach(id->{
            ItemVO vo=new ItemVO();
            vo.setId(id);
            Entity itemData = dao.lookupEntity(CommonTypes.ITEM, id);
            vo.setName(itemData.maybeGet(CommonAttributes.NAME));
            vo.setGenres(itemData.maybeGet(TypedName.create("genres", String.class)));
            items.add(vo);
        });
        return items;
    }

    @GetMapping("/user/rating")
    @ApiOperation(value = "用户历史物品评分")
    public List<ItemVO> userRating(int userId){
        DataAccessObject  dao=FileDataDao.get();
        EntityQueryBuilder builder=new EntityQueryBuilder(CommonTypes.RATING);
        builder.addFilterField(CommonAttributes.USER_ID,(long)userId);
        builder.addSortKey(CommonAttributes.RATING, SortOrder.DESCENDING);
        ObjectStream<Entity> stream = dao.streamEntities(builder.build());
        List<ItemVO> items=new ArrayList<>(10);
        stream.forEach(e->{
            ItemVO vo=new ItemVO();
            vo.setId(e.getLong(CommonAttributes.ITEM_ID));
            Entity itemData = dao.lookupEntity(CommonTypes.ITEM, vo.getId());
            vo.setName(itemData.maybeGet(CommonAttributes.NAME));
            vo.setGenres(itemData.maybeGet( TypedName.create("genres", String.class)));
            items.add(vo);
        });
        return items;
    }


    @GetMapping("item/recommend")
    @ApiOperation(value = "根据物品进行物品推荐")
    public List<Long> getRecommendItem(long itemId, int num){
        LenskitRecommender recommender= RecommenderFactory.getRecommender(RecommendTypeEnum.ITEM_CF.getType());
        ItemBasedItemRecommender itemRecommender=recommender.getItemBasedItemRecommender();
        return itemRecommender.recommendRelatedItems(itemId, num);
    }

    @GetMapping("test")
    @ApiOperation(value = "test")
    public List<Long> test(long user, int num){
        DataAccessObject  dao=FileDataDao.get();
        Long2ObjectMap<List<Neighbor>> map=neighborUtil.findNeighbors(user,null);
        System.out.println(map);
        return null;
    }


}
