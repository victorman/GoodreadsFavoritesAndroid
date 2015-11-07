package se.frand.app.favoriteauthorsbirthplace.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by victorfrandsen on 11/3/15.
 */
public class GoodreadsAuthenticatorService extends Service {
    private GoodreadsAuthenticator goodreadsAuthenticator;

    @Override
    public void onCreate() {
        goodreadsAuthenticator = new GoodreadsAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return goodreadsAuthenticator.getIBinder();
    }
}
