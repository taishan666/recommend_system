package com.tarzan.recommend.demo;

import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.*;
import org.lenskit.baseline.BaselineScorer;
import org.lenskit.baseline.ItemMeanRatingItemScorer;
import org.lenskit.baseline.UserMeanBaseline;
import org.lenskit.baseline.UserMeanItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.knn.MinNeighbors;
import org.lenskit.knn.NeighborhoodSize;
import org.lenskit.knn.item.ItemItemItemBasedItemScorer;
import org.lenskit.knn.item.ItemItemScorer;
import org.lenskit.knn.item.ModelSize;
import org.lenskit.knn.user.UserUserItemScorer;
import org.lenskit.transform.normalize.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lenovo
 */
public class HelloLenskit1 {
    private static final Logger logger = LoggerFactory.getLogger(LenskitDemo.class);

    public static void main(String[] args) throws IOException {
        // 配置Lenskit
        LenskitConfiguration config = new LenskitConfiguration();
        config.bind(ItemBasedItemScorer.class).to(ItemItemItemBasedItemScorer.class);
        config.bind(BaselineScorer.class,ItemScorer.class).to(UserMeanItemScorer.class);
        config.bind(UserMeanBaseline.class,ItemScorer.class).to(ItemMeanRatingItemScorer.class);
        config.bind(UserVectorNormalizer.class).to(BaselineSubtractingUserVectorNormalizer.class);
        // 读取数据
        Path dataFile = Paths.get("data/movielens.yml");
        StaticDataSource data = StaticDataSource.load(dataFile);
        DataAccessObject dao = data.get();
        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, dao);
        logger.info("构建推荐引擎");
        List<Long> users = new ArrayList<>(args.length);
        for (String arg : args) {
            users.add(Long.parseLong(arg));
        }
        //最后，获取并使用该推荐器。
        try (LenskitRecommender rec = engine.createRecommender(dao)) {
            logger.info("从推荐引擎中获得推荐");
            //我们想要推荐项
            ItemBasedItemRecommender irec = rec.getItemBasedItemRecommender();
            //不为空，因为我们配置了一个
            assert irec != null;
            // 循环遍历用户集
            //为该用户获取10个推荐
            Set<Long> items=new HashSet<>(1);
            items.add(1L);
            ResultList recs = irec.recommendRelatedItemsWithDetails(items, 30, null, null);
            //  System.out.format("Recommendations for user %d:\n", user);
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
