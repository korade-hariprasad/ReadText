package hari.personal.readtext;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import hari.personal.readtext.databinding.ActivityScrollingBinding;

public class ScrollingActivity extends AppCompatActivity {

    TextView tv_name, tv_main;
    FloatingActionButton plus, minus;
    Intent uriIntent;
    ProgressBar progressBar;
    Uri file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        plus = findViewById(R.id.btn_plus);
        minus = findViewById(R.id.btn_minus);
        tv_main = findViewById(R.id.tv_main);
        tv_name = findViewById(R.id.tv_name);
        progressBar = findViewById(R.id.progressBar);
        uriIntent = getIntent();
        file = uriIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        tv_name.setText(getFileName(file));
        parseFileWithLoadingIndicator();
        plus.setOnClickListener(v -> tv_main.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv_main.getTextSize()+2));
        minus.setOnClickListener(v -> tv_main.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(tv_main.getTextSize()-2,1)));
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme != null) {
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (displayNameIndex != -1) {
                            fileName = cursor.getString(displayNameIndex);
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                // If the URI is a file URI, simply extract the last segment of the path
                fileName = uri.getLastPathSegment();
            }
        }
        return fileName;
    }

    private void parseFileWithLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                tv_main.setText(readTextFromUri(file));
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }, 2000);
    }
}