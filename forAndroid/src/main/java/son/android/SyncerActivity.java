package son.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import son.Syncer;

public class SyncerActivity extends Service {
    Syncer syncer;
    Thread syncerThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = createNotificationChannel("SYNCER_MESSAGE", "Syncer Channel");
        Notification notification = new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle("Syncer")
                .setContentText("I just keep all stuff synced...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(startId, notification);
        start();
        return START_STICKY;
    }

    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    private void start() {
        syncerThread = new Thread(() -> {
            String path = PreferenceManager.getDefaultSharedPreferences(this).getString(MainActivity.PATH_KEY, null);
            if(path == null) return;
            syncer = new Syncer(new SyncFolderAndroid(Uri.parse(path), this));
            while(true) {
                try {
                    System.out.println("Syncing...");
                    syncer.sync();
                    System.out.println("Synced. Next sync in 60 seconds");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        syncerThread.start();
    }
}


