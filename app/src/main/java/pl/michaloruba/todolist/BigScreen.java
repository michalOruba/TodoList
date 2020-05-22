package pl.michaloruba.todolist;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BigScreen extends AppCompatActivity {

    ImageView imgSoloPhoto;
    Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solo_picture);

        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");

        imgSoloPhoto = findViewById(R.id.imgSoloPhoto);
        imgSoloPhoto.setImageBitmap( bitmap);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
