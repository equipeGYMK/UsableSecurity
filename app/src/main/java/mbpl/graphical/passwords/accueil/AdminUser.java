package mbpl.graphical.passwords.accueil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;
import mbpl.graphical.passwords.sqlite.PatternLock;

import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;

public class AdminUser extends AppCompatActivity {

    private Button btnAdmin, btnUser;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static String MY_PREFS_NAME = "Passfaces";
    int valide;
    private boolean isValide;

    private int nombreEssai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_admin_user);

        //Récupérer les vues
        btnAdmin = (Button) findViewById(R.id.adminButton);
        btnUser = (Button) findViewById(R.id.userButton);

        Intent intent = getIntent();
        final int position = intent.getIntExtra("methode",-1);

        //prefs
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(implementedMethods.get(position).getNameSavePref(), MODE_PRIVATE);
        nombreEssai = prefs.getInt("nbTentative", 3);

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
                isValide = false;

                Methode m;
                MethodeManager mm = new MethodeManager(getApplicationContext());
                mm.open();
                m = mm.getMethode(implementedMethods.get(position));
                valide = m.getParam2();

                if ((valide == 1) && (implementedMethods.get(position) instanceof Passfaces))
                    isValide = true;

                System.out.println("value: " + isValide);

                if (((!mm.defaultPassword(m)) && (isValide)) || ((!mm.defaultPassword(m)) && (implementedMethods.get(position) instanceof PatternLock))) {
                    if (nombreEssai > 0 ) {
                        authentification = new Intent(AdminUser.this, implementedMethods.get(position).getAuthentification());
                        mm.close();
                        startActivity(authentification);
                    }else {
                        Toast.makeText(AdminUser.this, "Vous n'avez plus d'essai restants !\nLa technique est bloquée", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    authentification = new Intent(AdminUser.this, implementedMethods.get(position).getInformation());
                    mm.close();
                    startActivity(authentification);
                }
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
