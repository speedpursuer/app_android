package com.lee.cliplay.configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lee.cliplay.util.ObjectSerializer;
import com.lee.cliplay.util.SecurePreferences;

import java.util.ArrayList;

/**
 * Created by xl on 16/6/22.
 */
public class LocalDataMgr {
    private static SecurePreferences settingsFavorite;
    private static SharedPreferences settings;
    private static ArrayList favorites;
    private final static String FAVORITE = "favorite";

    public static void init(Context context, String key) {
        if(settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(context);
            settingsFavorite = new SecurePreferences(context, "myFavorite", key, true);

            String fav = settingsFavorite.getString(FAVORITE);

            if(fav == null) {
                favorites = new ArrayList();
            }else{
                favorites = (ArrayList<String>) ObjectSerializer.deserialize(fav);
            }
        }
    }

    public static boolean getShowTipFlag(String id) {
        return settings.getBoolean(id, true);
    }

    public static void setShowTipFlag(String id) {
        settings.edit().putBoolean(id, false).commit();
    }

    public static void setFavoriate(String url) {
        if(!isFavoriate(url)) {
            favorites.add(0, url);
        }
    }

    public static boolean isFavoriate(String url) {
        return favorites.indexOf(url) != -1;
    }

    public static void unsetFavoriate(String url) {
        favorites.remove(url);
    }

    public static void persistData() {
        settingsFavorite.put(FAVORITE, ObjectSerializer.serialize(favorites));
    }

    public static ArrayList<String> getFavoriateImages() {
        return favorites;
    }
}
