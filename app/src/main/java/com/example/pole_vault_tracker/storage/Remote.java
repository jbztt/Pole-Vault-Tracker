package com.example.pole_vault_tracker.storage;

import android.content.Context;

import com.example.pole_vault_tracker.storage.model.Game;
import com.example.pole_vault_tracker.storage.model.Jump;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Remote {
    private static final String[] GAME_TABLE_SELECTION = {Common.Game._ID, Common.Game.COLUMN_NAME_PLAYER_NAME, Common.Game.COLUMN_NAME_LOCATION};
    private static final String[] JUMP_TABLE_SELECTION = {Common.Jump._ID, Common.Jump.COLUMN_NAME_GAME_ID, Common.Jump.COLUMN_NAME_JUMP_HEIGHT, Common.Jump.COLUMN_NAME_SUCCESS, Common.Jump.COLUMN_NAME_DESCRIPTION};
    private static final String DATABASE_NAME = "pole_vault_tracker";
    private static final String DATABASE_URL = "jdbc:mysql://10.0.2.2:3306/"+DATABASE_NAME;
    private static final String DATABASE_USER = "pole_vault_tracker";
    private static final String DATABASE_PASSWORD = "123456789";
    private static final String COLUMN_NAME_TENANT_NAME = "tenant_name";

    private static Connection instance;

    public static synchronized Connection getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            Class.forName("com.mysql.jdbc.Driver");
            instance = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }

        if (!instance.isValid(10)) {
            try {
                instance.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                instance = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            }
        }
        return instance;
    }

    public static List<Jump> getJumps(long gameID) throws SQLException, ClassNotFoundException {
        List<Jump> jumps = new ArrayList<>();
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format(Locale.getDefault(),"SELECT %s FROM %s WHERE %s=?", String.join(", ", JUMP_TABLE_SELECTION), Common.Jump.TABLE_NAME, Common.Jump.COLUMN_NAME_GAME_ID))){
            stmt.setLong(1,gameID);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) jumps.add(jumpFromResultSet(rs));
            }
        }
        return jumps;
    }

    public static void insertJump(long jumpID, long gameID) throws SQLException, ClassNotFoundException {
        try (PreparedStatement stmt = getInstance().prepareStatement(String.format(Locale.getDefault(),
                "INSERT INTO %s (%s, %s) VALUES(?,?)", Common.Jump.TABLE_NAME, Common.Jump._ID, Common.Jump.COLUMN_NAME_GAME_ID))){
            stmt.setLong(1,jumpID);
            stmt.setLong(2,gameID);
            stmt.executeUpdate();
        }
    }
    public static void updateJump(Jump jump) throws SQLException, ClassNotFoundException {
        try(PreparedStatement stmt = getInstance().prepareStatement(String
                .format(Locale.getDefault(),"UPDATE %s SET %s=?, %s=?, %s=? WHERE %s=?",
                        Common.Jump.TABLE_NAME, Common.Jump.COLUMN_NAME_JUMP_HEIGHT, Common.Jump.COLUMN_NAME_SUCCESS, Common.Jump.COLUMN_NAME_DESCRIPTION, Common.Jump._ID))){
            stmt.setDouble(1,jump.getJumpHeight());
            stmt.setBoolean(2,jump.isSuccess());
            stmt.setString(3,jump.getDescription());
            stmt.setLong(4, jump.getId());
        }
    }
    public static void deleteJump(long jumpID) throws SQLException, ClassNotFoundException {
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format("DELETE FROM %s WHERE %s=?", Common.Jump.TABLE_NAME, Common.Jump._ID))){
            stmt.setLong(1,jumpID);
            stmt.executeUpdate();
        }
    }

    public static List<Game> getGames(String tenantName) throws SQLException, ClassNotFoundException {
        List<Game> games = new ArrayList<>();
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format("SELECT %s FROM %s WHERE %s=?",
                        String.join(", ", GAME_TABLE_SELECTION), Common.Game.TABLE_NAME, COLUMN_NAME_TENANT_NAME))) {
            stmt.setString(1, tenantName);
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) games.add(gameFromResultSet(rs));
            }
        }
        return games;
    }

    private static Game gameFromResultSet(ResultSet resultSet) throws SQLException {
        return new Game(resultSet.getLong(Common.Game._ID), resultSet.getString(Common.Game.COLUMN_NAME_PLAYER_NAME), resultSet.getString(Common.Game.COLUMN_NAME_LOCATION));
    }
    private static Jump jumpFromResultSet(ResultSet resultSet) throws SQLException {
        return new Jump(resultSet
                .getLong(Common.Jump._ID), resultSet.getLong(Common.Jump.COLUMN_NAME_GAME_ID),
                resultSet.getDouble(Common.Jump.COLUMN_NAME_JUMP_HEIGHT),
                resultSet.getBoolean(Common.Jump.COLUMN_NAME_SUCCESS), resultSet.getString(Common.Jump.COLUMN_NAME_DESCRIPTION));
    }

    public static void deleteGame(long id) throws SQLException, ClassNotFoundException {
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format(Locale.getDefault(), "DELETE FROM %s WHERE %s=?", Common.Game.TABLE_NAME, Common.Game._ID))) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public static void updateGame(Game game) throws SQLException, ClassNotFoundException {
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format(Locale.getDefault(),"UPDATE %s SET %s=?, %s=? WHERE %s=?",
                        Common.Game.TABLE_NAME, Common.Game.COLUMN_NAME_LOCATION, Common.Game.COLUMN_NAME_PLAYER_NAME, Common.Game._ID))) {
            stmt.setString(1,game.getLocation());
            stmt.setString(2, game.getPlayerName());
            stmt.setLong(3, game.getId());
            stmt.executeUpdate();
        }
    }

    public static void insertGame(long gameID, String tenantName) throws SQLException, ClassNotFoundException {
        try (PreparedStatement stmt = getInstance().prepareStatement(String
                .format(Locale.getDefault(), "INSERT INTO %s (%s, %s) VALUES(?,?)",Common.Game.TABLE_NAME, Common.Game._ID, COLUMN_NAME_TENANT_NAME))) {
            stmt.setLong(1, gameID);
            stmt.setString(2, tenantName);
            stmt.executeUpdate();
        }
    }
    public static void setTenantName(Context context, String tenantName){
        context.getSharedPreferences(Common.SHARED_PREFERENCE_TENANT_NAME, Context.MODE_PRIVATE).edit().putString(Common.SHARED_PREFERENCE_TENANT_NAME, tenantName).apply();
    }
    public static String getTenantName(Context context){
        return context.getSharedPreferences(Common.SHARED_PREFERENCE_TENANT_NAME, Context.MODE_PRIVATE).getString(Common.SHARED_PREFERENCE_TENANT_NAME,"toto");
    }

}
