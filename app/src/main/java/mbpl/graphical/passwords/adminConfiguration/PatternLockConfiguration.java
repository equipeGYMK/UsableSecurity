package mbpl.graphical.passwords.adminConfiguration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

public class PatternLockConfiguration extends AppCompatActivity {


    private Spinner spinnerNbPoints, spinnerPointsMin;
    private Button btnSubmit, btnInit, btnInitEssai;
    private int nbPointsActuel, pointsMinimum;


    //BD
    protected Methode methode =  new PatternLock();
    private MethodeManager methodeManager;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static String MY_PREFS_NAME = "PatternLock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock_configuration);

        // action bar
        setTitle("Pattern Lock Configuration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        //mise en place de la bd
        methodeManager = new MethodeManager(getApplicationContext());
        //Récupération du nombre de points et du nombre de points minimum définis par l'admin
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        nbPointsActuel = methode.getParam1();
        pointsMinimum = methode.getParam2();
        methodeManager.close();

        //Création des spinners
        addItemsOnSpinnerNbPoints();
        addItemsOnSpinnerPointsMin();
        addListenerOnButton();

    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerNbPoints() {

        spinnerNbPoints = (Spinner) findViewById(R.id.spinnerConfigPatternLockNbPoints);
        List<String> list = new ArrayList<String>();
        list.add("9");
        list.add("16");
        list.add("25");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNbPoints.setAdapter(dataAdapter);

        //Initialiser la bonne valeur du spinner avec le nombre actuel de points
        int spinnerPositionNbPoints = dataAdapter.getPosition(String.valueOf(nbPointsActuel));
        //set the default according to value
        spinnerNbPoints.setSelection(spinnerPositionNbPoints, false);
    }


    // add items into spinner dynamically
    public void addItemsOnSpinnerPointsMin() {

        spinnerPointsMin = (Spinner) findViewById(R.id.spinnerConfigAndroidPatternLockPointsMin);
        List<String> list = new ArrayList<String>();
        list.add("4");
        list.add("5");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPointsMin.setAdapter(dataAdapter);


        //Initialiser la bonne valeur du spinner avec le nombre actuel de points
        int spinnerPositionPointsMinimum = dataAdapter.getPosition(String.valueOf(pointsMinimum));

        System.out.println("position: "+ spinnerPositionPointsMinimum);
        System.out.println("position: "+ spinnerPositionPointsMinimum);
        //set the default according to value
        spinnerPointsMin.setSelection(spinnerPositionPointsMinimum, false);
    }


    // get the selected dropdown list value
    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.buttonConfigPattrnLock);
        btnInit = (Button) findViewById(R.id.buttonInitPatternLock);
        btnInitEssai = (Button) findViewById(R.id.buttonDebloPatternLock);

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nbPointsActuel != Integer.parseInt(String.valueOf(spinnerNbPoints.getSelectedItem()))
                        || pointsMinimum != Integer.parseInt(String.valueOf(spinnerPointsMin.getSelectedItem()))) {
                    //Modification des paramètres
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setParam(methode, Integer.parseInt(String.valueOf(spinnerNbPoints.getSelectedItem())), Integer.parseInt(String.valueOf(spinnerPointsMin.getSelectedItem())));
                    methodeManager.close();

                    initMotDePasse();
                }

                //Changement d'activité
                Intent authentification = new Intent(PatternLockConfiguration.this, Accueil.class);
                startActivity(authentification);
                finish();
            }
        });

        btnInit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMotDePasse();
            }
        });

        btnInitEssai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initTentative();
                Toast.makeText(PatternLockConfiguration.this, "Le nombre de tentative a été Réinitialisé", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initMotDePasse() {
        //Initialisation du mot de passe
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        methodeManager.setPassword(methode, "");
        methodeManager.close();
        Toast.makeText(PatternLockConfiguration.this, "Votre mot de passe a été Réinitialisé", Toast.LENGTH_SHORT).show();

        initTentative();
    }

    private void initTentative() {
        //Initialisation des tentatives
        editor.putInt("nbTentative", 3);
        editor.commit();
    }



    @Override
    protected void onStart(){
        super.onStart();
    }

    /*
    @Override
    public void onBackPressed() {
        //Retour à la page d'accueil lorsque l'on clique sur retour
        Intent authentification = new Intent(PatternLockConfiguration.this, AccueilAdmin.class);
        startActivity(authentification);
        finish();
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
