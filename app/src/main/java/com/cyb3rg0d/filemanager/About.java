package com.cyb3rg0d.filemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ListView aboutListView = findViewById(R.id.about_list);
        ArrayList<String> aboutList = new ArrayList<>();
        aboutList.add("Report");
        aboutList.add("More apps");
        aboutList.add("License");
        aboutList.add("Privacy Policy");
        aboutList.add("Icons");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,aboutList);
        aboutListView.setAdapter(arrayAdapter);

        aboutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CYB3R-G0D/File-Manager/issues"));
                    startActivity(browserIntent);
                }
                if (position==1){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CYB3R-G0D/"));
                    startActivity(browserIntent);
                }
                if (position==2){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/CYB3R-G0D/File-Manager/main/LICENSE.txt"));
                    startActivity(browserIntent);
                }
                if (position==3){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CYB3R-G0D/PRIVACY.md#privacy-policy"));
                    startActivity(browserIntent);
                }
                if (position==4){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com/"));
                    startActivity(browserIntent);
                }
            }
        });
    }
}