package com.example.pole_vault_tracker.storage;

import android.provider.BaseColumns;

public final class Common {
    public static final String SQL_CREATE_GAMES = "CREATE TABLE " + Game.TABLE_NAME
            + " (" + Game._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Game.COLUMN_NAME_PLAYER + " TEXT, " + Game.COLUMN_NAME_LOCATION + " TEXT)";

    public static final String SQL_DELETE_GAMES = String.format("DROP TABLE IF EXISTS %s", Game.TABLE_NAME);

    private Common() {
    }

    public static class Game implements BaseColumns {
        public static final String TABLE_NAME = "game";
        public static final String COLUMN_NAME_PLAYER = "player";
        public static final String COLUMN_NAME_LOCATION = "location";
    }
}
