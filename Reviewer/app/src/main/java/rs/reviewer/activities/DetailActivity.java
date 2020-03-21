package rs.reviewer.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import rs.reviewer.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tvName = (TextView)findViewById(R.id.tvName);
        TextView tvDescr = (TextView)findViewById(R.id.tvDescr);

        tvName.setText(getIntent().getStringExtra("name"));
        tvDescr.setText(getIntent().getStringExtra("descr"));
    }

}
