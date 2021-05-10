package com.wyre.trade.stock;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.squareup.picasso.Picasso;

public class NewsActivity extends AppCompatActivity {

    TextView mNewsTitle, mNewsSummary, mNewsUrl, mNewsDate;
    ImageView mNewsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
         getSupportActionBar().setTitle("");

        mNewsTitle = findViewById(R.id.title);
        mNewsSummary = findViewById(R.id.summary);
        mNewsDate = findViewById(R.id.date);
        mNewsUrl = findViewById(R.id.url);
        mNewsImage = findViewById(R.id.image);

        mNewsTitle.setText(getIntent().getStringExtra("title"));
        mNewsSummary.setText(getIntent().getStringExtra("summary"));
        mNewsUrl.setText(getIntent().getStringExtra("url"));
        mNewsDate.setText(getIntent().getStringExtra("date"));

        if(!getIntent().getStringExtra("image").equalsIgnoreCase(""))
            Picasso.with(getBaseContext())
                .load(getIntent().getStringExtra("image"))
                .into(mNewsImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
