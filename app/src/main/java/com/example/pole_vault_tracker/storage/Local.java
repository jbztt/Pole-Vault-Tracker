package com.example.pole_vault_tracker.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pole_vault_tracker.storage.model.Game;

import java.util.ArrayList;
import java.util.List;

public class Local extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "PoleVaultTracker.db";
    private static Local instance;

    public Local(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized Local getInstance(Context context) {
        if (instance == null) instance = new Local(context.getApplicationContext());
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Common.SQL_CREATE_GAMES);
        db.execSQL(
                "CREATE TRIGGER prune_games AFTER INSERT ON " + Common.Game.TABLE_NAME + "\n" +
                        "BEGIN\n" +
                        "  DELETE FROM " + Common.Game.TABLE_NAME
                        + " WHERE " + Common.Game._ID
                        + " IN (SELECT " + Common.Game._ID + " FROM " + Common.Game.TABLE_NAME + " LIMIT 10 OFFSET 5);\n" +
                        "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Common.SQL_DELETE_GAMES);
        onCreate(db);
    }

    public static List<Game> getGames(Context context) {
        List<Game> games = new ArrayList<>();
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Game.TABLE_NAME, new String[]{Common.Game._ID, Common.Game.COLUMN_NAME_PLAYER, Common.Game.COLUMN_NAME_LOCATION},
                        null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                games.add(new Game(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            }
        }
        return games;
    }

    public static Game getLastGame(Context context){
        Game game = null;
        try (Cursor cursor = getInstance(context).getReadableDatabase()
                .query(Common.Game.TABLE_NAME, new String[]{Common.Game._ID, Common.Game.COLUMN_NAME_PLAYER, Common.Game.COLUMN_NAME_LOCATION},
                        null, null,null,null, null, "1")){
            if (cursor.moveToFirst()) game = new Game(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
        }
        return game;
    }

    public static long insertGame(Context context){
        return getInstance(context).getWritableDatabase().insert(Common.Game.TABLE_NAME, null, gameContentValues(null));
    }
    public static void updateGame(Context context, Game game){
        getInstance(context).getWritableDatabase()
                .update(Common.Game.TABLE_NAME, gameContentValues(game), "WHERE ?=?",new String[]{Common.Game._ID, String.valueOf(game.getId())});
    }

    public static ContentValues gameContentValues(Game game){
        if (game == null) game = new Game();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Common.Game.COLUMN_NAME_PLAYER,game.getPlayerName());
        contentValues.put(Common.Game.COLUMN_NAME_LOCATION,game.getLocation());
        return contentValues;
    }
}
