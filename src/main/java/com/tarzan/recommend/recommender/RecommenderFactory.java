package com.tarzan.recommend.recommender;

import com.tarzan.recommend.config.RecommenderConfigBuilder;
import com.tarzan.recommend.data.FileDataDao;
import lombok.extern.slf4j.Slf4j;
import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.api.ItemRecommender;
import org.lenskit.data.dao.DataAccessObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Lenovo
 */
@Slf4j
public class RecommenderFactory {

    private static final Map<String, ItemRecommender> RECOMMENDER_POOL = new ConcurrentHashMap<>(10);

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
        if(RECOMMENDER_POOL.get(type)!=null){
            return RECOMMENDER_POOL.get(type);
        }else {
            ItemRecommender recommender=getItemRecommender(type,dao);
            RECOMMENDER_POOL.put(type,recommender);
            return recommender;
        }
    }

}
