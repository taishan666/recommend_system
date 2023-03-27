import org.lenskit.knn.NeighborhoodSize
import org.lenskit.knn.user.LiveNeighborFinder
import org.lenskit.knn.user.NeighborFinder
import org.lenskit.knn.user.UserUserItemScorer
import org.lenskit.transform.normalize.DefaultUserVectorNormalizer
import org.lenskit.transform.normalize.MeanVarianceNormalizer
import org.lenskit.transform.normalize.UserVectorNormalizer
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer
import org.lenskit.transform.normalize.VectorNormalizer

//配置项目记分器 使用bind和set方法
//这里，我们需要一个项目-项目记分器。用ItemItemScorer 作为 ItemScorer 的实现类
bind ItemScorer to UserUserItemScorer.class

bind NeighborFinder to LiveNeighborFinder.class
// 当用户-用户失败时，使用物品-用户平均值
bind (BaselineScorer,ItemScorer) to UserMeanItemScorer
bind (UserMeanBaseline,ItemScorer) to ItemMeanRatingItemScorer
// 通过减去用户的平均评分进行归一化
within (UserVectorNormalizer) {
    //使用默认的归一化器，该归一化器使用向量归一化器。
    bind UserVectorNormalizer to DefaultUserVectorNormalizer
    // 使用均值方差归一化器。
    bind VectorNormalizer to MeanVarianceNormalizer
}
set NeighborhoodSize to 30
