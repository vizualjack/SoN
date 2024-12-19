package son.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import son.Syncer;

public class SyncerService extends Service {
    public static final String RUNNING_STATUS_CHANGED = "son.android.app.RUNNING_STATUS_CHANGED";
    public static final String RUNNING_STATUS_CHANGED_INTENT_VALUE_KEY = "isRunning";
    public static boolean running = false;
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    Syncer syncer;
    Thread syncerThread;
    private static final String ACTION_STOP = "ACTION_STOP";
    private Preferences preferences = new Preferences(this);
    private Notification notification;
    private BroadcastReceiver fileLogStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isActive = intent.getBooleanExtra(MainActivity.FILE_LOG_STATUS_CHANGED_INTENT_VALUE_KEY, false);
            if(isActive) activateFileLogging();
            else deactivateFileLogging();
        }
    };

    private BroadcastReceiver stopListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stop();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoggerSettings.apply();
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stop();
            return START_STICKY;
        }
        if (running) {
            logger.debug("Already running");
            return START_NOT_STICKY;
        }
        if(preferences.isFileLoggingActivated()) activateFileLogging();
        registerListeners();
        running = true;
        createNotification();
        startForeground(1337, notification);
        start();
        sendRunningStatus();
        return START_STICKY;
    }

    private void registerListeners() {
        IntentFilter statusFilter = new IntentFilter(MainActivity.FILE_LOG_STATUS_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(fileLogStatusListener, statusFilter);
        IntentFilter stopFilter = new IntentFilter(MainActivity.REQUEST_STOP_SERVICE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(stopListener, stopFilter);
    }

    private void activateFileLogging() {
        try {
            LoggerSettings.activateFileLogging(getApplicationContext());
        } catch (Exception ex) {
            logger.error("Exception at activateFileLogging: ", ex);
        }
    }

    private void deactivateFileLogging() {
        LoggerSettings.deactivateFileLogging(getApplicationContext());
    }

    private void sendRunningStatus() {
        Intent intent = new Intent(RUNNING_STATUS_CHANGED);
        intent.putExtra(RUNNING_STATUS_CHANGED_INTENT_VALUE_KEY, running);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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
        else logger.error("Can't create notification channel");
        return channelId;
    }

    private void start() {
        if(syncerThread != null) return;
        logger.debug("Starting syncer thread");
        syncerThread = new Thread(() -> {
            logger.debug("Thread started");
            String path = preferences.getBaseFolderPath();
            if(path == null) {
                logger.debug("Thread ending, cause path is null");
                return;
            };
            syncer = new Syncer(new SyncFolderAndroid(Uri.parse(path), this));
            syncer.syncLoop();
            logger.debug("Thread ending, caused by stopping");
            syncerThread = null;
        });
        syncerThread.start();
    }

    private void stop() {
        syncer.stop();
        syncerThread.interrupt();
        stopSelf();
        running = false;
        deactivateFileLogging();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(fileLogStatusListener);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(stopListener);
        sendRunningStatus();
    }
}


