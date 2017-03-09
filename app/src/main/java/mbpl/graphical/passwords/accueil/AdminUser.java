package mbpl.graphical.passwords.accueil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;

import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;

public class AdminUser extends AppCompatActivity {

    Button btnAdmin, btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_admin_user);

        //Récupérer les vues
        btnAdmin = (Button) findViewById(R.id.adminButton);
        btnUser = (Button) findViewById(R.id.userButton);

        Intent intent = getIntent();
        final int position = intent.getIntExtra("methode",-1);

        // action bar
        setTitle(implementedMethods.get(position).getNom());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(AdminUser.this, implementedMethods.get(position).getConfiguration());
                startActivity(authentification);
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification;

                Methode m;
                MethodeManager mm = new MethodeManager(AdminUser.this);
                mm.open();
                m = mm.getMethode(implementedMethods.get(position));
                if (!mm.defaultPassword(m)) {
                    //si il y a un mdp defini
                    authentification = new Intent(AdminUser.this, implementedMethods.get(position).getAuthentification());
                } else {
                    //si il n'y a pas de mdp defini
                    authentification = new Intent(AdminUser.this, implementedMethods.get(position).getInformation());
                }

                mm.close();
                startActivity(authentification);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
