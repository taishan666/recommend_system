package com.tarzan.recommend.service;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.Collection;
import java.util.List;

/**
 * @author Lenovo
 */
public final class MahoutRecommender implements Recommender {

    private final Recommender recommender;

    public MahoutRecommender(DataModel model) throws TasteException {
        UserSimilarity similarity = new CachingUserSimilarity(new EuclideanDistanceSimilarity(model), model);
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(10,similarity, model);
        recommender = new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
    }

    public MahoutRecommender(DataModel model,UserSimilarity userSimilarity) throws TasteException {
        UserSimilarity similarity = new CachingUserSimilarity(userSimilarity, model);
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(20,similarity, model);
        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    }


    @Override
    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
        return recommender.recommend(userID, howMany);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, boolean includeKnownItems) throws TasteException {
        return recommend(userID, howMany, null, includeKnownItems);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
        return recommender.recommend(userID, howMany, rescorer, false);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer, boolean includeKnownItems)
            throws TasteException {
        return recommender.recommend(userID, howMany, rescorer, false);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        return recommender.estimatePreference(userID, itemID);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        recommender.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        recommender.removePreference(userID, itemID);
    }

    @Override
    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }

    @Override
    public String toString() {
        return "MahoutRecommender[recommender:" + recommender + ']';
    }

}
