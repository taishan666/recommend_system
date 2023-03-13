import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer

bind ItemScorer to FunkSVDItemScorer
bind (BaselineScorer, ItemScorer) to UserMeanItemScorer
bind (UserMeanBaseline, ItemScorer) to ItemMeanRatingItemScorer
set FeatureCount to 25
set IterationCount to 125
//默认情况下，FunkSVD 会将每个特征的贡献固定在评级范围内。要禁用此功能，请隐藏评级范围：
within (FunkSVDUpdateRule) {
    bind PreferenceDomain to null
}
//运行时训练
bind (RuntimeUpdate, FunkSVDUpateRule) to FunkSVDUpdateRule
//您还可以使用上下文相关绑定来自定义运行时（计分时间，而不是模型时间）更新
within (RuntimeUpdate, FunkSVDUpdateRule) {
    bind StoppingCondition to ThresholdStoppingCondition
}