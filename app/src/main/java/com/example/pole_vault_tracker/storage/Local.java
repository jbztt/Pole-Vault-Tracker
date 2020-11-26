package com.example.pole_vault_tracker.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pole_vault_tracker.storage.model.Game;
import com.example.pole_vault_tracker.storage.model.Jump;

import java.util.ArrayList;
import java.util.List;

public class Local extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "PoleVaultTracker.db";
    private static final String SHARED_PREFERENCES_NAME = "LocalStorageSharedPreferences";
    private static final String[] GAME_TABLE_SELECTION = {Common.Game._ID, Common.Game.COLUMN_NAME_PLAYER_NAME, Common.Game.COLUMN_NAME_LOCATION};
    private static final String[] JUMP_TABLE_SELECTION = {Common.Jump._ID, Common.Jump.COLUMN_NAME_GAME_ID, Common.Jump.COLUMN_NAME_JUMP_HEIGHT, Common.Jump.COLUMN_NAME_SUCCESS, Common.Jump.COLUMN_NAME_DESCRIPTION};
    private static Local instance;

    public Local(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized Local getInstance(Context context) {
        if (instance == null) instance = new Local(context.getApplicationContext());
        return instance;
    }

    public static List<Jump> getJumps(Context context, long gameID) {
        List<Jump> jumps = new ArrayList<>();
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Jump.TABLE_NAME, JUMP_TABLE_SELECTION,
                        Common.Jump.COLUMN_NAME_GAME_ID + "=" + gameID, null, null, null, null)) {
            while (cursor.moveToNext()) jumps.add(jumpFromCursor(cursor));
        }
        return jumps;
    }

    public static Jump getJump(Context context, long id) {
        Jump jump = null;
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Jump.TABLE_NAME, JUMP_TABLE_SELECTION, Common.Jump._ID + "=" + id, null, null, null, null)) {
            if (cursor.moveToFirst()) jump = jumpFromCursor(cursor);
        }
        return jump;
    }

    public static long insertJump(Context context, long gameID) {
        return getInstance(context).getWritableDatabase().insert(Common.Jump.TABLE_NAME, null, jumpContentValues(new Jump(gameID)));
    }

    public static void updateJump(Context context, Jump jump) {
        getInstance(context).getWritableDatabase()
                .update(Common.Jump.TABLE_NAME, jumpContentValues(jump), Common.Jump._ID + "=" + jump.getId(), null);
    }

    public static void deleteJump(Context context, long jumpID) {
        getInstance(context).getWritableDatabase().delete(Common.Jump.TABLE_NAME, Common.Jump._ID + "=" + jumpID, null);
    }

    public static List<Game> getGames(Context context) {
        List<Game> games = new ArrayList<>();
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Game.TABLE_NAME, new String[]{Common.Game._ID, Common.Game.COLUMN_NAME_PLAYER_NAME, Common.Game.COLUMN_NAME_LOCATION},
                        null, null, null, null, null)) {
            while (cursor.moveToNext()) games.add(gameFromCursor(cursor));
        }
        return games;
    }

    public static Game getGame(Context context, long id) {
        Game game = null;
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Game.TABLE_NAME, GAME_TABLE_SELECTION,
                        Common.Game._ID + "=" + id, null, null, null, null, "1")) {
            if (cursor.moveToFirst()) game = gameFromCursor(cursor);
        }
        return game;
    }

    public static long insertGame(Context context) {
        return getInstance(context).getWritableDatabase().insert(Common.Game.TABLE_NAME, null, gameContentValues(new Game()));
    }

    public static void updateGame(Context context, Game game) {
        getInstance(context).getWritableDatabase()
                .update(Common.Game.TABLE_NAME, gameContentValues(game), Common.Game._ID + "=" + game.getId(), null);
    }

    public static void deleteGame(Context context, long id) {
        getInstance(context).getWritableDatabase().delete(Common.Game.TABLE_NAME, Common.Game._ID + "=" + id, null);
    }

    private static ContentValues gameContentValues(Game game) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Common.Game.COLUMN_NAME_PLAYER_NAME, game.getPlayerName());
        contentValues.put(Common.Game.COLUMN_NAME_LOCATION, game.getLocation());
        return contentValues;
    }

    private static ContentValues jumpContentValues(Jump jump) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Common.Jump.COLUMN_NAME_DESCRIPTION, jump.getDescription());
        contentValues.put(Common.Jump.COLUMN_NAME_GAME_ID, jump.getGameID());
        contentValues.put(Common.Jump.COLUMN_NAME_JUMP_HEIGHT, jump.getJumpHeight());
        contentValues.put(Common.Jump.COLUMN_NAME_SUCCESS, jump.isSuccess());
        return contentValues;
    }

    public static void setActiveJumpID(Context context, long id) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putLong(Common.SHARED_PREFERENCE_KEY_JUMP_ID, id).apply();
    }

    public static long getActiveJumpID(Context context) {
        long jumpID = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getLong(Common.SHARED_PREFERENCE_KEY_JUMP_ID, -1);
        if (-1 == jumpID) throw new IllegalStateException("Cannot retrieve active jumpID");
        return jumpID;
    }

    public static long getActiveGameID(Context context) {
        long gameID = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getLong(Common.SHARED_PREFERENCE_KEY_GAME_ID, -1);
        if (-1 == gameID) throw new IllegalStateException("Cannot retrieve active gameID");
        return gameID;
    }

    public static void setActiveGameID(Context context, long id) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putLong(Common.SHARED_PREFERENCE_KEY_GAME_ID, id).apply();
    }

    private static Jump jumpFromCursor(Cursor cursor) {
        return new Jump(cursor.getLong(0), cursor.getLong(1),
                cursor.getDouble(2), cursor.getInt(3) == 1, cursor.getString(4));
    }

    private static Game gameFromCursor(Cursor cursor) {
        return new Game(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Common.SQL_CREATE_GAMES);
        db.execSQL(
                "CREATE TRIGGER prune_games AFTER INSERT ON " + Common.Game.TABLE_NAME + "\n" +
                        "BEGIN\n" +
                        "  DELETE FROM " + Common.Game.TABLE_NAME
                        + " WHERE " + Common.Game._ID
                        + " IN (SELECT " + Common.Game._ID + " FROM " + Common.Game.TABLE_NAME
                        + " ORDER BY " + Common.Game._ID + " DESC LIMIT 10 OFFSET 5);\n" +
                        "END;");
        db.execSQL(Common.SQL_CREATE_JUMPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Common.SQL_DELETE_GAMES);
        onCreate(db);
    }
}
