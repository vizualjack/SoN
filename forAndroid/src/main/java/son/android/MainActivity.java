package son.android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {
    public static final String REQUEST_STOP_SERVICE = "son.android.app.REQUEST_STOP_SERVICE";
    public static final String FILE_LOG_STATUS_CHANGED = "son.android.app.FILE_LOG_ACTIVE";
    public static final String FILE_LOG_STATUS_CHANGED_INTENT_VALUE_KEY = "isActive";
    private boolean needPostCreateInitialization = true;
    private boolean restartService = false;
    private static Logger logger;
    private ActivityResultLauncher<Intent> openDocumentTreeLauncher;
    private Preferences preferences = new Preferences(this);
    private ImageButton selectFolderButton;
    private EditText selectFolderText;
    private Button syncStartButton;
    private Button syncStopButton;
    private Button logsSwitchButton;
    private TextView logsTextView;
    private ScrollView logsContainer;
    private ConstraintLayout baseLayout;
    private LinearLayout contentContainer;
    private View serviceStatusDot;
    private TextView serviceStatusText;
    private long lastFileSize = -1;
    private int fileReaderDelay = 1000;
    private Handler fileReaderHandler = new Handler(Looper.getMainLooper());
    private Runnable fileReader = new Runnable() {
        @Override
        public void run() {
            if(!preferences.isFileLoggingActivated()) return;
            File logFile = LoggerSettings.getLogFile(getApplicationContext(), SyncerService.logFileName);
            long currentFileSize = getFileSize(logFile);
            if (logsTextView == null || currentFileSize <= lastFileSize) {
                fileReaderHandler.postDelayed(this, fileReaderDelay);
                return;
            }
            if(currentFileSize > 0) {
                String content = "";
                content = readFile(logFile);
                content = content.replace(" ", "\u00A0");
                Spannable spannableString = new SpannableString(content);
                logsTextView.setText(spannableString);
            } else
                logsTextView.setText("");
            lastFileSize = currentFileSize;
            fileReaderHandler.postDelayed(this, fileReaderDelay);
        }
    };

    private BroadcastReceiver serviceStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.debug("Got service status intent");
            boolean isRunning = intent.getBooleanExtra(SyncerService.RUNNING_STATUS_CHANGED_INTENT_VALUE_KEY, false);
            refreshServiceStatus(isRunning);
            if(!isRunning && restartService) {
                restartService = false;
                startSyncService();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoggerSettings.apply();
        logger = LoggerFactory.getLogger(MainActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeHeaderLettersBold();
        getGuiElements();
        registerDocumentLauncher();
        registerServiceStatusListener();
        refreshGuiElements();
        selectFolderButton.setOnClickListener(view -> launchDirectoryIntent());
        selectFolderText.setOnClickListener(view -> launchDirectoryIntent());
        syncStartButton.setOnClickListener(view -> onStartButtonPressed());
        syncStopButton.setOnClickListener(view -> onStopButtonPressed());
        logsSwitchButton.setOnClickListener(view -> onSwitchLogsButtonPressed());
        logger.debug("Current service status: {}", SyncerService.running);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!needPostCreateInitialization) return;
        if(!hasFocus) return;
        if(preferences.isFileLoggingActivated()) switchLogReader(true);
        needPostCreateInitialization = false;
    }

    @Override
    protected void onDestroy() {
        logger.debug("onDestroy");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStatusListener);
    }

    private void onStartButtonPressed() {
        if (preferences.getBaseFolderPath() == null) return;
        if(!checkNotificationPermissionGranted()) requestNotificationPermission();
        else {
            if(SyncerService.running) {
                restartService = true;
                stopSyncService();
            } else startSyncService();
        }
    }
    private void onStopButtonPressed() {
        stopSyncService();
    }
    private void onSwitchLogsButtonPressed() {
        switchLogReader(logsContainer.getVisibility() == View.GONE);
    }

    private void switchLogReader(boolean activate) {
        if(activate) {
            preferences.setLoggingActivated(true);
            sendFileLogStatus(true);
            fileReaderHandler.post(fileReader);
            logsTextView.setText("Loading logs...");
            logsContainer.setVisibility(View.VISIBLE);
            float toHeight = (baseLayout.getHeight() - contentContainer.getHeight()) * 0.6f;
            animateLogsContainerHeight(0, Math.round(toHeight), null);
            logsSwitchButton.setText("Hide logs");
        } else {
            preferences.setLoggingActivated(false);
            sendFileLogStatus(false);
            lastFileSize = -1;
            animateLogsContainerHeight(logsContainer.getHeight(), 0, () -> {
                logsContainer.setVisibility(View.GONE);
                logsTextView.setText("");
            });
            logsSwitchButton.setText("Show logs");
        }
    }

    private void animateLogsContainerHeight(int fromHeight, int toHeight, Runnable onFinish) {
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener(animation -> {
            logsContainer.getLayoutParams().height = (int) animation.getAnimatedValue();
            logsContainer.requestLayout();
        });
        if(onFinish != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onFinish.run();
                }
            });
        }
        animator.setDuration(300);
        animator.start();
    }
    private boolean checkNotificationPermissionGranted() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        boolean permissionGranted = permissionStatus == PackageManager.PERMISSION_GRANTED;
        logger.debug("POST_NOTIFICATIONS permission granted: " + permissionGranted);
        return permissionGranted;
    }
    private void requestNotificationPermission() {
        logger.debug("Requesting POST_NOTIFICATIONS permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1337);
    }
    private void startSyncService() {
        logger.debug("Current service status: {}", SyncerService.running);
        logger.debug("Start sync service");
        lastFileSize = -1;
        Intent intent = new Intent(getApplicationContext(), SyncerService.class);
        ContextCompat.startForegroundService(this, intent);
    }
    private void stopSyncService() {
        sendStopIntent();
    }
    private void launchDirectoryIntent() {
        logger.debug("Open directory chooser");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        openDocumentTreeLauncher.launch(intent);
    }
    private void registerDocumentLauncher() {
        openDocumentTreeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            preferences.setBaseFolderPath(uri.toString());
                            refreshGuiElements();
                        }
                    }
                }
        );
    }
    private void registerServiceStatusListener() {
        IntentFilter filter = new IntentFilter(SyncerService.RUNNING_STATUS_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(serviceStatusListener, filter);
    }

    private void getGuiElements() {
        selectFolderButton = findViewById(R.id.selectFolderBtn);
        selectFolderText = findViewById(R.id.selectFolderText);
        syncStartButton = findViewById(R.id.startSyncService);
        syncStopButton = findViewById(R.id.stopSyncService);
        logsSwitchButton = findViewById(R.id.logSwitcher);
        logsTextView = findViewById(R.id.logsTextView);
        logsContainer = findViewById(R.id.logsContainer);
        logsSwitchButton = findViewById(R.id.logSwitcher);
        baseLayout = findViewById(R.id.baseLayout);
        contentContainer = findViewById(R.id.centerContainer);
        serviceStatusDot = findViewById(R.id.statusDot);
        serviceStatusText = findViewById(R.id.statusText);
    }
    private void refreshGuiElements() {
        String baseFolderPath = preferences.getBaseFolderPath();
        if(baseFolderPath == null) {
            syncStartButton.setActivated(false);
            syncStopButton.setVisibility(View.GONE);
            selectFolderText.setText("Please select");
        }
        else {
            syncStartButton.setActivated(true);
            try {
                String decodedPath = URLDecoder.decode(Uri.parse(baseFolderPath).getLastPathSegment(), "UTF-8");
                selectFolderText.setText("/" + decodedPath.split(":")[1]);
            } catch (UnsupportedEncodingException ex) {
                logger.error("Can't decode path: {}", baseFolderPath);
            }
        }
        refreshServiceStatus(SyncerService.running);
    }
    private void refreshServiceStatus(boolean isRunning) {
        if(isRunning) {
            serviceStatusText.setText("Active");
            serviceStatusDot.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0,255,0)));
            syncStartButton.setText("Restart service");
            syncStopButton.setVisibility(View.VISIBLE);
        } else {
            serviceStatusText.setText("Inactive");
            serviceStatusDot.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,0,0)));
            syncStartButton.setText("Start service");
            syncStopButton.setVisibility(View.GONE);
        }
    }
    private void makeHeaderLettersBold() {
        TextView headerText = findViewById(R.id.header);
        SpannableString styledHeaderText = new SpannableString("Synchronisation over Network");
        styledHeaderText.setSpan(new StyleSpan(Typeface.BOLD), 0,1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        styledHeaderText.setSpan(new StyleSpan(Typeface.BOLD), 16,17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        styledHeaderText.setSpan(new StyleSpan(Typeface.BOLD), 21,22, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        headerText.setText(styledHeaderText);
    }
    private void sendFileLogStatus(boolean isActive) {
        Intent intent = new Intent(FILE_LOG_STATUS_CHANGED);
        intent.putExtra(FILE_LOG_STATUS_CHANGED_INTENT_VALUE_KEY, isActive);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    private void sendStopIntent() {
        Intent intent = new Intent(REQUEST_STOP_SERVICE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    private long getFileSize(File file) {
        if(file == null) return 0;
        if(!file.exists() || !file.isFile()) return 0;
        return file.length();
    }
    private String readFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException ex) {
            logger.error("Read file exception: ", ex);
            return "";
        }
        return stringBuilder.toString();
    }
}