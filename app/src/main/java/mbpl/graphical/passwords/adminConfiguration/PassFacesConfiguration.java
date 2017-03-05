package mbpl.graphical.passwords.adminConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;

public class PassFacesConfiguration extends AppCompatActivity {


    private Button btnSubmit, btnInit;
    //BD
    protected Methode methode =  new Passfaces();
    private MethodeManager methodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_faces_configuration);

        // action bar
        setTitle("PassFaces Configuration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mise en place de la bd
        methodeManager = new MethodeManager(getApplicationContext());
        //Evenement des boutons
        addListenerOnButton();
    }



    // get the selected dropdown list value
    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.buttonSubmitPassFace);
        btnInit = (Button) findViewById(R.id.buttonInitPassFacePassword);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Changement d'activité
                Intent authentification = new Intent(PassFacesConfiguration.this, Accueil.class);
                startActivity(authentification);
                finish();
            }
        });

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialisation du mot de passe
                methodeManager.open();
                methode = methodeManager.getMethode(methode);
                methodeManager.setPassword(methode, "");
                methodeManager.close();

                Toast.makeText(PassFacesConfiguration.this, "Votre mot de passe a été initialisé", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onStart(){
        super.onStart();
    }

    /*
    @Override
    public void onBackPressed() {
        //Retour à la page d'accueil lorsque l'on clique sur retour
        Intent authentification = new Intent(PassFacesConfiguration.this, AccueilAdmin.class);
        startActivity(authentification);
        finish();
    }*/

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
