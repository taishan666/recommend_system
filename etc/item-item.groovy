import org.lenskit.api.ItemBasedItemScorer
import org.lenskit.knn.item.ItemItemItemBasedItemScorer
import org.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer
import org.lenskit.transform.normalize.UserVectorNormalizer
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer
import org.lenskit.knn.MinNeighbors
import org.lenskit.knn.item.ItemItemScorer
import org.lenskit.knn.item.ModelSize

//配置项目记分器 使用bind和set方法
//这里，我们需要一个项目-项目记分器。用ItemItemScorer 作为 ItemScorer 的实现类
bind ItemScorer to ItemItemScorer.class
bind ItemBasedItemScorer to ItemItemItemBasedItemScorer.class
// 基于物品的推荐系统在具有最小邻居计数时效果最佳。
set MinNeighbors to 2

//限制模型大小
set ModelSize to 1000

//让我们使用个性化的平均评级作为基线/后备预测器。
// 这里需要2步过程:
//首先，使用用户平均评分作为基线评分
bind (BaselineScorer, ItemScorer) to UserMeanItemScorer
//其次，使用项目平均值评分作为用户平均值的基础
bind (UserMeanBaseline, ItemScorer) to ItemMeanRatingItemScorer
// and normalize ratings by baseline prior to computing similarities
//在计算相似度之前通过基线优化对评分进行归一化
bind UserVectorNormalizer to BaselineSubtractingUserVectorNormalizer

