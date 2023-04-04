package com.tarzan.recommend.service;

import com.tarzan.recommend.model.fiile.MyFileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Lenovo
 */
public class CustomRecommender {


    public static DataModel model;

    public static void buildModel(){
        try {
            long old=System.currentTimeMillis();
            model=new MyFileDataModel(new File("C:\\Users\\Lenovo\\Desktop\\data\\ratings.csv"));
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long[] similarUserIDs(int userId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 指定用户邻居数量，这里为10
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            // 构建基于用户的推荐系统
            GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            long[] userIDs= recommender.mostSimilarUserIDs(userId, size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            return userIDs;
        } catch (Exception e) {
            System.out.println(e);
        }
        return new long[0];
    }

    public static List<RecommendedItem> userCfRecommend(int userId, int size){
        try {
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 指定用户邻居数量，这里为10
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            // 构建基于用户的推荐系统
            GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
         //   Recommender recommender =new  CachingRecommender(new GenericUserBasedRecommender(model, neighborhood, similarity));
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            return recommender.recommend(userId, size);
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }


    public static List<RecommendedItem> itemCfRecommend(int userId,long itemId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
            // 构建基于物品的推荐系统
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
            //Recommender recommender =new  CachingRecommender(new GenericItemBasedRecommender(model, similarity));
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            List<RecommendedItem> list= recommender.recommendedBecause(userId,itemId, size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            return  list;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }

    public static List<RecommendedItem> similarItems(long itemId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
            // 构建基于物品的推荐系统
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
            List<RecommendedItem> list=  recommender.mostSimilarItems(itemId,size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            return  list;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }

    public static List<RecommendedItem> randomRecommend(int userId, int size){
        try {
            // 构建基于用户的推荐系统
            Recommender recommender = new RandomRecommender(model);
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            return recommender.recommend(userId, size);
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }

    public static List<RecommendedItem> SVDRecommend(int userId, int size){
        try {
            // 从文件加载数据
            ALSWRFactorizer factorizer = new ALSWRFactorizer(model, 50, 0.065, 15);
            // 构建基于用户的推荐系统
            Recommender recommender = new SVDRecommender(model,factorizer);
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            return recommender.recommend(userId, size);
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }


}
