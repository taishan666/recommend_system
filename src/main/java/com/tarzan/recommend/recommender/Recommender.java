package com.tarzan.recommend.recommender;

import com.tarzan.recommend.config.RecommenderConfigBuilder;
import com.tarzan.recommend.data.FileDataDao;
import lombok.extern.slf4j.Slf4j;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.api.ItemRecommender;
import org.lenskit.data.dao.DataAccessObject;


/**
 * @author Lenovo
 */
@Slf4j
public class Recommender {

    public static ItemRecommender getItemRecommender(LenskitConfiguration config, DataAccessObject dao){
        //最后，获取并使用该推荐器。
        try (LenskitRecommender rec = LenskitRecommender.build(config, dao)) {
            rec.close();
            log.info("obtained recommender from engine");
            //我们想要推荐项
            return rec.getItemRecommender();
        }
    }

    public static ItemRecommender getItemRecommender(String type,DataAccessObject dao){
        LenskitConfiguration config= RecommenderConfigBuilder.getConfig(type);
        return getItemRecommender(config,dao);
    }

    public static ItemRecommender getItemRecommender(String type){
        DataAccessObject dao=FileDataDao.get();
        return getItemRecommender(type,dao);
    }

}
