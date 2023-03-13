package com.tarzan.recommend.listener;

import com.google.common.base.Splitter;
import com.tarzan.recommend.dto.UserPreferenceDTO;
import com.tarzan.recommend.model.mongodb.MongoDBDataModel;
import com.tarzan.recommend.mongodb.MongoAsyncService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author tarzan
 * @date 2021-10-05
 */
@Slf4j
@Component
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartedListener implements ApplicationListener<ApplicationStartedEvent> {


    private final MongoTemplate mongoTemplate;

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        printStartInfo(event);
        MongoDBDataModel model=new MongoDBDataModel(mongoTemplate);
        // 指定用户相似度计算方法，这里采用皮尔森相关度
        UserSimilarity similarity = null;
        try {
            similarity = new PearsonCorrelationSimilarity(model);
            // 指定用户邻居数量，这里为10
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
            // 构建基于用户的推荐系统
            GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            // 得到指定用户的推荐结果，这里是得到用户1的两个推荐
            long[] userIDs= recommender.mostSimilarUserIDs(1L, 10);
            for (long userID : userIDs) {
                System.out.println(userID);
            }
        } catch (TasteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打印信息
     */
    private void printStartInfo(ApplicationStartedEvent event) {
        ConfigurableApplicationContext context=event.getApplicationContext();
        Environment env = context.getEnvironment();
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + contextPath + "\n\t" +
                "External: \thttp://" + ip + ':' + port + contextPath + '\n' +
                "----------------------------------------------------------");
    }

}
