package com.tarzan.recommend.service;

import com.tarzan.recommend.dto.MovieDTO;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tarzan
 */
public class MovieRecommender{

    static List<MovieDTO> movies= Collections.EMPTY_LIST;

    public static void loadMovies(){
        CustomRecommender.buildModel();
        movies=MyFileDataSource.getMovies();
    }

    public static List<MovieDTO> similarMovies(long movieId, int size){
        List<RecommendedItem> items= CustomRecommender.similarItems(movieId,size);
        List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return movies.stream().filter(e->itemIds.contains(e.getMovieId())).collect(Collectors.toList());
    }

    public static List<MovieDTO> similarMovies(int userId,long movieId, int size){
        List<RecommendedItem> items= CustomRecommender.itemCfRecommend(userId,movieId,size);
        List<Long> itemIds= items.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return movies.stream().filter(e->itemIds.contains(e.getMovieId())).collect(Collectors.toList());
    }
}
