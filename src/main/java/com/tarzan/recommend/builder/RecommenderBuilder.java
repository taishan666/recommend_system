package com.tarzan.recommend.builder;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * @author tarzan
 */
public final class RecommenderBuilder {

    public static UserBasedRecommender buildUBRecommender(DataModel model) throws TasteException {
        UserSimilarity similarity = new CachingUserSimilarity(new EuclideanDistanceSimilarity(model), model);
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(20,similarity, model);
       return new GenericUserBasedRecommender(model, neighborhood, similarity);
    }

    public static ItemBasedRecommender buildIBRecommender(DataModel model) throws TasteException {
        ItemSimilarity similarity = new CachingItemSimilarity(new EuclideanDistanceSimilarity(model), model);
        return new GenericItemBasedRecommender(model, similarity);
    }

    public static SVDRecommender buildALSRecommender(DataModel model) throws TasteException {
        // 从文件加载数据
        ALSWRFactorizer factorizer = new ALSWRFactorizer(model, 50, 0.065, 15);
        return new SVDRecommender(model, factorizer);
    }

    public static SVDRecommender buildSVDRecommender(DataModel model) throws TasteException {
        // 从文件加载数据
        SVDPlusPlusFactorizer factorizer = new SVDPlusPlusFactorizer(model, 2, 2);
        return new SVDRecommender(model, factorizer);
    }

    public static ItemAverageRecommender buildIARecommender(DataModel model) throws TasteException {
        return new ItemAverageRecommender(model);
    }

    public static ItemUserAverageRecommender buildIUARecommender(DataModel model) throws TasteException {
        return new ItemUserAverageRecommender(model);
    }

    public static RandomRecommender buildRandomRecommender(DataModel model) throws TasteException {
        return new RandomRecommender(model);
    }




}
