package son.android;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        Thread thread = new Thread(() -> {
            File dataFolder = getBaseContext().getDataDir();
            File syncFolder = new File(dataFolder, "sync");
            if(!syncFolder.exists()) syncFolder.mkdir();
            syncer = new Syncer(syncFolder);

            /*while(true) {
                try {
                    Thread.sleep(10000);
                    syncer.sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        });

        thread.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.testText);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionAndBrowseFile();
                //sync();
            }
        });
    }

    private void askPermissionAndBrowseFile()  {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.INTERNET);

            if (permission != PackageManager.PERMISSION_GRANTED ) {
                /*this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_REQUEST_CODE_PERMISSION
                );*/

                this.requestPermissions(
                        new String[]{Manifest.permission.INTERNET},
                        MY_REQUEST_CODE_PERMISSION
                );

                return;
            }
        }
        //doBrowseFile();
        sync();
    }

    private void doBrowseFile()  {
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //startActivityForResult(intent, MY_RESULT_CODE_FILECHOOSER);

        /*registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                System.out.println("result: " + result);
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this.getBaseContext(), "Permission granted!", Toast.LENGTH_SHORT).show();

                    doBrowseFile();
                }

                else {
                    Toast.makeText(getBaseContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        Uri fileUri = data.getData();
                        String path = fileUri.getPath().split(":")[1];
                        File file = new File(Environment.getExternalStorageDirectory(), path);
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("myPrefs", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("syncPath", file.getPath());
                        editor.apply();
                        File f = new File(file, "dasdasd");
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //sync();
                        //String[] fileNames = new File(fileUri.toString()).list();
                        //File[] files = new File(fileUri.toString()).listFiles();
                        //text.setText("path: " + path);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void sync() {
        Thread thread = new Thread(() -> {
            /*File dataFolder = getBaseContext().getDataDir();
            File syncFolder = new File(dataFolder, "sync");
            if(!syncFolder.exists()) syncFolder.mkdir();*/
            System.out.println("start syncing");
            syncer.sync();
            System.out.println("sync done");
        });

        thread.start();
    }
}