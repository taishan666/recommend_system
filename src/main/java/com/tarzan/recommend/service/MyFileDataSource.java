package com.tarzan.recommend.service;

import com.tarzan.recommend.service.FileDataSource;
import com.tarzan.recommend.dto.ItemDTO;
import com.tarzan.recommend.dto.MovieDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MyFileDataSource {

    public static void main(String[] args) {
        getMovies();
    }


    /**
     * 方法描述: 读取电影数据
     *
     * @Return {@link List< ItemDTO >}
     * @author tarzan
     * @date 2020年07月31日 16:54:22
     */
    public static List<MovieDTO> getMovies() {
        String folderPath= "C:\\Users\\Lenovo\\Desktop\\ml-latest";
        List<MovieDTO> movies = Lists.newArrayList();
        int count=0;
        try {
            FileInputStream out = new FileInputStream(folderPath+"\\movies.csv");
            InputStreamReader reader = new InputStreamReader(out, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                if(count>0){
                    String[] ht = line.split(",");
                    movies.add(new MovieDTO(Long.parseLong(ht[0]),ht[1],ht[2]));
                }
                count++;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return movies;
    }
}
