package com.tarzan.recommend.model.fiile;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.Collection;

/**
 * @author Lenovo
 */
public class CustomDataModel extends AbstractDataModel {

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return null;
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long l) throws TasteException {
        return null;
    }

    @Override
    public FastIDSet getItemIDsFromUser(long l) throws TasteException {
        return null;
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return null;
    }

    @Override
    public PreferenceArray getPreferencesForItem(long l) throws TasteException {
        return null;
    }

    @Override
    public Float getPreferenceValue(long l, long l1) throws TasteException {
        return null;
    }

    @Override
    public Long getPreferenceTime(long l, long l1) throws TasteException {
        return null;
    }

    @Override
    public int getNumItems() throws TasteException {
        return 0;
    }

    @Override
    public int getNumUsers() throws TasteException {
        return 0;
    }

    @Override
    public int getNumUsersWithPreferenceFor(long l) throws TasteException {
        return 0;
    }

    @Override
    public int getNumUsersWithPreferenceFor(long l, long l1) throws TasteException {
        return 0;
    }

    @Override
    public void setPreference(long l, long l1, float v) throws TasteException {

    }

    @Override
    public void removePreference(long l, long l1) throws TasteException {

    }

    @Override
    public boolean hasPreferenceValues() {
        return false;
    }

    @Override
    public void refresh(Collection<Refreshable> collection) {

    }
}
