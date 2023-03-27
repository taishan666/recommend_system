package com.tarzan.recommend;

import com.tarzan.recommend.service.Recommend;
import com.tarzan.recommend.dto.ItemDTO;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RecommendSystemApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RecommendSystemApplication.class, args);
		System.out.println("------基于用户协同过滤推荐---------------下列电影");
		List<ItemDTO> itemList= Recommend.userCfRecommend(1);
		itemList.forEach(e-> System.out.println(e.getName()));
		System.out.println("------基于物品协同过滤推荐---------------下列电影");
		List<ItemDTO> itemList1= Recommend.itemCfRecommend(1);
		itemList1.forEach(e-> System.out.println(e.getName()));
	}

}
