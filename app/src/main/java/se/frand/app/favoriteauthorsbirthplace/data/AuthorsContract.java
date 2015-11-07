package se.frand.app.favoriteauthorsbirthplace.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by victorfrandsen on 11/6/15.
 */
public class AuthorsContract {
    public AuthorsContract() {
    }

    public static class AuthorEntry implements BaseColumns {
        /* Provider specific */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUTHOR).build();
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AUTHOR;
        public static Uri buildAuthorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        /* End */


        public static final String TABLE_NAME = "authors";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BIRTHPLACE = "birthplace";
    }


    public static final String CONTENT_AUTHORITY = "se.frand.app.favoriteauthorsbirthplace";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_AUTHOR = "author";
}