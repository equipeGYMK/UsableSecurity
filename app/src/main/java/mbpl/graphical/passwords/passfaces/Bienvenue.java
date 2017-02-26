package mbpl.graphical.passwords.passfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.accueil.AccueilUser;
import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;

public class Bienvenue extends AppCompatActivity {


    Button btnRetour, btnReinit;
    //BDD
    private MethodeManager methodeManager;
    private Class creationClass;
    protected Methode methode = new Passfaces();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue_authentification);

        //récupérer le contexte
        //récupérer le contexte de la bdd
        methodeManager = new MethodeManager(getApplicationContext());

        //création des boutons
        btnRetour = (Button) findViewById(R.id.buttonRetourPatternLock);
        btnReinit = (Button) findViewById(R.id.buttonReinit);


        //Evévenement des boutons
        btnReinit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    //On réinitialise le mot de passe à " "
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                //Récupération de la classe création pour la redirection lors du clic sur le bouton enregistrer nouveau mdp
                    creationClass = methode.getCreation();
                    methodeManager.close();

                    //On retourne à l'activité accueil
                    Intent authentification = new Intent(Bienvenue.this, creationClass);
                    startActivity(authentification);
                    finish();
            }
        });


        btnRetour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //On retourne à l'activité accueil sans rien faire
                Intent authentification = new Intent(Bienvenue.this, AccueilUser.class);
                startActivity(authentification);
                finish();
            }
        });



    }
}
