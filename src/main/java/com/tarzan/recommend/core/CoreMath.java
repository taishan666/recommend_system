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
 * @date 2020/7/31$ 15:21$
 * @since JDK1.8
 */
public class CoreMath {

    /**
     * 方法描述: 推荐电影id列表
     *
     * @param userId 当前用户
     * @param list 用户电影评分数据
     * @return {@link List<Integer>}
     * @date 2023年02月02日 14:51:42
     */
    public List<Integer> recommend(Integer userId, List<RelateDTO> list) {
        //按用户分组
        Map<Integer, List<RelateDTO>>  userMap=list.stream().collect(Collectors.groupingBy(RelateDTO::getUseId));
        //获取其他用户与当前用户的关系值
        Map<Integer,Double>  userDisMap = computeNeighbor(userId, userMap);
        //获取关系最近的用户
        double maxvalue=Collections.max(userDisMap.values());
        Set<Integer> userIds=userDisMap.entrySet().stream().filter(e->e.getValue()==maxvalue).map(Map.Entry::getKey).collect(Collectors.toSet());
        //取关系最近的用户
        Integer nearestUserId = userIds.stream().findAny().get();
        //最近邻用户看过电影列表
        List<Integer>  neighborItems = userMap.get(nearestUserId).stream().map(RelateDTO::getModuleId).collect(Collectors.toList());
        //指定用户看过电影列表
        List<Integer>  userItems  = userMap.get(userId).stream().map(RelateDTO::getModuleId).collect(Collectors.toList());
        //找到最近邻看过，但是该用户没看过的电影
        neighborItems.removeAll(userItems);
        return neighborItems;
    }


    /**
     * 在给定userId的情况下，计算其他用户和它的相关系数并排序
     * @param userId 用户id
     * @param userMap 用户电影评分关系mqp
     * @return Map<Integer,Double>
     */
    private Map<Integer,Double> computeNeighbor(Integer userId, Map<Integer,List<RelateDTO>>  userMap) {
        Map<Integer,Double> userDisMap = new TreeMap<>();
        List<RelateDTO> userItems=userMap.get(userId);
        userMap.forEach((k,v)->{
            //排除此用户
            if(!k.equals(userId)){
                //关系系数
                double coefficient = pearsonDis(v,userItems);
                //关系距离
                double distance=Math.abs(coefficient);
                userDisMap.put(k,distance);
            }
        });
        return userDisMap;
    }


    /**
     * 计算两个序列间的相关系数
     *
     * @param xList 用户1喜欢的电影
     * @param yList 用户2喜欢的电影
     * @return double
     */
    private double pearsonDis(List<RelateDTO> xList, List<RelateDTO> yList) {
        List<Integer> xs= Lists.newArrayList();
        List<Integer> ys= Lists.newArrayList();
        xList.forEach(x->{
            yList.forEach(y->{
                if(x.getModuleId().equals(y.getModuleId())){
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
     * @param xs x集合
     * @param ys y集合
     * @Return {@link double}
     * @author tarzan
     * @date 2020年07月31日 17:03:20
     */
    public static double getRelate(List<Integer> xs, List<Integer> ys){
        int n=xs.size();
        if (n==0) {
            return 0D;
        }
        double Ex= xs.stream().mapToDouble(x->x).sum();
        double Ey=ys.stream().mapToDouble(y->y).sum();
        double Ex2=xs.stream().mapToDouble(x->Math.pow(x,2)).sum();
        double Ey2=ys.stream().mapToDouble(y->Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->xs.get(i)*ys.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator=Math.sqrt((Ex2-Math.pow(Ex,2)/n)*(Ey2-Math.pow(Ey,2)/n));
        if (denominator==0) {
            return 0D;
        }
        return numerator/denominator;
    }

}
