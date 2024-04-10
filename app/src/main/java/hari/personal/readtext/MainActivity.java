package hari.personal.readtext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    ListView lv_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_search = findViewById(R.id.lv_recent);
        findViewById(R.id.btn).setOnClickListener(v -> {
            Intent pickFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pickFileIntent.setType("*/*");
            startActivityForResult(pickFileIntent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                // Perform operations on the document using its URI.
                Intent openFileIntent = new Intent(MainActivity.this, ScrollingActivity.class);
                openFileIntent.setAction(Intent.ACTION_SEND);
                openFileIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(openFileIntent);
            }
        }
    }
}