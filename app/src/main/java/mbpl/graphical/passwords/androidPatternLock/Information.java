package mbpl.graphical.passwords.androidPatternLock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.R;

public class Information extends AppCompatActivity {

    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_apl);

        btnOk = (Button) findViewById(R.id.button_info_AndroidPatternLock);

        setTitle("Pattern Lock Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(Information.this, Creation.class);
                startActivity(authentification);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
