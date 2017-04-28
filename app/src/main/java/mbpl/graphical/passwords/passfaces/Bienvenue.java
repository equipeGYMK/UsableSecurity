package mbpl.graphical.passwords.passfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;

public class Bienvenue extends AppCompatActivity {


    Button btnRetour, btnReinit;
    //prefs
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static String MY_PREFS_NAME = "Passfaces";

    //BDD
    private MethodeManager methodeManager;
    private Class creationClass;
    protected Methode methode = new Passfaces();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue_authentification);

        // action bar
        setTitle("Passfaces Bienvenue");

        //récupérer le contexte
        //récupérer le contexte de la bdd
        methodeManager = new MethodeManager(getApplicationContext());

        //création des boutons
        btnRetour = (Button) findViewById(R.id.buttonRetourPatternLock);
        btnReinit = (Button) findViewById(R.id.buttonReinit);

        //prefs
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        //Evévenement des boutons
        btnReinit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //On réinitialise le mot de passe à " "
                methodeManager.open();
                methodeManager.setPassword(methode, "");
                methodeManager.setParam(methode, methode.getParam1(), 0);
                methodeManager.close();
                Toast.makeText(Bienvenue.this, "Votre mot de passe a été réinitialisé", Toast.LENGTH_SHORT).show();

                //On retourne à l'activité accueil
                Intent authentification = new Intent(Bienvenue.this, Presentation.class);
                startActivity(authentification);
                finish();
            }
        });


        btnRetour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //On retourne à l'activité accueil sans rien faire
                Intent authentification = new Intent(Bienvenue.this, Accueil.class);
                startActivity(authentification);
                finish();
            }
        });



    }
}
