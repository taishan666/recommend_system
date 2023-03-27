package com.tarzan.recommend.utils;

import com.google.common.base.Preconditions;
import com.tarzan.recommend.dao.FileDataDao;
import it.unimi.dsi.fastutil.longs.*;
import lombok.AllArgsConstructor;
import org.grouplens.lenskit.transform.threshold.RealThreshold;
import org.grouplens.lenskit.transform.threshold.Threshold;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.ratings.StandardRatingVectorPDAO;
import org.lenskit.knn.user.*;
import org.lenskit.similarity.CosineVectorSimilarity;
import org.lenskit.similarity.VectorSimilarity;
import org.lenskit.transform.normalize.DefaultUserVectorNormalizer;
import org.lenskit.transform.normalize.UserVectorNormalizer;
import org.lenskit.util.collections.SortedListAccumulator;
import org.lenskit.util.math.Vectors;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lenovo
 */
@Component
@AllArgsConstructor
public class NeighborUtil {



    /**
     * Find the neighbors for a user with respect to a collection of items.
     * For each item, the <var>neighborhoodSize</var> users closest to the
     * provided user are returned.
     *
     * @param user  The user's rating vector.
     * @param items The items for which neighborhoods are requested.
     * @return A mapping of item IDs to neighborhoods.
     */
    public Long2ObjectMap<List<Neighbor>>
    findNeighbors(long user, @Nonnull LongSet items) {
        Preconditions.checkNotNull(user, "user profile");
        Preconditions.checkNotNull(user, "item set");

        Long2ObjectOpenHashMap<SortedListAccumulator<Neighbor>> heaps = new Long2ObjectOpenHashMap<>(items.size());
        for (LongIterator iter = items.iterator(); iter.hasNext();) {
            long item = iter.nextLong();
            heaps.put(item, SortedListAccumulator.decreasing(20,
                    Neighbor.SIMILARITY_COMPARATOR));
        }

        for (Neighbor nbr: getCandidateNeighbors(user, items)) {
            // TODO consider optimizing
            for (Long2DoubleMap.Entry e: Vectors.fastEntries(nbr.vector)) {
                final long item = e.getLongKey();
                SortedListAccumulator<Neighbor> heap = heaps.get(item);
                if (heap != null) {
                    heap.add(nbr);
                }
            }
        }
        Long2ObjectMap<List<Neighbor>> neighbors = new Long2ObjectOpenHashMap<>();
        Iterator<Long2ObjectMap.Entry<SortedListAccumulator<Neighbor>>> hiter =
                heaps.long2ObjectEntrySet().fastIterator();
        while (hiter.hasNext()) {
            Long2ObjectMap.Entry<SortedListAccumulator<Neighbor>> me = hiter.next();
            neighbors.put(me.getLongKey(), me.getValue().finish());
        }
        return neighbors;
    }
    public Iterable<Neighbor> getCandidateNeighbors(long user, LongSet items) {
        DataAccessObject dao= FileDataDao.get();
        StandardRatingVectorPDAO rvd=new StandardRatingVectorPDAO(dao);
        VectorSimilarity sim=new CosineVectorSimilarity();
        UserSimilarity usim=new UserVectorSimilarity(sim);
        UserVectorNormalizer scoreNorm=new DefaultUserVectorNormalizer();
        Threshold thresh=new RealThreshold(0.0);
        NeighborFinder neighborFinder=new LiveNeighborFinder(rvd,dao,usim,scoreNorm,scoreNorm,thresh);
        return neighborFinder.getCandidateNeighbors(user,items);
    }
}
