package com.tarzan.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tarzan
 */
@EnableScheduling
@SpringBootApplication
public class RecommenderApplication {

    public static void main(String[] args){
        SpringApplication.run(RecommenderApplication.class, args);
    }

/*    public static void main(String[] args) {
        DataAccessObject  dao=FileDataDao.get();
        ItemRecommender irec= RecommenderFactory.getItemRecommender("popular",dao);
            //为该用户获取10个推荐
            ResultList recs = irec.recommendWithDetails(0, 3, null, null);
            System.out.format("Recommendations for user %d:\n", 1);
            for (Result item : recs) {
                Entity itemData = dao.lookupEntity(CommonTypes.ITEM, item.getId());
                String name = null;
                if (itemData != null) {
                    name = itemData.maybeGet(CommonAttributes.NAME);
                }
                System.out.format("\t%d (%s): %.2f\n", item.getId(), name, item.getScore());
            }

    }*/
}
