package com.lmntrx.livin.sharepic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static int CAMERA_REQUEST_CODE = 0;

    Bitmap capturedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        FloatingActionButton share_fab = (FloatingActionButton) findViewById(R.id.share_fab);
        share_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (capturedImage != null){
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);

                    }else {
                        Bitmap icon = capturedImage;
                        if (icon != null) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/jpeg");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                            try {
                                if (f.createNewFile()) {
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(bytes.toByteArray());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(getString(R.string.file_uri)));
                            startActivity(Intent.createChooser(share, "Share Via"));
                        }

                    }
                }else {
                    Toast.makeText(MainActivity.this, "Capture image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            ImageView imageView = (ImageView) findViewById(R.id.imageHolder);
            capturedImage = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(capturedImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
