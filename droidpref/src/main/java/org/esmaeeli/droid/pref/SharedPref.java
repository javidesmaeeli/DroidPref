package org.esmaeeli.droid.pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An abstract wrapper on SharedPreferences which provides
 * - Versioning and Migration
 * - Thread safety
 * - Caching
 * <p>
 * This wrapper hides access to the actual {@link SharedPreferences} class, thus the implementation
 * has only to provide its own interface for the preferences it provides and doesn't have to worry
 * about it's consumer having access to keys or mistakenly reading a wrong type from a key and such
 * problems.
 * <p>
 * The implementation should not use the key "file_version" as it's reserved for maintaining pref
 * version and migration. Note that the reserved key is not checked by the provided wrapper methods
 * to avoid performance overhead.
 *
 * @author Javid Esmaeeli
 */
@SuppressWarnings("SameParameterValue")
public abstract class SharedPref {

    private static final String KEY_VERSION = "file_version";

    private SharedPreferences preferences;
    private ReentrantReadWriteLock lock;
    private Map<String, Object> cache;

    @SuppressLint("ApplySharedPref")
    public SharedPref(@NonNull Context context) {

        lock = new ReentrantReadWriteLock(true);
        cache = new HashMap<>();
        preferences = context.getSharedPreferences(getName(), Context.MODE_PRIVATE);
        int savedVersion = preferences.getInt(KEY_VERSION, 1);
        if (savedVersion != getVersion()) {
            migrate(savedVersion, getVersion());
        }
        preferences.edit().putInt(KEY_VERSION, getVersion()).commit();
        cacheAll();
    }

    protected abstract int getVersion();

    protected abstract String getName();

    protected abstract void migrate(int oldVersion, int newVersion);

    // region Get
    protected final boolean getBoolean(@NonNull String key, boolean defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                return (boolean) cache.get(key);
            }
            boolean value = preferences.getBoolean(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }
    }

    protected final int getInt(@NonNull String key, int defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                return (int) cache.get(key);
            }
            int value = preferences.getInt(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }
    }

    protected final long getLong(@NonNull String key, long defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                return (long) cache.get(key);
            }
            long value = preferences.getLong(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }

    }

    protected final float getFloat(@NonNull String key, float defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                return (float) cache.get(key);
            }
            float value = preferences.getFloat(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    protected final String getString(@NonNull String key, @Nullable String defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                return (String) cache.get(key);
            }
            String value = preferences.getString(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    protected final Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValue) {

        lock.readLock().lock();
        try {
            if (cache.containsKey(key)) {
                //noinspection unchecked
                return (Set<String>) cache.get(key);
            }
            Set<String> value = preferences.getStringSet(key, defValue);
            cache.put(key, value);
            return value;

        } finally {
            lock.readLock().unlock();
        }
    }
    // endregion

    protected final boolean containsKey(@NonNull String key) {
        lock.readLock().lock();
        try {
            return cache.containsKey(key) || preferences.contains(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    // region Put
    protected final boolean putBoolean(@NonNull String key, boolean value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putBoolean(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean putInt(@NonNull String key, int value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putInt(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean putLong(@NonNull String key, long value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putLong(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean putFloat(@NonNull String key, float value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putFloat(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean putString(@NonNull String key, @Nullable String value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putString(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean putStringSet(@NonNull String key, @Nullable Set<String> value) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().putStringSet(key, value).commit();
            if (result) {
                cache.put(key, value);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }
    // endregion

    protected final boolean deleteKey(@NonNull String key) {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().remove(key).commit();
            if (result) {
                cache.remove(key);
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }

    }

    protected final boolean clearAll() {

        lock.writeLock().lock();
        try {
            boolean result = preferences.edit().clear().commit();
            if (result) {
                cache.clear();
            }
            return result;

        } finally {
            lock.writeLock().unlock();
        }
    }

    protected final void cacheAll() {

        lock.writeLock().lock();
        try {
            cache.clear();
            cache.putAll(preferences.getAll());

        } finally {
            lock.writeLock().unlock();
        }

    }

}
