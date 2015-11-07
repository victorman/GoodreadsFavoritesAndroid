package se.frand.app.favoriteauthorsbirthplace.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import se.frand.app.favoriteauthorsbirthplace.R;
import se.frand.app.favoriteauthorsbirthplace.Util;
import se.frand.app.favoriteauthorsbirthplace.data.AuthorsContract.AuthorEntry;

/**
 * Created by victorfrandsen on 11/3/15.
 */
public class GoodreadsSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = GoodreadsSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;



    public GoodreadsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public GoodreadsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;

        try {
            final String V = "v";
            final String ID = "id";
            final String SHELF = "shelf";
            final String KEY = "key";

            final String USERNAME_OR_ID = Util.getGoodreadsUsernameOrID(getContext());

            final String AUTHOR_URL = "https://www.goodreads.com/author/show.xml?";
            final String USER_REVIEWS_URL = "https://www.goodreads.com/review/list/" + USERNAME_OR_ID + "?";
            final String USER_URL = "https://www.goodreads.com/user/show/" + USERNAME_OR_ID +".xml?";

            Uri builtUri = Uri.parse(USER_REVIEWS_URL).buildUpon()
                    .appendQueryParameter(V, "2")
                    .appendQueryParameter(SHELF, "favorites")
                    .appendQueryParameter(KEY,getContext().getString(R.string.goodreads_key))
                    .build();
            Log.v(LOG_TAG,builtUri.toString());

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if(inputStream == null) {
                return;
            }

            getAuthorDataFromXml(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void getAuthorDataFromXml(InputStream in) throws XmlPullParserException, IOException {
        ReviewsXmlParser xmlParser = new ReviewsXmlParser();

        ArrayList<ReviewsXmlParser.Entry> list = (ArrayList<ReviewsXmlParser.Entry>) xmlParser.parse(in);
        Log.v(LOG_TAG, "what's going on? " + list.size());


        Vector<ContentValues> cValVector =  new Vector<ContentValues>(list.size());

        for(int i = 0; i<list.size();i++) {
            ReviewsXmlParser.Entry entry = list.get(i);
            ContentValues values = new ContentValues();

            values.put(AuthorEntry._ID, entry.id);
            values.put(AuthorEntry.COLUMN_NAME_NAME,entry.name);

            cValVector.add(values);
        }

        if ( cValVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cValVector.size()];
            cValVector.toArray(cvArray);
            ContentResolver resolver = getContext().getContentResolver();
            resolver.delete(AuthorEntry.CONTENT_URI,null,null);
            resolver.bulkInsert(AuthorEntry.CONTENT_URI, cvArray);
            resolver.notifyChange(AuthorEntry.CONTENT_URI,null);
        }

        Log.d(LOG_TAG, "Sync Complete. " + cValVector.size() + " Inserted");
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        GoodreadsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
