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
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
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

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    Button btn;
    TextView text;

    Syncer syncer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//
//        String path = Environment.getExternalStorageDirectory().toString() + "/syyyncFolder";
//        System.out.println(path);
//        File dir = new File(path);
//        System.out.println("mkdir: " + dir.mkdir());
//
//        Thread thread = new Thread(() -> {
//            File dataFolder = getBaseContext().getDataDir();
//            File syncFolder = new File(dataFolder, "sync");
//            if(!syncFolder.exists()) syncFolder.mkdir();
//            syncer = new Syncer(syncFolder);;
//            /*while(true) {
//                try {
//                    Thread.sleep(10000);
//                    syncer.sync();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }*/
//        });
//
//        thread.start();

        openDirectory();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.testText);
        btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //askPermissionAndBrowseFile();
//                //sync();
//            }
//        });
    }


    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 1234);
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                System.out.println(uri);
                DocumentFile folder = DocumentFile.fromTreeUri(getApplicationContext(), uri);
                DocumentFile foundFileForName = folder.findFile("letssgooo");
                if(foundFileForName != null) {
                    try {
                        DocumentsContract.deleteDocument(getApplicationContext().getContentResolver(), foundFileForName.getUri());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                DocumentFile newFile = folder.createFile("", "letssgooo");

                try {
                    ParcelFileDescriptor pfd = this.getContentResolver().
                            openFileDescriptor(newFile.getUri(), "w");
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(pfd.getFileDescriptor());
                    fileOutputStream.write(("Overwritten at " + System.currentTimeMillis() +
                            "\n").getBytes());
                    // Let the document provider know you're done by closing the stream.
                    fileOutputStream.close();
                    pfd.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }




//    private void askPermissionAndBrowseFile()  {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            int permission = ActivityCompat.checkSelfPermission(getBaseContext(),
//                    Manifest.permission.MANAGE_EXTERNAL_STORAGE);
//
//            if (permission != PackageManager.PERMISSION_GRANTED ) {
//                this.requestPermissions(
//                        new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
//                        MY_REQUEST_CODE_PERMISSION
//                );
//
//                /*this.requestPermissions(
//                        new String[]{Manifest.permission.INTERNET},
//                        MY_REQUEST_CODE_PERMISSION
//                );*/
//
//                return;
//            }
//        }
//        doBrowseFile();
//        //sync();
//    }
//
//    private void doBrowseFile()  {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        startActivityForResult(intent, MY_RESULT_CODE_FILECHOOSER);
//
//        /*registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), new ActivityResultCallback<Uri>() {
//            @Override
//            public void onActivityResult(Uri result) {
//                System.out.println("result: " + result);
//            }
//        });*/
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //
//        switch (requestCode) {
//            case MY_REQUEST_CODE_PERMISSION: {
//
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Toast.makeText(this.getBaseContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
//
//                    doBrowseFile();
//                }
//
//                else {
//                    Toast.makeText(getBaseContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case MY_RESULT_CODE_FILECHOOSER:
//                if (resultCode == Activity.RESULT_OK ) {
//                    if(data != null)  {
//                        Uri fileUri = data.getData();
//                        String path = fileUri.getPath().split(":")[1];
//                        File file = new File(Environment.getExternalStorageDirectory(), path);
//                        System.out.println("file: " + file);
//                        System.out.println("canRead: " + file.canRead());
//                        System.out.println("canExecute: " + file.canExecute());
//                        System.out.println("canWrite: " + file.canWrite());
//                        File testTxt = new File(file, "tteest");
//                        try {
//                            System.out.println("createNewFIle: " + testTxt.createNewFile());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        /*SharedPreferences settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString("syncPath", file.getPath());
//                        editor.apply();
//                        File f = new File(file, "dasdasd");
//                        try {
//                            f.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }*/
//                        //sync();
//                        //String[] fileNames = new File(fileUri.toString()).list();
//                        //File[] files = new File(fileUri.toString()).listFiles();
//                        //text.setText("path: " + path);
//                    }
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//
//
//    private void sync() {
//        Thread thread = new Thread(() -> {
//            /*File dataFolder = getBaseContext().getDataDir();
//            File syncFolder = new File(dataFolder, "sync");
//            if(!syncFolder.exists()) syncFolder.mkdir();*/
//            System.out.println("start syncing");
//            syncer.sync();
//            System.out.println("sync done");
//        });
//
//        thread.start();
//    }
}