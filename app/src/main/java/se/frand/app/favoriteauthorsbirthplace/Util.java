package se.frand.app.favoriteauthorsbirthplace;

import android.content.Context;

/**
 * Created by victorfrandsen on 11/6/15.
 */
public class Util {
    private static String goodreadsUsernameOrID;

    public static String getGoodreadsUsernameOrID(Context context) {
        goodreadsUsernameOrID = context.getSharedPreferences(
                context.getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
        ).getString(context.getString(R.string.goodreads_username_or_id_key), "30045160");
        return goodreadsUsernameOrID;
    }
}
