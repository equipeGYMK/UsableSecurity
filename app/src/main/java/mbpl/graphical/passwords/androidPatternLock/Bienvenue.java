package mbpl.graphical.passwords.androidPatternLock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

public class Bienvenue extends AppCompatActivity {


    Button btnRetour, enregistrerNewMdp;
    private MethodeManager methodeManager;
    private Class creationClass;
    protected Methode methode = new PatternLock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue_authentification);

        setTitle("Pattern Lock Authentification");

        methodeManager = new MethodeManager(getApplicationContext());

        btnRetour = (Button) findViewById(R.id.buttonRetourPatternLock);
        enregistrerNewMdp = (Button) findViewById(R.id.buttonReinit);

        enregistrerNewMdp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    creationClass =  methode.getCreation();
                    methodeManager.close();

                    Intent authentification = new Intent(Bienvenue.this, creationClass);
                    startActivity(authentification);
                    finish();
            }
        });


        btnRetour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(Bienvenue.this, Accueil.class);
                startActivity(authentification);
                finish();
            }
        });



    }
}
