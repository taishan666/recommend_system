package com.tarzan.recommend.service;

import com.tarzan.recommend.demo.service.FileDataSource;
import com.tarzan.recommend.demo.dto.ItemDTO;
import com.tarzan.recommend.model.fiile.BXDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MoviesRecommender {

    static DataModel  model;

    public static void main(String[] args) {
        buildModel();
  /*      List<ItemDTO> items= similarMovies(1689,10);
        for (ItemDTO item : items) {
            System.out.println(item);
        }*/
        similarUsers(999,10);
    }

    public static void buildModel(){
        try {
              model=new BXDataModel(new File("E:\\work_space\\recommend_system\\src\\main\\resources\\ml-100k\\u.data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadMovies(){
        CustomRecommender.buildModel();
      //  movies=MyFileDataSource.getMovies();
    }

    public static List<ItemDTO> similarMovies(long movieId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 构建基于物品的推荐系统
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
            List<RecommendedItem> items=  recommender.mostSimilarItems(movieId,size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            List<ItemDTO> result=new ArrayList<>(10);
            List<ItemDTO> itemAll= FileDataSource.getItemData();
            itemIds.forEach(e->result.add(itemAll.stream().filter(a-> a.getId().equals(e)).findFirst().get()));
            return result;
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }

    public static long[] similarUsers(long userId, int size){
        try {
            model.setPreference(999L,1L,3.0f);
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 指定用户邻居数量，这里为10
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            // 构建基于用户的推荐系统
            GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            long[] userIDs= recommender.mostSimilarUserIDs(userId, size);
            for (long userID : userIDs) {
                System.out.println(userID);
            }
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            return userIDs;
        } catch (Exception e) {
            System.out.println(e);
        }
        return new long[0];
    }


}
