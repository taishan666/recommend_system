package com.tarzan.recommend;

import org.grouplens.lenskit.vectors.similarity.PearsonCorrelation;
import org.grouplens.lenskit.vectors.similarity.VectorSimilarity;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.EventDAO;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.eval.traintest.recommend.ItemSelector;
import org.lenskit.knn.MinNeighbors;
import org.lenskit.knn.item.ModelSize;
import org.lenskit.knn.user.UserSimilarity;
import org.lenskit.knn.user.UserSimilarityThreshold;
import org.lenskit.knn.user.UserUserItemScorer;
import org.lenskit.knn.user.UserVectorSimilarity;
import org.lenskit.transform.normalize.DefaultUserVectorNormalizer;
import org.lenskit.transform.normalize.UserVectorNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lenovo
 */
public class LenskitUBCF {
    private static final Logger logger = LoggerFactory.getLogger(LenskitDemo.class);

    public static void main(String[] args) throws IOException {
        // 配置Lenskit
        LenskitConfiguration config = new LenskitConfiguration();
        // 配置推荐列表大小
        config.bind(ItemScorer.class).to(UserUserItemScorer.class);
        config.set(MinNeighbors.class).to(2);
        config.set(ModelSize.class).to(1000);
       // 配置协同过滤算法，使用 Pearson 相似度
     //   config.bind(UserSimilarity.class).to(UserVectorSimilarity.class);
    //    config.bind(VectorSimilarity.class).to(PearsonCorrelation.class);
        // 配置用户向量归一化器
      //  config.bind(UserVectorNormalizer.class).to(DefaultUserVectorNormalizer.class);
        // 配置用户相似度阈值，过滤相似度低于该阈值的用户
      //  config.set(UserSimilarityThreshold.class).to(0.5);
      //  config.bind(AbstractItemScorer.class).to(UserUserItemScorer.class);
       // 配置使用最近邻算法进行基于用户的协同过滤推荐
      //  config.bind(UserUserItemScorer.class).to(KNNItemScorer.class);
        // 配置构建推荐列表所需的上下文信息
       // config.bind(ItemScorerContext.class).to(UserHistorySummarizer.class);
        // 配置使用基于用户的协同过滤算法构建推荐列表
      //  config.bind(BasicItemRecommender.class).to(UserUserItemRecommender.class);

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
            ItemRecommender irec = rec.getItemRecommender();
            //不为空，因为我们配置了一个
            assert irec != null;
            // 循环遍历用户集
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

}
