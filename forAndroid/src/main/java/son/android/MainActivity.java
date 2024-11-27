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
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String TEMPLATE_BASE_FOLDER = "Synchronisation base folder: %s";
    private ActivityResultLauncher<Intent> openDocumentTreeLauncher;
    private Preferences preferences = new Preferences(this);
    private TextView folderText;
    private Button selectFolderBtn;
    private Button syncStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getGuiElements();
        registerDocumentLauncher();
        refreshGuiElements();
        selectFolderBtn.setOnClickListener(view -> launchDirectoryIntent());
        syncStartBtn.setOnClickListener(view -> onStartButtonPressed());
    }
    private void onStartButtonPressed() {
        if(checkNotificationPermissionGranted()) startSyncService();
        else requestNotificationPermission();
    }
    private boolean checkNotificationPermissionGranted() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
        boolean permissionGranted = permissionStatus == PackageManager.PERMISSION_GRANTED;
        System.out.println("MainActivity - POST_NOTIFICATIONS permission granted: " + permissionGranted);
        return permissionGranted;
    }
    private void requestNotificationPermission() {
        System.out.println("MainActivity - Requesting POST_NOTIFICATIONS permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1337);
    }
    private void startSyncService() {
        System.out.println("MainActivity - Start sync service");
        Intent intent = new Intent(getApplicationContext(), SyncerService.class);
        ContextCompat.startForegroundService(this, intent);
    }
    private void launchDirectoryIntent() {
        System.out.println("MainActivity - Open directory chooser");
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
        folderText = findViewById(R.id.baseFolder);
        selectFolderBtn = findViewById(R.id.selectFolder);
        syncStartBtn = findViewById(R.id.startSyncService);
    }
    private void refreshGuiElements() {
        String baseFolderPath = preferences.getBaseFolderPath();
        if(baseFolderPath == null) {
            syncStartBtn.setActivated(false);
            folderText.setText(String.format(TEMPLATE_BASE_FOLDER, "Not selected"));
        }
        else {
            syncStartBtn.setActivated(true);
            DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(baseFolderPath));
            folderText.setText(String.format(TEMPLATE_BASE_FOLDER, documentFile.getName()));
        }
    }

}