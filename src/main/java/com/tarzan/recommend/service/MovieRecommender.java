package com.tarzan.recommend.service;

import com.tarzan.recommend.dto.MovieDTO;
import com.tarzan.recommend.model.fiile.MyFileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.AllSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ItemAverageRecommender;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tarzan
 */
public class MovieRecommender{

    static List<MovieDTO> movies= Collections.EMPTY_LIST;

    public static DataModel model;

    public static void loadMovies(){
        try {
            model=new MyFileDataModel(new File("C:\\Users\\Lenovo\\Desktop\\data\\ratings.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        movies=MyFileDataSource.getMovies();
    }

    public static List<MovieDTO> similarMovies(long movieId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
            AllSimilarItemsCandidateItemsStrategy strategy=new AllSimilarItemsCandidateItemsStrategy(similarity);
            // 构建基于物品的推荐系统
            GenericBooleanPrefItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model, similarity,strategy,strategy);
            List<RecommendedItem> items=  recommender.mostSimilarItems(movieId,size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            return movies.stream().filter(e->itemIds.contains(e.getMovieId())).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();

    }

    public static List<MovieDTO> similarMovies1(long movieId, int size){
        try {
            long old=System.currentTimeMillis();
            // 指定用户相似度计算方法，这里采用皮尔森相关度
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 构建基于物品的推荐系统
            GenericItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model, similarity);
            List<RecommendedItem> items=  recommender.mostSimilarItems(movieId,size);
            System.out.println("耗时 "+(System.currentTimeMillis()-old)+" ms");
            List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            return movies.stream().filter(e->itemIds.contains(e.getMovieId())).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
        }
        return Collections.emptyList();
    }

 /*   public static List<MovieDTO> similarMovies(int userId,long movieId, int size){
        List<RecommendedItem> items= CustomRecommender.itemCfRecommend(userId,movieId,size);
        List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return movies.stream().filter(e->itemIds.contains(e.getMovieId())).collect(Collectors.toList());
    }*/
}
