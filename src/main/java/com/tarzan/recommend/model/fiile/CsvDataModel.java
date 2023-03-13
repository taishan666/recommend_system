package com.tarzan.recommend.model.fiile;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.*;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CsvDataModel extends AbstractDataModel {

    private static final Logger log = LoggerFactory.getLogger(FileDataModel.class);

    public static final long DEFAULT_MIN_RELOAD_INTERVAL_MS = 60 * 1000L; // 1 minute?
    private static final char COMMENT_CHAR = '#';
    private static final char[] DELIMIETERS = {',', '\t'};

    private final File dataFile;
    private long lastModified;
    private long lastUpdateFileModified;
    private final char delimiter;
    private final Splitter delimiterPattern;
    private final boolean hasPrefValues;
    private DataModel delegate;
    private final ReentrantLock reloadLock;
    private final boolean transpose;
    private boolean skipFirstLine;
    private final long minReloadIntervalMS;

    /**
     * @param dataFile
     *          file containing preferences data. If file is compressed (and name ends in .gz or .zip
     *          accordingly) it will be decompressed as it is read)
     * @param skipFirstLine
     * @throws FileNotFoundException
     *           if dataFile does not exist
     * @throws IOException
     *           if file can't be read
     */
    public CsvDataModel(File dataFile, boolean skipFirstLine) throws IOException {
        this(dataFile, false, DEFAULT_MIN_RELOAD_INTERVAL_MS, skipFirstLine);
    }

    /**
     * @param delimiterRegex If your data file don't use '\t' or ',' as delimiter, you can specify
     * a custom regex pattern.
     */
    public CsvDataModel(File dataFile, String delimiterRegex) throws IOException {
        this(dataFile, false, DEFAULT_MIN_RELOAD_INTERVAL_MS, delimiterRegex,false);
    }

    /**
     * @param transpose
     *          transposes user IDs and item IDs -- convenient for 'flipping' the data model this way
     * @param minReloadIntervalMS
     *  the minimum interval in milliseconds after which a full reload of the original datafile is done
     *  when refresh() is called
     */
    public CsvDataModel(File dataFile, boolean transpose, long minReloadIntervalMS,boolean skipFirstLine) throws IOException {
        this(dataFile, transpose, minReloadIntervalMS, null,skipFirstLine);
    }

    /**
     * @param delimiterRegex If your data file don't use '\t' or ',' as delimiters, you can specify
     * user own using regex pattern.
     * @throws IOException
     */
    public CsvDataModel(File dataFile, boolean transpose, long minReloadIntervalMS, String delimiterRegex,boolean skipFirstLine)
            throws IOException {

        this.dataFile = Preconditions.checkNotNull(dataFile.getAbsoluteFile());
        if (!dataFile.exists() || dataFile.isDirectory()) {
            throw new FileNotFoundException(dataFile.toString());
        }
        Preconditions.checkArgument(dataFile.length() > 0L, "dataFile is empty");
        Preconditions.checkArgument(minReloadIntervalMS >= 0L, "minReloadIntervalMs must be non-negative");

        log.info("Creating FileDataModel for file {}", dataFile);

        this.lastModified = dataFile.lastModified();
        this.lastUpdateFileModified = readLastUpdateFileModified();

        FileLineIterator iterator = new FileLineIterator(dataFile, false);
        String firstLine = iterator.peek();
        while (firstLine.isEmpty() || firstLine.charAt(0) == COMMENT_CHAR) {
            iterator.next();
            firstLine = iterator.peek();
        }
        Closeables.close(iterator, true);

        if (delimiterRegex == null) {
            delimiter = determineDelimiter(firstLine);
            delimiterPattern = Splitter.on(delimiter);
        } else {
            delimiter = '\0';
            delimiterPattern = Splitter.onPattern(delimiterRegex);
            if (!delimiterPattern.split(firstLine).iterator().hasNext()) {
                throw new IllegalArgumentException("Did not find a delimiter(pattern) in first line");
            }
        }
        List<String> firstLineSplit = Lists.newArrayList();
        for (String token : delimiterPattern.split(firstLine)) {
            firstLineSplit.add(token);
        }
        // If preference value exists and isn't empty then the file is specifying pref values
        hasPrefValues = firstLineSplit.size() >= 3 && !firstLineSplit.get(2).isEmpty();

        this.reloadLock = new ReentrantLock();
        this.transpose = transpose;
        this.minReloadIntervalMS = minReloadIntervalMS;
        this.skipFirstLine=skipFirstLine;
        reload();
    }

    public File getDataFile() {
        return dataFile;
    }

    protected void reload() {
        if (reloadLock.tryLock()) {
            try {
                delegate = buildModel(this.skipFirstLine);
            } catch (IOException ioe) {
                log.warn("Exception while reloading", ioe);
            } finally {
                reloadLock.unlock();
            }
        }
    }

    protected DataModel buildModel(boolean skipFirstLine) throws IOException {

        long newLastModified = dataFile.lastModified();
        long newLastUpdateFileModified = readLastUpdateFileModified();

        boolean loadFreshData = delegate == null || newLastModified > lastModified + minReloadIntervalMS;

        long oldLastUpdateFileModifieid = lastUpdateFileModified;
        lastModified = newLastModified;
        lastUpdateFileModified = newLastUpdateFileModified;

        FastByIDMap<FastByIDMap<Long>> timestamps = new FastByIDMap<FastByIDMap<Long>>();

        if (hasPrefValues) {

            if (loadFreshData) {

                FastByIDMap<Collection<Preference>> data = new FastByIDMap<Collection<Preference>>();
                FileLineIterator iterator = new FileLineIterator(dataFile, skipFirstLine);
                processFile(iterator, data, timestamps, false);

                for (File updateFile : findUpdateFilesAfter(newLastModified)) {
                    processFile(new FileLineIterator(updateFile, false), data, timestamps, false);
                }

                return new GenericDataModel(GenericDataModel.toDataMap(data, true), timestamps);

            } else {

                FastByIDMap<PreferenceArray> rawData = ((GenericDataModel) delegate).getRawUserData();

                for (File updateFile : findUpdateFilesAfter(Math.max(oldLastUpdateFileModifieid, newLastModified))) {
                    processFile(new FileLineIterator(updateFile, false), rawData, timestamps, true);
                }

                return new GenericDataModel(rawData, timestamps);

            }

        } else {

            if (loadFreshData) {

                FastByIDMap<FastIDSet> data = new FastByIDMap<FastIDSet>();
                FileLineIterator iterator = new FileLineIterator(dataFile, false);
                processFileWithoutID(iterator, data, timestamps);

                for (File updateFile : findUpdateFilesAfter(newLastModified)) {
                    processFileWithoutID(new FileLineIterator(updateFile, false), data, timestamps);
                }

                return new GenericBooleanPrefDataModel(data, timestamps);

            } else {

                FastByIDMap<FastIDSet> rawData = ((GenericBooleanPrefDataModel) delegate).getRawUserData();

                for (File updateFile : findUpdateFilesAfter(Math.max(oldLastUpdateFileModifieid, newLastModified))) {
                    processFileWithoutID(new FileLineIterator(updateFile, false), rawData, timestamps);
                }

                return new GenericBooleanPrefDataModel(rawData, timestamps);

            }

        }
    }

    /**
     * Finds update delta files in the same directory as the data file. This finds any file whose name starts
     * the same way as the data file (up to first period) but isn't the data file itself. For example, if the
     * data file is /foo/data.txt.gz, you might place update files at /foo/data.1.txt.gz, /foo/data.2.txt.gz,
     * etc.
     */
    private Iterable<File> findUpdateFilesAfter(long minimumLastModified) {
        String dataFileName = dataFile.getName();
        int period = dataFileName.indexOf('.');
        String startName = period < 0 ? dataFileName : dataFileName.substring(0, period);
        File parentDir = dataFile.getParentFile();
        Map<Long, File> modTimeToUpdateFile = new TreeMap<Long,File>();
        FileFilter onlyFiles = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };
        for (File updateFile : parentDir.listFiles(onlyFiles)) {
            String updateFileName = updateFile.getName();
            if (updateFileName.startsWith(startName)
                    && !updateFileName.equals(dataFileName)
                    && updateFile.lastModified() >= minimumLastModified) {
                modTimeToUpdateFile.put(updateFile.lastModified(), updateFile);
            }
        }
        return modTimeToUpdateFile.values();
    }

    private long readLastUpdateFileModified() {
        long mostRecentModification = Long.MIN_VALUE;
        for (File updateFile : findUpdateFilesAfter(0L)) {
            mostRecentModification = Math.max(mostRecentModification, updateFile.lastModified());
        }
        return mostRecentModification;
    }

    public static char determineDelimiter(String line) {
        for (char possibleDelimieter : DELIMIETERS) {
            if (line.indexOf(possibleDelimieter) >= 0) {
                return possibleDelimieter;
            }
        }
        throw new IllegalArgumentException("Did not find a delimiter in first line");
    }

    protected void processFile(FileLineIterator dataOrUpdateFileIterator,
                               FastByIDMap<?> data,
                               FastByIDMap<FastByIDMap<Long>> timestamps,
                               boolean fromPriorData) {
        log.info("Reading file info...");
        int count = 0;
        while (dataOrUpdateFileIterator.hasNext()) {
            String line = dataOrUpdateFileIterator.next();
            if (!line.isEmpty()) {
                processLine(line, data, timestamps, fromPriorData);
                if (++count % 1000000 == 0) {
                    log.info("Processed {} lines", count);
                }
            }
        }
        log.info("Read lines: {}", count);
    }

    /**
     * <p>
     * Reads one line from the input file and adds the data to a {@link FastByIDMap} data structure which maps user IDs
     * to preferences. This assumes that each line of the input file corresponds to one preference. After
     * reading a line and determining which user and item the preference pertains to, the method should look to
     * see if the data contains a mapping for the user ID already, and if not, add an empty data structure of preferences
     * as appropriate to the data.
     * </p>
     *
     * <p>
     * Note that if the line is empty or begins with '#' it will be ignored as a comment.
     * </p>
     *
     * @param line
     *          line from input data file
     * @param data
     *          all data read so far, as a mapping from user IDs to preferences
     * @param fromPriorData an implementation detail -- if true, data will map IDs to
     *  {@link PreferenceArray} since the framework is attempting to read and update raw
     *  data that is already in memory. Otherwise it maps to {@link Collection}s of
     *  {@link Preference}s, since it's reading fresh data. Subclasses must be prepared
     *  to handle this wrinkle.
     */
    protected void processLine(String line,
                               FastByIDMap<?> data,
                               FastByIDMap<FastByIDMap<Long>> timestamps,
                               boolean fromPriorData) {

        // Ignore empty lines and comments
        if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
            return;
        }

        Iterator<String> tokens = delimiterPattern.split(line).iterator();
        String userIDString = tokens.next();
        String itemIDString = tokens.next();
        String preferenceValueString = tokens.next();
        boolean hasTimestamp = tokens.hasNext();
        String timestampString = hasTimestamp ? tokens.next() : null;

        long userID = readUserIDFromString(userIDString);
        long itemID = readItemIDFromString(itemIDString);

        if (transpose) {
            long tmp = userID;
            userID = itemID;
            itemID = tmp;
        }

        // This is kind of gross but need to handle two types of storage
        Object maybePrefs = data.get(userID);
        if (fromPriorData) {
            // Data are PreferenceArray

            PreferenceArray prefs = (PreferenceArray) maybePrefs;
            if (!hasTimestamp && preferenceValueString.isEmpty()) {
                // Then line is of form "userID,itemID,", meaning remove
                if (prefs != null) {
                    boolean exists = false;
                    int length = prefs.length();
                    for (int i = 0; i < length; i++) {
                        if (prefs.getItemID(i) == itemID) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        if (length == 1) {
                            data.remove(userID);
                        } else {
                            PreferenceArray newPrefs = new GenericUserPreferenceArray(length - 1);
                            for (int i = 0, j = 0; i < length; i++, j++) {
                                if (prefs.getItemID(i) == itemID) {
                                    j--;
                                } else {
                                    newPrefs.set(j, prefs.get(i));
                                }
                            }
                            ((FastByIDMap<PreferenceArray>) data).put(userID, newPrefs);
                        }
                    }
                }

                removeTimestamp(userID, itemID, timestamps);

            } else {

                float preferenceValue = Float.parseFloat(preferenceValueString);

                boolean exists = false;
                if (prefs != null) {
                    for (int i = 0; i < prefs.length(); i++) {
                        if (prefs.getItemID(i) == itemID) {
                            exists = true;
                            prefs.setValue(i, preferenceValue);
                            break;
                        }
                    }
                }

                if (!exists) {
                    if (prefs == null) {
                        prefs = new GenericUserPreferenceArray(1);
                    } else {
                        PreferenceArray newPrefs = new GenericUserPreferenceArray(prefs.length() + 1);
                        for (int i = 0, j = 1; i < prefs.length(); i++, j++) {
                            newPrefs.set(j, prefs.get(i));
                        }
                        prefs = newPrefs;
                    }
                    prefs.setUserID(0, userID);
                    prefs.setItemID(0, itemID);
                    prefs.setValue(0, preferenceValue);
                    ((FastByIDMap<PreferenceArray>) data).put(userID, prefs);
                }
            }

            addTimestamp(userID, itemID, timestampString, timestamps);

        } else {
            // Data are Collection<Preference>

            Collection<Preference> prefs = (Collection<Preference>) maybePrefs;

            if (!hasTimestamp && preferenceValueString.isEmpty()) {
                // Then line is of form "userID,itemID,", meaning remove
                if (prefs != null) {
                    // remove pref
                    Iterator<Preference> prefsIterator = prefs.iterator();
                    while (prefsIterator.hasNext()) {
                        Preference pref = prefsIterator.next();
                        if (pref.getItemID() == itemID) {
                            prefsIterator.remove();
                            break;
                        }
                    }
                }

                removeTimestamp(userID, itemID, timestamps);

            } else {

                float preferenceValue = Float.parseFloat(preferenceValueString);

                boolean exists = false;
                if (prefs != null) {
                    for (Preference pref : prefs) {
                        if (pref.getItemID() == itemID) {
                            exists = true;
                            pref.setValue(preferenceValue);
                            break;
                        }
                    }
                }

                if (!exists) {
                    if (prefs == null) {
                        prefs = Lists.newArrayListWithCapacity(2);
                        ((FastByIDMap<Collection<Preference>>) data).put(userID, prefs);
                    }
                    prefs.add(new GenericPreference(userID, itemID, preferenceValue));
                }

                addTimestamp(userID, itemID, timestampString, timestamps);

            }

        }
    }

    protected void processFileWithoutID(FileLineIterator dataOrUpdateFileIterator,
                                        FastByIDMap<FastIDSet> data,
                                        FastByIDMap<FastByIDMap<Long>> timestamps) {
        log.info("Reading file info...");
        int count = 0;
        while (dataOrUpdateFileIterator.hasNext()) {
            String line = dataOrUpdateFileIterator.next();
            if (!line.isEmpty()) {
                processLineWithoutID(line, data, timestamps);
                if (++count % 100000 == 0) {
                    log.info("Processed {} lines", count);
                }
            }
        }
        log.info("Read lines: {}", count);
    }

    protected void processLineWithoutID(String line,
                                        FastByIDMap<FastIDSet> data,
                                        FastByIDMap<FastByIDMap<Long>> timestamps) {

        if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
            return;
        }

        Iterator<String> tokens = delimiterPattern.split(line).iterator();
        String userIDString = tokens.next();
        String itemIDString = tokens.next();
        boolean hasPreference = tokens.hasNext();
        String preferenceValueString = hasPreference ? tokens.next() : "";
        boolean hasTimestamp = tokens.hasNext();
        String timestampString = hasTimestamp ? tokens.next() : null;

        long userID = readUserIDFromString(userIDString);
        long itemID = readItemIDFromString(itemIDString);

        if (transpose) {
            long tmp = userID;
            userID = itemID;
            itemID = tmp;
        }

        if (hasPreference && !hasTimestamp && preferenceValueString.isEmpty()) {
            // Then line is of form "userID,itemID,", meaning remove

            FastIDSet itemIDs = data.get(userID);
            if (itemIDs != null) {
                itemIDs.remove(itemID);
            }

            removeTimestamp(userID, itemID, timestamps);

        } else {

            FastIDSet itemIDs = data.get(userID);
            if (itemIDs == null) {
                itemIDs = new FastIDSet(2);
                data.put(userID, itemIDs);
            }
            itemIDs.add(itemID);

            addTimestamp(userID, itemID, timestampString, timestamps);

        }
    }

    private void addTimestamp(long userID,
                              long itemID,
                              String timestampString,
                              FastByIDMap<FastByIDMap<Long>> timestamps) {
        if (timestampString != null) {
            FastByIDMap<Long> itemTimestamps = timestamps.get(userID);
            if (itemTimestamps == null) {
                itemTimestamps = new FastByIDMap<Long>();
                timestamps.put(userID, itemTimestamps);
            }
            long timestamp = readTimestampFromString(timestampString);
            itemTimestamps.put(itemID, timestamp);
        }
    }

    private static void removeTimestamp(long userID,
                                        long itemID,
                                        FastByIDMap<FastByIDMap<Long>> timestamps) {
        FastByIDMap<Long> itemTimestamps = timestamps.get(userID);
        if (itemTimestamps != null) {
            itemTimestamps.remove(itemID);
        }
    }

    /**
     * Subclasses may wish to override this if ID values in the file are not numeric. This provides a hook by
     * which subclasses can inject an {@link org.apache.mahout.cf.taste.model.IDMigrator} to perform
     * translation.
     */
    protected long readUserIDFromString(String value) {
        return Long.parseLong(value);
    }

    /**
     * Subclasses may wish to override this if ID values in the file are not numeric. This provides a hook by
     * which subclasses can inject an {@link org.apache.mahout.cf.taste.model.IDMigrator} to perform
     * translation.
     */
    protected long readItemIDFromString(String value) {
        return Long.parseLong(value);
    }

    /**
     * Subclasses may wish to override this to change how time values in the input file are parsed.
     * By default they are expected to be numeric, expressing a time as milliseconds since the epoch.
     */
    protected long readTimestampFromString(String value) {
        return Long.parseLong(value);
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return delegate.getUserIDs();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
        return delegate.getPreferencesFromUser(userID);
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

    /**
     * Note that this method only updates the in-memory preference data that this {@link FileDataModel}
     * maintains; it does not modify any data on disk. Therefore any updates from this method are only
     * temporary, and lost when data is reloaded from a file. This method should also be considered relatively
     * slow.
     */
    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        delegate.setPreference(userID, itemID, value);
    }

    /** See the warning at {@link #setPreference(long, long, float)}. */
    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        FastByIDMap<PreferenceArray> rawData = ((GenericDataModel) delegate).getRawUserData();
        PreferenceArray prefs = rawData.get(userID);
        if (prefs != null) {
            boolean exists = false;
            int length = prefs.length();
            for (int i = 0; i < length; i++) {
                if (prefs.getItemID(i) == itemID) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                rawData.remove(userID);
                if (length > 1) {
                    PreferenceArray newPrefs = new GenericUserPreferenceArray(length - 1);
                    for (int i = 0, j = 0; i < length; i++, j++) {
                        if (prefs.getItemID(i) == itemID) {
                            j--;
                        } else {
                            newPrefs.set(j, prefs.get(i));
                        }
                    }
                    rawData.put(userID, newPrefs);
                }
                log.info("Removing userID: {} itemID: {}", userID, itemID);
              /*  if (mongoManage) {
                    removeMongoUserItem(Long.toString(userID), Long.toString(itemID));
                }*/
                removeCsvUserItem(Long.toString(userID), Long.toString(itemID),rawData);
            }
        }
    }


    private void removeCsvUserItem1(String userID, String itemID,FastByIDMap<PreferenceArray> rawData) {
            rawData.values().forEach(e->{
                GenericUserPreferenceArray array= (GenericUserPreferenceArray) e;
                array.forEach(p->{
                    System.out.println(p.getUserID()+","+p.getItemID()+","+p.getValue());
                });
                /*StringBuffer buffer=new StringBuffer();
                buffer.append(e.)
                         writer.write(datum);
                writer.write('\n');
                */
            });
        log.info("Removing userID: {} itemID: {}", userID, itemID);

    }


    private void removeCsvUserItem(String userID, String itemID,FastByIDMap<PreferenceArray> rawData) {
        System.out.println(rawData.values().size());
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(dataFile,false), Charsets.UTF_8)) {
            StringBuffer buffer=new StringBuffer();
            rawData.values().forEach(e->{
                GenericUserPreferenceArray array= (GenericUserPreferenceArray) e;
                array.forEach(p->{
                    buffer.append(p.getUserID()).append(",").append(p.getItemID()).append(",").append(p.getValue()).append("\n");
                });
            });
            try {
                writer.write(buffer.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ioe){
            System.out.println(ioe);
        }
        log.info("Removing userID: {} itemID: {}", userID, itemID);
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        if (dataFile.lastModified() > lastModified + minReloadIntervalMS
                || readLastUpdateFileModified() > lastUpdateFileModified + minReloadIntervalMS) {
            log.debug("File has changed; reloading...");
            reload();
        }
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
        return "FileDataModel[dataFile:" + dataFile + ']';
    }
}
