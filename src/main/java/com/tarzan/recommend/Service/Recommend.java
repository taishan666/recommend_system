package com.tarzan.recommend.Service;

import com.tarzan.recommend.core.CoreMath;
import com.tarzan.recommend.dto.ItemDTO;
import com.tarzan.recommend.dto.RelateDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐服务
 *
 * @author liu yapeng
 * @version 1.0
 * @copyright (c) 2019 LuoYang TuLian Co'Ltd Inc. All rights reserved.
 * @date 2020/7/31$ 16:18$
 * @since JDK1.8
 */
public class Recommend{


    /**
     * 方法描述: 猜你喜欢
     *
     * @param
     * @Return {@link List< ItemDTO>}
     * @throws
     * @author tarzan
     * @date 2020年07月31日 17:28:06
     */
    public static List<ItemDTO>  guessYouLike(){
        CoreMath coreMath = new CoreMath();
        List<RelateDTO> data= FileDataSource.getData();
        List<Integer> recommendations = coreMath.recommend(2, data);
        List<ItemDTO> itemList= FileDataSource.getItemData().stream().filter(e->recommendations.contains(e.getId())).collect(Collectors.toList());
        return itemList;
    }

    public static void main(String[] args) {
        List<ItemDTO> itemList= Recommend.guessYouLike();
        System.out.println("------猜你可能喜欢---------------下列电影="+itemList.stream().map(e->e.getName()).collect(Collectors.toList()));
    }

}
