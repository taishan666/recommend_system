import org.lenskit.knn.user.UserUserItemScorer
import org.lenskit.transform.normalize.UserVectorNormalizer
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer

//配置项目记分器 使用bind和set方法
//这里，我们需要一个项目-项目记分器。用ItemItemScorer 作为 ItemScorer 的实现类
bind ItemScorer to UserUserItemScorer.class
// use item-user mean when user-user fails
bind (BaselineScorer,ItemScorer) to UserMeanItemScorer
bind (UserMeanBaseline,ItemScorer) to ItemMeanRatingItemScorer
// normalize by subtracting the user's mean rating
within (UserVectorNormalizer) {
    // use default normalizer, which uses a vector normalizer
    bind UserVectorNormalizer to DefaultUserVectorNormalizer
    // use the mean-variance normalizer
    bind VectorNormalizer to MeanVarianceVectorNormalizer
}
set NeighborhoodSize to 30
