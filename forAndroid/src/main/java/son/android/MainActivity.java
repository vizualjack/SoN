package son.android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import son.Syncer;

public class MainActivity extends AppCompatActivity {
    private static Logger logger;
    private ActivityResultLauncher<Intent> openDocumentTreeLauncher;
    private Preferences preferences = new Preferences(this);
    private ImageButton selectFolderBtn;
    private EditText selectFolderText;
    private Button syncStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoggerSettings.apply();
        logger = LoggerFactory.getLogger(MainActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeHeaderLettersBold();
        getGuiElements();
        registerDocumentLauncher();
        refreshGuiElements();
        selectFolderBtn.setOnClickListener(view -> launchDirectoryIntent());
        selectFolderText.setOnClickListener(view -> launchDirectoryIntent());
        syncStartBtn.setOnClickListener(view -> onStartButtonPressed());
        logger.debug("Current service status: {}", SyncerService.running);
    }
    private void onStartButtonPressed() {
        if (preferences.getBaseFolderPath() == null) return;
        if(checkNotificationPermissionGranted()) startSyncService();
        else requestNotificationPermission();
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
        Intent intent = new Intent(getApplicationContext(), SyncerService.class);
        ContextCompat.startForegroundService(this, intent);
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
    private void getGuiElements() {
        selectFolderBtn = findViewById(R.id.selectFolderBtn);
        selectFolderText = findViewById(R.id.selectFolderText);
        syncStartBtn = findViewById(R.id.startSyncService);
    }
    private void refreshGuiElements() {
        String baseFolderPath = preferences.getBaseFolderPath();
        if(baseFolderPath == null) {
            syncStartBtn.setActivated(false);
            selectFolderText.setText("Please select");
        }
        else {
            syncStartBtn.setActivated(true);
            try {
                String decodedPath = URLDecoder.decode(Uri.parse(baseFolderPath).getLastPathSegment(), "UTF-8");
                selectFolderText.setText("/" + decodedPath.split(":")[1]);
            } catch (UnsupportedEncodingException ex) {
                logger.error("Can't decode path: {}", baseFolderPath);
            }
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

}