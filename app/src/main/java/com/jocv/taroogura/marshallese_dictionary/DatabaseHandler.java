package com.jocv.taroogura.marshallese_dictionary;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by taro on 2016/07/24.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static String DICT_DB_NAME = "MarDict";
    private static String HIST_DB_NAME = "History";
    private static String DICT_DB_NAME_ASSET = "MarDict.db";
    private static String HIST_DB_NAME_ASSET = "History.db";
    private static final int DICT_DB_ID = 1;
    private static final int HIST_DB_ID = 2;
    private static final int DICT_DB_VERSION = 1;
    private static final int HIST_DB_VERSION = 1;
    public static SQLiteDatabase dict_db;
    public static SQLiteDatabase hist_db;
    public static DatabaseHandler mDictDbHandler;
    public static DatabaseHandler mHistDbHandler;

    private Context mDictContext;
    private File mDictDatabasePath;
    private Context mHistContext;

    public DatabaseHandler(Context context, String db_name, int db_version, int db_id) {
        super(context, db_name, null, db_version);
        if(db_id == DICT_DB_ID) {
            this.mDictContext = context;
            this.mDictDatabasePath = mDictContext.getDatabasePath(db_name);
        }
        if(db_id == HIST_DB_ID) {
            this.mHistContext = context;
        }
    }

   //make a new DB and copy data from raw dictionary DB in assets
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBaseExists();
        if (dbExist) {
            // do nothing - database already exist
        } else {
            this.getReadableDatabase();
            //http://banani.de/android/android-sqlite-and-a-few-problems/
            this.close();
            try {
                this.copyDataBaseFromAsset();
                String dbPath = mDictDatabasePath.getAbsolutePath();
                SQLiteDatabase checkDb = null;
                try {
                    checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
                } catch (SQLiteException e) {
                }
                if (checkDb != null) {
                    checkDb.setVersion(DICT_DB_VERSION);
                    checkDb.close();
                }
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBaseExists() {
        String dbPath = mDictDatabasePath.getAbsolutePath();
        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //no DB exists yet
        }
        if (checkDb == null) {
            //no DB exists yet
            return false;
        }
        int oldVersion = checkDb.getVersion();
        int newVersion = DICT_DB_VERSION;
        if (oldVersion == newVersion) {
            // DB exists and new
            checkDb.close();
            return true;
        }
        // DB exists but not new -> delete
        File f = new File(dbPath);
        f.delete();
        return false;
    }

    //copy raw DB from assets
    private void copyDataBaseFromAsset() throws IOException{
        // raw DB file in assets
        InputStream mInput = mDictContext.getAssets().open(DICT_DB_NAME_ASSET);
        // empty DB
        OutputStream mOutput = new FileOutputStream(mDictDatabasePath);

        // copy
        byte[] buffer = new byte[1024];
        int size;
        while ((size = mInput.read(buffer)) > 0) {
            mOutput.write(buffer, 0, size);
        }
        // close the streams
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        return getReadableDatabase();
    }

    //make 2 tables(Marshallese to English, English to Marshallese) in History DB when you first activate the App
    public static void createHistTables() {
        String CREATE_M2E_HIST_TABLE = "CREATE TABLE IF NOT EXISTS " + StaticInfo.M2E_HIST_TBL_NAME
                + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "word_id INTEGER" + ")";
        hist_db.execSQL(CREATE_M2E_HIST_TABLE);

        String CREATE_E2M_HIST_TABLE = "CREATE TABLE IF NOT EXISTS " + StaticInfo.E2M_HIST_TBL_NAME
                + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "word_id INTEGER" + ")";
        hist_db.execSQL(CREATE_E2M_HIST_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        if(dict_db != null)
            dict_db.close();
        if(hist_db != null)
            hist_db.close();
        super.close();
    }

    //set 2 DBs(Dictionary, History)
    public static synchronized void setDatabase(Context context) {
        mDictDbHandler = new DatabaseHandler(context.getApplicationContext(), DICT_DB_NAME, DICT_DB_VERSION, DICT_DB_ID);
        mHistDbHandler = new DatabaseHandler(context.getApplicationContext(), HIST_DB_NAME, HIST_DB_VERSION, HIST_DB_ID);
        try {
            mDictDbHandler.createDataBase();
            dict_db = mDictDbHandler.openDataBase();
            hist_db = mHistDbHandler.getWritableDatabase();
            createHistTables();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch(SQLException sqle){
            throw sqle;
        }
    }

}