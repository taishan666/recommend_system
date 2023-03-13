package com.tarzan.recommend.test;

import com.tarzan.recommend.dto.MovieDTO;
import com.tarzan.recommend.service.MovieRecommender;

import java.util.List;

public class TestDB {

    public static void main(String[] args) {
        MovieRecommender.loadMovies();
        List<MovieDTO> items1= MovieRecommender.similarMovies(1,10);
        System.out.println("-------------------------------------------");
        items1.forEach(e-> System.out.println(e));


        List<MovieDTO> items2=MovieRecommender.similarMovies(1,1,10);
        System.out.println("-------------------------------------------");
        items2.forEach(e-> System.out.println(e));

        List<MovieDTO> items3=MovieRecommender.similarMovies(2,1,10);
        System.out.println("-------------------------------------------");
        items3.forEach(e-> System.out.println(e));


    }
}
