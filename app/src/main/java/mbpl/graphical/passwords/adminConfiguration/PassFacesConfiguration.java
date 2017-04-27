package mbpl.graphical.passwords.adminConfiguration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;

public class PassFacesConfiguration extends AppCompatActivity {


    private Button btnSubmit, btnInit, btnInitEssai;
    private EditText textNbimage;
    private int nbImageActuel, nbImageTemp;

    private int nbImageActuelleTest;

    protected Methode methode =  new Passfaces();
    private MethodeManager methodeManager;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static String MY_PREFS_NAME = "PassFaces";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_faces_configuration);

        // action bar
        setTitle("Passfaces Configuration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methodeManager = new MethodeManager(getApplicationContext());

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        nbImageActuel = prefs.getInt("param1", 4);
        nbImageTemp = nbImageActuel;

        initEditText();
        addListenerOnButton();
    }

    private void initEditText(){

        textNbimage = (EditText) findViewById(R.id.textfield_nbimage);
        textNbimage.setText(nbImageActuel+"", TextView.BufferType.EDITABLE);

        textNbimage.addTextChangedListener(new TextWatcher() {
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
                String text = textNbimage.getText().toString();
                if(!text.isEmpty()) {
                    if (!text.matches("^-?\\d+$")) {
                        textNbimage.setText(nbImageTemp + "", TextView.BufferType.EDITABLE);
                    } else {
                        if (Integer.parseInt(textNbimage.getText().toString()) > Passfaces.nbImageBD) {
                            textNbimage.setText(Passfaces.nbImageBD + "", TextView.BufferType.EDITABLE);
                        } else if(Integer.parseInt(textNbimage.getText().toString()) < 1) {
                            textNbimage.setText(1 + "", TextView.BufferType.EDITABLE);
                        }
                        nbImageTemp = Integer.parseInt(textNbimage.getText().toString());
                    }
                }
            }
        });
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.buttonSubmitPassFace);
        btnInit = (Button) findViewById(R.id.buttonInitPassFacePassword);
        btnInitEssai = (Button) findViewById(R.id.btnInitEssaipf);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textNbimage.getText().toString().isEmpty()){
                    textNbimage.setText(nbImageTemp + "", TextView.BufferType.EDITABLE);
                }

                if (nbImageActuel != Integer.parseInt(textNbimage.getText().toString())) {
                    editor.putInt("param1", Integer.parseInt(textNbimage.getText().toString()));
                    editor.commit();

                    initMotDePasse();
                }

                Intent authentification = new Intent(PassFacesConfiguration.this, Accueil.class);
                startActivity(authentification);
                finish();
            }
        });

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMotDePasse();
            }
        });

        btnInitEssai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTentative();
                Toast.makeText(PassFacesConfiguration.this, "Le nombre de tentative a été réinitialisé", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void initMotDePasse() {
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        methodeManager.setPassword(methode, "");
        methodeManager.close();
        Toast.makeText(PassFacesConfiguration.this, "Votre mot de passe a été réinitialisé", Toast.LENGTH_SHORT).show();

        initTentative();
    }

    private void initTentative() {
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
