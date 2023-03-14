import org.grouplens.lenskit.iterative.IterationCount
import org.grouplens.lenskit.iterative.StoppingCondition
import org.grouplens.lenskit.iterative.ThresholdStoppingCondition
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer
import org.lenskit.data.ratings.PreferenceDomain
import org.lenskit.mf.funksvd.FeatureCount
import org.lenskit.mf.funksvd.FunkSVDItemScorer
import org.lenskit.mf.funksvd.FunkSVDUpdateRule
import org.lenskit.mf.funksvd.RuntimeUpdate

bind ItemScorer to FunkSVDItemScorer
bind (BaselineScorer, ItemScorer) to UserMeanItemScorer
bind (UserMeanBaseline, ItemScorer) to ItemMeanRatingItemScorer
set FeatureCount to 25
set IterationCount to 125
//默认情况下，FunkSVD 会将每个特征的贡献固定在评级范围内。要禁用此功能，请隐藏评级范围：
within (FunkSVDUpdateRule) {
    bind PreferenceDomain to null
}
bind (RuntimeUpdate, FunkSVDUpdateRule) to FunkSVDUpdateRule
//您还可以使用上下文相关绑定来自定义运行时（计分时间，而不是模型时间）更新
within (RuntimeUpdate, FunkSVDUpdateRule) {
    bind StoppingCondition to ThresholdStoppingCondition
}