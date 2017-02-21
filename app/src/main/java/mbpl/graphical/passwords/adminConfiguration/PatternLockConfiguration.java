package mbpl.graphical.passwords.adminConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.AccueilAdmin;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

public class PatternLockConfiguration extends Activity{


        private Spinner spinnerNbPoints, spinnerPointsMin;
        private Button btnSubmit, btnInit;
        private int nbPointsActuel, pointsMinimum;

        //BD
        protected Methode methode =  new PatternLock();
        private MethodeManager methodeManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pattern_lock_configuration);

            //mise en place de la bd
            methodeManager = new MethodeManager(getApplicationContext());
            //Récupération du nombre de points et du nombre de points minimum définis par l'admin
            methodeManager.open();
            methode = methodeManager.getMethode(methode);
            nbPointsActuel = methode.getParam1();
            pointsMinimum = methode.getParam2();

            System.out.println("param1: "+ nbPointsActuel + " et param2: " + pointsMinimum);
            System.out.println("param1: "+ nbPointsActuel + " et param2: " + pointsMinimum);
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

            btnSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Modification des paramètres
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setParam(methode, Integer.parseInt(String.valueOf(spinnerNbPoints.getSelectedItem())), Integer.parseInt(String.valueOf(spinnerPointsMin.getSelectedItem())));
                    methodeManager.close();

                    //Changement d'activité
                    Intent authentification = new Intent(PatternLockConfiguration.this, AccueilAdmin.class);
                    startActivity(authentification);
                    finish();
                }
            });

            btnInit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Initialisation du mot de passe
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setPassword(methode, "");
                    methodeManager.close();

                    Toast.makeText(PatternLockConfiguration.this, "Votre mot de passe a été initialisé", Toast.LENGTH_SHORT).show();
                }
            });
        }



        @Override
        protected void onStart(){
            super.onStart();
        }

        @Override
        public void onBackPressed() {
            //Retour à la page d'accueil lorsque l'on clique sur retour
            Intent authentification = new Intent(PatternLockConfiguration.this, AccueilAdmin.class);
            startActivity(authentification);
            finish();
        }




}
