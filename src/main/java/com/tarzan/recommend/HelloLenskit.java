package com.tarzan.recommend;

import com.google.common.base.Throwables;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.*;
import org.lenskit.config.ConfigHelpers;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 演示应用程序的LensKit。这个应用程序构建一个项目-项目CF模型
 * 基于CSV文件数据，为用户生成推荐。
 *
 * @author tarzan 
 */
public class HelloLenskit implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HelloLenskit.class);

    public static void main(String[] args) {
        HelloLenskit hello = new HelloLenskit(args);
        try {
            hello.run();
        } catch (RuntimeException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private Path dataFile = Paths.get("data/movielens.yml");
    private List<Long> users;

    public HelloLenskit(String[] args) {
        users = new ArrayList<>(args.length);
        for (String arg: args) {
            users.add(Long.parseLong(arg));
        }
    }

    @Override
    public void run() {
        //我们首先需要配置数据访问。
        //我们将从静态数据源加载数据;您可以实现自己的DAO
        //在某种类型的数据库之上
        DataAccessObject dao;
        try {
            StaticDataSource data = StaticDataSource.load(dataFile);
            // 从DAO中获取数据
            dao = data.get();
        } catch (IOException e) {
            logger.error("cannot load data", e);
            throw Throwables.propagate(e);
        }

        //下一步:加载LensKit算法配置
        LenskitConfiguration config = null;
        try {
            config = ConfigHelpers.load(new File("etc/item-item.groovy"));
        } catch (IOException e) {
            throw new RuntimeException("could not load configuration", e);
        }

        //可以设置更多的参数、角色和组件。看到
        //每个推荐算法的JavaDoc以获取更多信息。
        //现在我们有了一个配置，从配置中构建一个推荐引擎
        //和数据源。这将计算相似度矩阵并返回一个推荐
        //使用它的引擎
        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, dao);
        logger.info("built recommender engine");
        //最后，获取并使用该推荐器。
        try (LenskitRecommender rec = engine.createRecommender(dao)) {
            logger.info("obtained recommender from engine");
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
            RatingPredictor pred = rec.getRatingPredictor();
            Result score = pred.predict(1, 40870);
            System.out.println(score.getId()+"  "+score.getScore());
        }
    }
}
