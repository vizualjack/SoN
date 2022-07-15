package son.android;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import son.Syncer;
import son.network.ClientHolder;

public class MainActivity extends AppCompatActivity {

    private static final int MY_RESULT_CODE_FILECHOOSER = 1337;
    public static final String PATH_KEY = "SYNC_FOLDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //savePath(null);
        startSyncer();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button btn = (Button)findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), SyncerActivity.class);
//                getApplicationContext().startForegroundService(intent);
//            }
//        });
    }

    private void startSyncer() {
        if(getPath() == null) {
            openDirectory();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), SyncerActivity.class);
            getApplicationContext().startForegroundService(intent);
        }
    }


    public void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, MY_RESULT_CODE_FILECHOOSER);
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == MY_RESULT_CODE_FILECHOOSER && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags); //safe to just ask once for folder
                savePath(uri.toString());
            }
        }
        startSyncer();
    }

    private void savePath(String path) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(PATH_KEY, path);
        editor.apply();
    }

    private String getPath() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(PATH_KEY, null);
    }
}