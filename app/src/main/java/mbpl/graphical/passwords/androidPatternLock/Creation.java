package mbpl.graphical.passwords.androidPatternLock;

import mbpl.graphical.passwords.accueil.AccueilAdmin;
import mbpl.graphical.passwords.accueil.AccueilAdminUser;
import mbpl.graphical.passwords.accueil.AccueilUser;
import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Creation extends ActionBarActivity {

    private FrameLayout enterPatternContainer, confirmPatternContainer;
    private Lock9View lockViewFirstTry, lockViewConfirm;
    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;
    SharedPreferences prefs;
    Editor editor;
    TextView tvMsg;
    Button btnRecommencer;
    int pointsMinimum;

    //BDD
    private MethodeManager methodeManager;
    protected Methode methode = new PatternLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_pattern_lock);
        //evenement Bouton
        evenementButton();

        // action bar
        setTitle("Pattern Lock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //récupérer le contexte de la bdd
        methodeManager = new MethodeManager(getApplicationContext());
        //Récupération du nombre de points minimum dans la bd
        //Récupération du nombre de points et du nombre de points minimum définis par l'admin
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        pointsMinimum = methode.getParam2();
        methodeManager.close();


        //Création des préférences afin de stocker le mdp
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        //Message situé en haut
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        tvMsg.setText(getResources().getString(R.string.draw_pattern_msg));

        //Récupérer les deux frameLayout. VOir le fichier xml activity_creation_pattern_lock
        enterPatternContainer = (FrameLayout) findViewById(R.id.enterPattern);
        confirmPatternContainer = (FrameLayout) findViewById(R.id.confirmPattern);

        //Récupérer les deux lock9View. Voir le fichier xml activity_creation_pattern_lock
        lockViewFirstTry = (Lock9View) findViewById(R.id.lock_viewFirstTry);
        lockViewConfirm =  (Lock9View) findViewById(R.id.lock_viewConfirm);
        //Cacher la vue confirm
        confirmPatternContainer.setVisibility(View.GONE);

//		we can get a call back string when ever user interacts with the pattern lock view
        lockViewFirstTry.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password, int nombre, StringBuilder ok) {

                StringBuilder str = ok;
                if (nombre >= pointsMinimum) {
                    PATTERN_KEY = password;
                    enterPatternContainer.setVisibility(View.GONE);
                    tvMsg.setText(getResources().getString(R.string.redraw_confirm_pattern_msg));
                    confirmPatternContainer.setVisibility(View.VISIBLE);
                    btnRecommencer.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getApplicationContext(), "Votre mot de passe doit contenir au minimum " + pointsMinimum +" points. Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
            }

        });


        lockViewConfirm.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password, int nombre,  StringBuilder ok) {
                if(password.equals(PATTERN_KEY)){

                    //Ecriture dans la BDD
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setPassword(methode, "PATTERN_KEY");
                    methodeManager.close();

                    //Suite traitement
                    Toast.makeText(getApplicationContext(), "Le mot de passe a été créé avec succès.", Toast.LENGTH_SHORT).show();
                    editor.putString("Pattern", password);
                    editor.commit();

                    //On se redirige vers la page d'accueil de l'utilisateur
                    Intent intent = new Intent(Creation.this, AccueilUser.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Vous avez dessiné le mauvais chemin.\n Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void evenementButton(){
        //Evenement bouton
        btnRecommencer = (Button) findViewById(R.id.buttonPatternLockCreationRecommencer);
        btnRecommencer.setVisibility(View.GONE);

        //Lors d'un clique, on revient à l'acitivité pour recommencer la saisie
        btnRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(Creation.this, Creation.class);
                startActivity(authentification);
                finish();
            }
        });
    }



    //Afin de remplir le test d'acception suivant: quand un user appuie sur annuler alors il revient sur l'écran de création d'un pattern
    @Override
    public void onBackPressed() {

        Intent authentification;
        //On peut seulement se rediriger vers la page de création d'un nouveau mdp si l'utilisateur a au moins entré une fois son pattern. Cela n'a aucune utilité sinon
        //Si le bouton recommencer est visible alors on sait qu'on se trouve dans la seconde partie de création lorsque l'utilisateur doit valider son pattern en le remettant.
        if (btnRecommencer.isShown()) {
            authentification = new Intent(Creation.this, Creation.class);
            startActivity(authentification);
        }
        //Sinon on revient à l'écran d'accueil
        else
        {
            authentification = new Intent(Creation.this, AccueilUser.class);
            startActivity(authentification);
        }
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Étend le menu ; ceci ajoute des items à la barre d'actions si elle est présente.
        getMenuInflater().inflate(R.menu.menu_change, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.contact:
                new AlertDialog.Builder(this)
                        .setTitle("Android Pattern Lock")
                        .setMessage("Prototype version 1")
                        .setPositiveButton("OK", null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    */
}
