package se.frand.app.favoriteauthorsbirthplace.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by victorfrandsen on 11/3/15.
 */
public class GoodreadsSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static GoodreadsSyncAdapter goodreadsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(GoodreadsSyncService.class.getSimpleName(),"onCreate()");
        //android.os.Debug.waitForDebugger();
        synchronized (syncAdapterLock) {
            if(goodreadsSyncAdapter == null) {
                goodreadsSyncAdapter = new GoodreadsSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return goodreadsSyncAdapter.getSyncAdapterBinder();
    }
}
