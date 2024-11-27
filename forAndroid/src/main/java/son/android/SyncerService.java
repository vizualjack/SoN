package son.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import son.Syncer;

public class SyncerService extends Service {
    Syncer syncer;
    Thread syncerThread;
    boolean started = false;
    private static final String ACTION_STOP = "ACTION_STOP";
    private Preferences preferences = new Preferences(this);
    private Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SyncerService - onStartCommand");
        if(intent != null && ACTION_STOP.equals(intent.getAction())) {
            syncerThread.interrupt();
            stopSelf();
            return START_STICKY;
        }
        if(started) {
            System.out.println("SyncerService - already started");
            return START_NOT_STICKY;
        }
        started = true;
        createNotification();
        startForeground(1337, notification);
        start();
        return START_STICKY;
    }

    private void createNotification() {
        String channelId = createNotificationChannel("SYNCER_MESSAGE", "Syncer Channel");
        notification = new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle("Syncer")
                .setContentText("Keep everything synced...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .addAction(new Notification.Action.Builder(Icon.createWithResource(getApplicationContext(), R.drawable.ic_launcher_foreground), "Stop", createStopIntent()).build())
                .build();
    }

    private PendingIntent createStopIntent() {
        Intent stopIntent = new Intent(this, SyncerService.class);
        stopIntent.setAction(ACTION_STOP);
        return PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Syncer Channel for Foreground Service");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
        else System.out.println("Can't create notification channel");
        return channelId;
    }

    private void start() {
        System.out.println("SyncerService - starting syncer thread");
        syncerThread = new Thread(() -> {
            System.out.println("SyncerService - thread started");
            String path = preferences.getBaseFolderPath();
            if(path == null) {
                System.out.println("SyncerService - thread ending, cause path is null");
                return;
            };
            syncer = new Syncer(new SyncFolderAndroid(Uri.parse(path), this));
            syncer.syncLoop();
        });
        syncerThread.start();
    }
}


