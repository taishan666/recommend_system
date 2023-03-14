package com.tarzan.recommend;

import com.tarzan.recommend.data.FileDataDao;
import com.tarzan.recommend.recommender.Recommender;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarzan
 */
public class RecommenderApplication {

    public static void main(String[] args) {
        List<Long> users = new ArrayList<>(args.length);
        for (String arg : args) {
            users.add(Long.parseLong(arg));
        }
        DataAccessObject  dao=FileDataDao.get();
        ItemRecommender irec=Recommender.getItemRecommender("popular",dao);
        for (long user : users) {
            //为该用户获取10个推荐
            ResultList recs = irec.recommendWithDetails(user, 3, null, null);
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
