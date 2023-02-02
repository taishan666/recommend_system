package com.tarzan.recommend.service;

import com.tarzan.recommend.core.CoreMath;
import com.tarzan.recommend.dto.ItemDTO;
import com.tarzan.recommend.dto.RelateDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐服务
 *
 * @author TARZAN
 * @version 1.0
 * @date 2020/7/31$ 16:18$
 * @since JDK1.8
 */
public class Recommend{

    /**
     * 方法描述: 猜你喜欢
     *
     * @param userId 用户id
     * @Return {@link List<ItemDTO>}
     * @author tarzan
     * @date 2020年07月31日 17:28:06
     */
    public static List<ItemDTO>  guessUserLike(int userId){
        CoreMath coreMath = new CoreMath();
        List<RelateDTO> data= FileDataSource.getData();
        List<Integer> recommendations = coreMath.recommend(userId, data);
        return FileDataSource.getItemData().stream().filter(e->recommendations.contains(e.getId())).collect(Collectors.toList());
    }


}
