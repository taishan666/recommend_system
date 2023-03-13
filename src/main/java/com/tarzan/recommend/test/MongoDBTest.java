package com.tarzan.recommend.test;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.net.UnknownHostException;
import java.text.DateFormat;

/**
 * @author Lenovo
 */
public class MongoDBTest {

    public static void main(String[] args) throws UnknownHostException, TasteException {
        DataModel model=new MongoDBDataModel("119.167.159.215",27017,"recommend","items",false,false, DateFormat.getDateInstance(),"test","123456");
        // 指定用户相似度计算方法，这里采用皮尔森相关度
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        // 指定用户邻居数量，这里为10
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
        // 构建基于用户的推荐系统
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
        long[] userIDs= recommender.mostSimilarUserIDs(1L, 10);
        for (long userID : userIDs) {
            System.out.println(userID);
        }
    }
}
