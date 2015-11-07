package se.frand.app.favoriteauthorsbirthplace;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import se.frand.app.favoriteauthorsbirthplace.data.AuthorsContract.AuthorEntry;
import se.frand.app.favoriteauthorsbirthplace.sync.GoodreadsSyncAdapter;

public class MainActivity extends ListActivity {
    private static final String[] projection = new String[] {
            AuthorEntry._ID,
            AuthorEntry.COLUMN_NAME_NAME
    };
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoodreadsSyncAdapter.initializeSyncAdapter(this);

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(AuthorEntry.CONTENT_URI, projection, null, null, null);

        String[] from = new String[] {
                AuthorEntry.COLUMN_NAME_NAME
        };
        int[] to = new int[] {
                R.id.author_name
        };
        adapter = new SimpleCursorAdapter(this, R.layout.author_item, cursor, from, to, 0);
        ListView listView = getListView();
        listView.setAdapter(adapter);
        final Handler handler = new Handler();

        resolver.registerContentObserver(AuthorEntry.CONTENT_URI, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, final Uri uri) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = MainActivity.this.getContentResolver().query(uri, projection, null, null, null);
                        adapter.swapCursor(cursor);
                    }
                });
            }
        });

    }
}
