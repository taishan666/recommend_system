package com.tarzan.recommend.demo;

import com.tarzan.recommend.data.FileDataDao;
import com.tarzan.recommend.recommender.RecommenderFactory;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.hybrid.RankBlendingItemRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lenovo
 */
public class LenskitHybrid {

    private static final Logger logger = LoggerFactory.getLogger(LenskitDemo.class);

    public static void main(String[] args) throws IOException {
        List<Long> users = new ArrayList<>(args.length);
        for (String arg : args) {
            users.add(Long.parseLong(arg));
        }
        DataAccessObject  dao= FileDataDao.get();
        ItemRecommender irec1= RecommenderFactory.getItemRecommender("popular",dao);
        ItemRecommender irec2= RecommenderFactory.getItemRecommender("user-user",dao);
        RankBlendingItemRecommender irec=new RankBlendingItemRecommender(irec1,irec2,0.9D);
        for (long user : users) {
            //为该用户获取10个推荐
            ResultList recs = irec.recommendWithDetails(user, 5, null, null);
            System.out.format("Recommendations for user %d:\n", user);
            for (Result item : recs) {
                Entity itemData = dao.lookupEntity(CommonTypes.ITEM, item.getId());
                String name = null;
                if (itemData != null) {
                    name = itemData.maybeGet(CommonAttributes.NAME);
                }
                System.out.format("\t%d (%s): %.2f\n", item.getId(), name, item.getScore());
            }
        }
    }
}
