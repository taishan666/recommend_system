package com.tarzan.recommend.test;

import com.tarzan.recommend.model.fiile.CsvFileDataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousUserDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnonymousTest {

    public static void main(String[] args) throws TasteException, IOException {
        DataModel model=new CsvFileDataModel(new File("C:\\Users\\Lenovo\\Desktop\\userPrefs.csv"));
        PreferenceArray anonymousPrefs  = new GenericUserPreferenceArray(3);
        anonymousPrefs .setUserID(0,PlusAnonymousUserDataModel.TEMP_USER_ID);
        anonymousPrefs .setItemID(0, 1L);
        anonymousPrefs .setValue(0, 1.0f);
        anonymousPrefs .setItemID(1, 2L);
        anonymousPrefs .setValue(1, 3.0f);
        anonymousPrefs .setItemID(2, 3L);
        anonymousPrefs .setValue(2, 2.0f);
        PlusAnonymousUserDataModel anonymousUserDataModel=new PlusAnonymousUserDataModel(model);
        anonymousUserDataModel.setTempPrefs(creatAnAnonymousPrefs());
        ItemSimilarity similarity = new PearsonCorrelationSimilarity(anonymousUserDataModel);
        // 构建基于物品的推荐系统
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(anonymousUserDataModel, similarity);
        List<RecommendedItem> list=  recommender.recommendedBecause(PlusAnonymousUserDataModel.TEMP_USER_ID,1L,10);
        list.forEach(e-> System.out.println(e));
    }

    //创建当前匿名用户的伪数据
    public static PreferenceArray creatAnAnonymousPrefs() {
        PreferenceArray anonymousPrefs =
                new GenericUserPreferenceArray(3);
        anonymousPrefs.setUserID(0, PlusAnonymousUserDataModel.TEMP_USER_ID);
        anonymousPrefs.setItemID(0, 123L);
        anonymousPrefs.setValue(0, 1.0f);
        anonymousPrefs.setItemID(1, 123L);
        anonymousPrefs.setValue(1, 3.0f);
        anonymousPrefs.setItemID(2, 123L);
        anonymousPrefs.setValue(2, 2.0f);
        return anonymousPrefs;
    }


}
