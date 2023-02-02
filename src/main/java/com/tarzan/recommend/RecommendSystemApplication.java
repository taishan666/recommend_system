package com.tarzan.recommend;

import com.tarzan.recommend.service.Recommend;
import com.tarzan.recommend.dto.ItemDTO;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RecommendSystemApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RecommendSystemApplication.class, args);
		List<ItemDTO> itemList= Recommend.guessUserLike(2);
		System.out.println("------猜你可能喜欢---------------下列电影");
		itemList.forEach(e-> System.out.println(e.getName()));
	}

}
