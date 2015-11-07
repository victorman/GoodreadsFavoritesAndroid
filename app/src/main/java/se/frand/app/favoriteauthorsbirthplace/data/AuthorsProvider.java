package se.frand.app.favoriteauthorsbirthplace.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import se.frand.app.favoriteauthorsbirthplace.data.AuthorsContract.AuthorEntry;
/**
 * Created by victorfrandsen on 11/6/15.
 */
public class AuthorsProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private AuthorsDbHelper mOpenHelper;


    static final int AUTHOR = 100;


    @Override
    public boolean onCreate() {
        mOpenHelper = new AuthorsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case AUTHOR:
                return mOpenHelper.getReadableDatabase().query(
                        AuthorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case AUTHOR:
                return AuthorEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case AUTHOR:
                long _id = db.insert(AuthorEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    return AuthorEntry.buildAuthorUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case AUTHOR:
                rowsDeleted = db.delete(AuthorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case AUTHOR:
                rowsUpdated = db.update(AuthorEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AuthorsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AuthorsContract.PATH_AUTHOR, AUTHOR);

        return matcher;
    }
}
