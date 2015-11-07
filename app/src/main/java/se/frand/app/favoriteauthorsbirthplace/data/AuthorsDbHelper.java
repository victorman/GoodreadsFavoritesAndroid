package se.frand.app.favoriteauthorsbirthplace.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import se.frand.app.favoriteauthorsbirthplace.data.AuthorsContract.AuthorEntry;

/**
 * Created by victorfrandsen on 11/6/15.
 */
public class AuthorsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "authors.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String C = ",";
    private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + AuthorEntry.TABLE_NAME + " (" +
                 AuthorEntry._ID + INT_TYPE + "PRIMARY KEY" + C +
                 AuthorEntry.COLUMN_NAME_NAME + TEXT_TYPE +
                " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AuthorEntry.TABLE_NAME;
    
    public AuthorsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}