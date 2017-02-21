package mbpl.graphical.passwords.accueil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.R;

public class AccueilAdminUser extends AppCompatActivity {

    Button btnAdmin, btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_admin_user);

        //Récupérer les vues
        btnAdmin = (Button) findViewById(R.id.adminButton);
        btnUser = (Button) findViewById(R.id.userButton);


        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(AccueilAdminUser.this, AccueilAdmin.class);
                startActivity(authentification);
                finish();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(AccueilAdminUser.this, AccueilUser.class);
                startActivity(authentification);
                finish();
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
