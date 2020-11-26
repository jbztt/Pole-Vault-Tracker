package com.example.pole_vault_tracker.storage;

import android.content.Context;
import android.provider.BaseColumns;

public final class Common {
    public static final String SHARED_PREFERENCE_KEY_GAME_ID = "GAME_ID";
    public static final String SHARED_PREFERENCE_KEY_JUMP_ID = "JUMP_ID";
    public static final String SHARED_PREFERENCE_TENANT_NAME = "TENANT_ID";
    public static final String SHARED_PREFERENCE_LOCALE_ID = "LOCALE_ID";
    public static final String SHARED_PREFERENCES_NAME = "LocalStorageSharedPreferences";

    private Common() {
    }

    public static class Game implements BaseColumns {
        public static final String TABLE_NAME = "game";
        public static final String COLUMN_NAME_PLAYER_NAME = "player_name";
        public static final String COLUMN_NAME_LOCATION = "location";
    }

    public static class Jump implements BaseColumns {
        public static final String TABLE_NAME = "jump";
        public static final String COLUMN_NAME_GAME_ID = "game_id";
        public static final String COLUMN_NAME_SUCCESS = "success";
        public static final String COLUMN_NAME_JUMP_HEIGHT = "jump_height";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public static void setLocaleID(Context context,int id){
       context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(SHARED_PREFERENCE_LOCALE_ID,id).apply();
    }
    public static int getLocaleID(Context context){
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(SHARED_PREFERENCE_LOCALE_ID, 1);
    }
}
