package com.example.pole_vault_tracker.storage;

import android.provider.BaseColumns;

public final class Common {
    public static final String SQL_CREATE_GAMES = "CREATE TABLE " + Game.TABLE_NAME
            + " (" + Game._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Game.COLUMN_NAME_PLAYER_NAME + " TEXT, " + Game.COLUMN_NAME_LOCATION + " TEXT)";

    public static final String SQL_DELETE_GAMES = String.format("DROP TABLE IF EXISTS %s", Game.TABLE_NAME);
    public static final String SHARED_PREFERENCE_KEY_GAME_ID = "GAME_ID";
    public static final String SHARED_PREFERENCE_KEY_JUMP_ID = "JUMP_ID";
    public static final String SQL_CREATE_JUMPS = "CREATE TABLE "
            + Jump.TABLE_NAME + " (" + Jump._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Jump.COLUMN_NAME_GAME_ID + " INTEGER, " + Jump.COLUMN_NAME_SUCCESS + " BOOLEAN, "
            + Jump.COLUMN_NAME_JUMP_HEIGHT + " FLOAT, " + Jump.COLUMN_NAME_DESCRIPTION
            + " TEXT, FOREIGN KEY(" + Jump.COLUMN_NAME_GAME_ID + ") REFERENCES "
            + Game.TABLE_NAME + "(" + Game._ID + ")" + "ON DELETE CASCADE ON UPDATE CASCADE)";
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
}
