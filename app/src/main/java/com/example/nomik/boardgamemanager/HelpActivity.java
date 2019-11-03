package com.example.nomik.boardgamemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {
    int next;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        imageView = findViewById(R.id.imageView_help);

        Bundle bundle = getIntent().getExtras();
        int num = bundle.getInt("fragmentNum");
        switch (num) {
            case 0:
                imageView.setImageResource(R.drawable.image_help_main);
                imageView.setOnClickListener(close);
                break;
            case 1:
                imageView.setImageResource(R.drawable.image_help_stat);
                imageView.setOnClickListener(close);
                break;
            case 2:
                next = 0;
                imageView.setImageResource(R.drawable.image_help_map);
                imageView.setOnClickListener(nextImageMap);
                break;
            case 3:
                imageView.setImageResource(R.drawable.image_help_search);
                imageView.setOnClickListener(close);
                break;
            case 4:
                imageView.setImageResource(R.drawable.image_help_player);
                imageView.setOnClickListener(close);
                break;
            case 5:
                imageView.setImageResource(R.drawable.image_help_tracker);
                imageView.setOnClickListener(close);
                break;
            case 6:
                imageView.setImageResource(R.drawable.image_help_score);
                imageView.setOnClickListener(close);
                break;
        }
    }

    View.OnClickListener close = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    View.OnClickListener nextImageMap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (next) {
                case 0:
                    imageView.setImageResource(R.drawable.image_help_map_create);
                    next++;
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.image_help_map_host);
                    next++;
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.image_help_map_entry);
                    imageView.setOnClickListener(close);
                    next = 0;
                    break;
                default:
                    break;
            }
        }
    };
}
