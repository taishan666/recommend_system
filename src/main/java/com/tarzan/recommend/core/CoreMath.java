package com.tarzan.recommend.core;

import com.tarzan.recommend.dto.RelateDTO;
import org.assertj.core.util.Lists;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 核心算法
 *
 * @author tarzan
 * @version 1.0
 * @company 洛阳图联科技有限公司
 * @copyright (c) 2019 LuoYang TuLian Co'Ltd Inc. All rights reserved.
 * @date 2020/7/31$ 15:21$
 * @since JDK1.8
 */
public class CoreMath {

    public List<Integer> recommend(Integer userId, List<RelateDTO> list) {
        //找到最近邻用户id
        Map<Double, Integer> distances = computeNearestNeighbor(userId, list);
        Integer nearest = distances.values().iterator().next();

        Map<Integer, List<RelateDTO>>  userMap=list.stream().collect(Collectors.groupingBy(RelateDTO::getUseId));

        //最近邻用户看过电影列表
        List<Integer>  neighborItemList = userMap.get(nearest).stream().map(e->e.getModuleId()).collect(Collectors.toList());
        //指定用户看过电影列表
        List<Integer>  userItemList  = userMap.get(userId).stream().map(e->e.getModuleId()).collect(Collectors.toList());;

        //找到最近邻看过，但是该用户没看过的电影，计算推荐，放入推荐列表
        List<Integer> recommendList = new ArrayList<>();
        for (Integer item : neighborItemList) {
            if (!userItemList.contains(item)) {
                recommendList.add(item);
            }
        }
        Collections.sort(recommendList);
        return recommendList;
    }


    /**
     * 在给定userId的情况下，计算其他用户和它的相关系数并排序
     * @param userId
     * @param list
     * @return
     */
    private Map<Double, Integer> computeNearestNeighbor(Integer userId, List<RelateDTO> list) {
        Map<Integer, List<RelateDTO>>  userMap=list.stream().collect(Collectors.groupingBy(RelateDTO::getUseId));
        Map<Double, Integer> distances = new TreeMap<>();
        userMap.forEach((k,v)->{
            if(k!=userId){
                double distance = pearson_dis(v,userMap.get(userId));
                distances.put(distance, k);
            }
        });
        return distances;
    }


    /**
     * 计算两个序列间的相关系数
     *
     * @param xList
     * @param yList
     * @return
     */
    private double pearson_dis(List<RelateDTO> xList, List<RelateDTO> yList) {
        List<Integer> xs= Lists.newArrayList();
        List<Integer> ys= Lists.newArrayList();
        xList.forEach(x->{
            yList.forEach(y->{
                if(x.getModuleId()==y.getModuleId()){
                    xs.add(x.getIndex());
                    ys.add(y.getIndex());
                }
            });
        });
        return getRelate(xs,ys);
    }

    /**
     * 方法描述: 皮尔森（pearson）相关系数计算
     *
     * @param xs
     * @param ys 
     * @Return {@link Double}
     * @throws 
     * @author tarzan
     * @date 2020年07月31日 17:03:20
     */
    public static Double getRelate(List<Integer> xs, List<Integer>  ys){
        int n=xs.size();
        double Ex= xs.stream().mapToDouble(x->x).sum();
        double Ey=ys.stream().mapToDouble(y->y).sum();
        double Ex2=xs.stream().mapToDouble(x->Math.pow(x,2)).sum();
        double Ey2=ys.stream().mapToDouble(y->Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->xs.get(i)*ys.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator=Math.sqrt((Ex2-Math.pow(Ex,2)/n)*(Ey2-Math.pow(Ey,2)/n));
        if (denominator==0) return 0.0;
        return numerator/denominator;
    }

}
