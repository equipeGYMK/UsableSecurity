package mbpl.graphical.passwords.adminConfiguration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

public class PatternLockConfiguration extends AppCompatActivity {


    private Spinner spinnerNbPoints, spinnerTentative;
    private Button btnSubmit, btnInit, btnInitEssai;
    private int nbPointsActuel, pointsMinimum, pointsMinimumTemp, tentativeActuelle;
    private EditText textPointsMinimum;

    private ArrayList listIntSpinner;

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
        tentativeActuelle = prefs.getInt("param3", 3);
        methodeManager.close();

        //Création des spinners
        addItemsOnSpinnerNbPoints();
        addItemsOnSpinnerTentative();
        initEditText();
        addListenerOnButton();

    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerNbPoints() {

        spinnerNbPoints = (Spinner) findViewById(R.id.spinnerConfigPatternLockNbPoints);
        List<String> list = new ArrayList<String>();
        listIntSpinner = new ArrayList();
        list.add("3x3");
        listIntSpinner.add(9);
        list.add("4x4");
        listIntSpinner.add(16);
        list.add("5x5");
        listIntSpinner.add(25);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNbPoints.setAdapter(dataAdapter);



        switch (nbPointsActuel) {
            case 9 : {
                spinnerNbPoints.setSelection(0, false);
                break;
            }
            case 16 : {
                spinnerNbPoints.setSelection(1, false);
                break;
            }
            case 25 : {
                spinnerNbPoints.setSelection(2, false);
                break;
            }
        }
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerTentative() {

        spinnerTentative = (Spinner) findViewById(R.id.aPLSpinnerTentative);
        List<String> list = new ArrayList<String>();
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTentative.setAdapter(dataAdapter);
        spinnerTentative.setSelection(tentativeActuelle-2, false);

    }

    private void initEditText(){

        textPointsMinimum = (EditText) findViewById(R.id.editTextpointsMinimum);
        textPointsMinimum.setText(pointsMinimum+"", TextView.BufferType.EDITABLE);

        textPointsMinimum.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = textPointsMinimum.getText().toString();
                if(!text.isEmpty()) {
                    if (!text.matches("^-?\\d+$")) {
                        textPointsMinimum.setText(pointsMinimumTemp + "", TextView.BufferType.EDITABLE);
                    } else {
                        if (Integer.parseInt(textPointsMinimum.getText().toString()) > (int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition())) {
                            textPointsMinimum.setText((int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition()) + "", TextView.BufferType.EDITABLE);
                        } else if(Integer.parseInt(textPointsMinimum.getText().toString()) < 1) {
                            textPointsMinimum.setText(1 + "", TextView.BufferType.EDITABLE);
                        }
                        pointsMinimumTemp = Integer.parseInt(textPointsMinimum.getText().toString());
                    }
                }
            }
        });
    }


    // get the selected dropdown list value
    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.buttonConfigPattrnLock);
        btnInit = (Button) findViewById(R.id.buttonInitPatternLock);
        btnInitEssai = (Button) findViewById(R.id.buttonDebloPatternLock);

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textPointsMinimum.getText().toString().isEmpty()){
                    textPointsMinimum.setText(pointsMinimumTemp + "", TextView.BufferType.EDITABLE);
                }

                if (Integer.parseInt(textPointsMinimum.getText().toString()) > (int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition())) {
                    textPointsMinimum.setText((int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition()) + "", TextView.BufferType.EDITABLE);
                }

                if (nbPointsActuel != (int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition())
                        || pointsMinimum != Integer.parseInt(textPointsMinimum.getText().toString())) {
                    //Modification des paramètres
                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setParam(methode, (int) listIntSpinner.get(spinnerNbPoints.getSelectedItemPosition()), Integer.parseInt(textPointsMinimum.getText().toString()));
                    methodeManager.close();

                    initMotDePasse();
                    initTentative();
                }
                if(tentativeActuelle != (spinnerTentative.getSelectedItemPosition()+2)) {
                    initTentative();
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
                initTentative();
            }
        });

        btnInitEssai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initTentative();
                Toast.makeText(PatternLockConfiguration.this, "Le nombre de tentative a été réinitialisé", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initMotDePasse() {

        initTentative();
        //Initialisation du mot de passe
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        methodeManager.setPassword(methode, "");
        methodeManager.close();
        Toast.makeText(PatternLockConfiguration.this, "Votre mot de passe a été réinitialisé", Toast.LENGTH_SHORT).show();
    }

    private void initTentative() {
        //Initialisation des tentatives
        String tentativeTemp = (String) spinnerTentative.getSelectedItem();

        editor.putInt("param3", Integer.parseInt(tentativeTemp));
        editor.putInt("nbTentative", Integer.parseInt(tentativeTemp));
        editor.commit();
    }



    @Override
    protected void onStart(){
        super.onStart();
    }


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
