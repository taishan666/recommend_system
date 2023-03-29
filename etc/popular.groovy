import org.lenskit.api.ItemRecommender
import org.lenskit.api.ItemScorer
import org.lenskit.basic.PopularItemRecommender
import org.lenskit.basic.PopularityRankItemScorer

bind ItemScorer to PopularityRankItemScorer
bind ItemRecommender to PopularItemRecommender