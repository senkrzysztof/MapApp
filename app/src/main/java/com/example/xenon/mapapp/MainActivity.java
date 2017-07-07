package com.example.xenon.mapapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private Button b1;
    private Button bwrite;
    private Button bcompass;
    public String filename;
    private String path;

    private boolean fileOperation = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("dir", getApplicationContext().getFilesDir().toString());
        path=getApplicationContext().getFilesDir().toString()+"/folder";
        File myDir = new File(getFilesDir(), "folder");
        if(!myDir.exists()) {
            if(myDir.mkdir());
        }
        Log.i("dir2", path);

        filename="";

        b1 = (Button)findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                myIntent.putExtra("FILENAME", filename);
                MainActivity.this.startActivity(myIntent);
            }
        });
        bwrite = (Button)findViewById(R.id.button3);
        bwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createDialog();
            }
        });
        bcompass = (Button)findViewById(R.id.button4);
        bcompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CompassActivity.class);
                MainActivity.this.startActivity(myIntent);
                //startActivity(myIntent);
            }
        });
    }

    public String[] ListAllFiles(){
        String path = getApplicationContext().getFilesDir().toString();
        Log.d("Files from", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        //String [] file_names=new String [files.length];
        List<String> file_names=new ArrayList<>();
        Log.d("Files", "Size: "+ files.length);
        int j=0;
        for (int i = 0; i< files.length; i++)
        {
            if(files[i].getName().startsWith("trasa")) {
                file_names.add(files[i].getName());
                j++;
            }
        }
        j=0;
        String [] fl2=new String[file_names.size()];

        for(String s : file_names){
            if(s!=null && s!="") {
                fl2[j] = s;
                j++;
            }
        }

        return fl2;
    }

    public void createDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Lista tras");
        final String[] options=ListAllFiles();
        for(int i=0; i<options.length; i++){
            Log.i("opt "+i, options[i]);
        }

        builder.setItems(options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename=options[which];
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                myIntent.putExtra("FILENAME", filename);
                MainActivity.this.startActivity(myIntent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



}
