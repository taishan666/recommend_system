
package com.tarzan.recommend.model.mongodb;


import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author Lenovo
 */
public final class MongoDBDataModel implements DataModel {

    private MongoTemplate mongoTemplate;
    private DataModel delegate;
    private Date mongoTimestamp;

    public MongoDBDataModel(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        buildModel();
    }

    private void buildModel() {
        mongoTimestamp = new Date(0);
        FastByIDMap<Collection<Preference>> userIDPrefMap = new FastByIDMap<Collection<Preference>>();
            FindIterable iterable = mongoTemplate.getCollection("items").find();
            MongoCursor cursor=iterable.cursor();
            while (cursor.hasNext()) {
                Map<String,Object> user = (Map<String,Object>) cursor.next();
                if (!user.containsKey("deleted_at")) {
                    long userID = (long) user.get("userId");
                    long itemID = (long) user.get("itemId");
                    double ratingValue = (double) user.get("preference");
                    Collection<Preference> userPrefs = userIDPrefMap.get(userID);
                    if (userPrefs == null) {
                        userPrefs = Lists.newArrayListWithCapacity(2);
                        userIDPrefMap.put(userID, userPrefs);
                    }
                    userPrefs.add(new GenericPreference(userID, itemID, (float) ratingValue));
                }
            }
        delegate = new GenericDataModel(GenericDataModel.toDataMap(userIDPrefMap, true));
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return delegate.getUserIDs();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long id) throws TasteException {
        return delegate.getPreferencesFromUser(id);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return delegate.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return delegate.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        return delegate.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID) throws TasteException {
        return delegate.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        return delegate.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return delegate.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return delegate.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return delegate.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
        return delegate.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePreference(long userID, long itemID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPreferenceValues() {
        return delegate.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return delegate.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return delegate.getMinPreference();
    }

    @Override
    public String toString() {
        return "MongoDBDataModel";
    }

    @Override
    public void refresh(Collection<Refreshable> collection) {

    }
}
