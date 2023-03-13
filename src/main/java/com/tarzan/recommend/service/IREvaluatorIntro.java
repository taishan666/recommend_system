package com.tarzan.recommend.service;

import com.tarzan.recommend.model.fiile.BXDataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;

public class IREvaluatorIntro {
    private IREvaluatorIntro() {
    }

    public static void main(String[] args) throws Exception {

        RandomUtils.useTestSeed();

        final DataModel model = new BXDataModel(new File("E:\\work_space\\recommend_system\\src\\main\\resources\\ml-100k\\u.data"));
        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

        RecommenderBuilder recommenderBuilder = model1 -> {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model1);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, similarity, model1);
            return new GenericUserBasedRecommender(model1, neighborhood, similarity);
        };

        IRStatistics stats = evaluator.evaluate(recommenderBuilder, null, model, null, 5, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

        System.out.println(stats.getPrecision());
        System.out.println(stats.getRecall());
        System.out.println(stats.getF1Measure());
    }
}

