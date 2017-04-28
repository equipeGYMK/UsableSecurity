package mbpl.graphical.passwords.androidPatternLock;

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

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

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

    private MethodeManager methodeManager;
    protected Methode methode = new PatternLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_pattern_lock);
        evenementButton();

        setTitle("Pattern Lock Création");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        pointsMinimum = methode.getParam2();
        methodeManager.close();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        tvMsg = (TextView)findViewById(R.id.tvMsg);
        tvMsg.setText(getResources().getString(R.string.draw_pattern_msg));

        enterPatternContainer = (FrameLayout) findViewById(R.id.enterPattern);
        confirmPatternContainer = (FrameLayout) findViewById(R.id.confirmPattern);

        lockViewFirstTry = (Lock9View) findViewById(R.id.lock_viewFirstTry);
        lockViewConfirm =  (Lock9View) findViewById(R.id.lock_viewConfirm);
        confirmPatternContainer.setVisibility(View.GONE);
        lockViewFirstTry.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password, int nombre, StringBuilder ok) {
                if (nombre >= pointsMinimum) {
                    PATTERN_KEY = password;
                    enterPatternContainer.setVisibility(View.GONE);
                    tvMsg.setText(getResources().getString(R.string.redraw_confirm_pattern_msg));
                    confirmPatternContainer.setVisibility(View.VISIBLE);
                    btnRecommencer.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Votre mot de passe doit contenir au minimum " + pointsMinimum +" points. Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
                }

            }

        });


        lockViewConfirm.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password, int nombre,  StringBuilder ok) {
                if(password.equals(PATTERN_KEY)){

                    methodeManager.open();
                    methode = methodeManager.getMethode(methode);
                    methodeManager.setPassword(methode, "PATTERN_KEY");
                    methodeManager.close();

                    Toast.makeText(getApplicationContext(), "Le mot de passe a été créé avec succès.", Toast.LENGTH_SHORT).show();
                    editor.putString("Pattern", password);
                    editor.putInt("nbTentative", prefs.getInt("param3", 3));
                    editor.commit();

                    Intent intent = new Intent(Creation.this, Accueil.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Vous avez dessiné le mauvais chemin.\n Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Initialise le listener du bouton Recommencer
     */
    public void evenementButton(){
        btnRecommencer = (Button) findViewById(R.id.buttonPatternLockCreationRecommencer);
        btnRecommencer.setVisibility(View.GONE);

        btnRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(Creation.this, Creation.class);
                startActivity(authentification);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent authentification;
        if (btnRecommencer.isShown()) {
            authentification = new Intent(Creation.this, Creation.class);
            startActivity(authentification);
            finish();
        }
        else
        {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
