package mbpl.graphical.passwords.androidPatternLock;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.attr.data;
import static mbpl.graphical.passwords.utils.Tools.writeToFile;

public class Authentification extends AppCompatActivity {

    private Lock9View lock9View;
    private MethodeManager methodeManager;
    protected Methode methode = new PatternLock();
    protected int nombreEssai,nbPointsMin;


    //Créer le fichier de préférence dans lequel on stockera les données
    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification_pattern_lock);

        // action bar
        setTitle("Pattern Lock Authentification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        lock9View = (Lock9View) findViewById(R.id.lock_9_view);

        //récupérer le contexte de la bdd
        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        nbPointsMin = methode.getParam1();

        lock9View.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password, int nombre,  StringBuilder ok) {

                //retourne la valeur pour la clé donnée qui est "Pattern". Le second paramètre donne une valeur par défaut dans le cas où la référence n'est pas trouvée
                PATTERN_KEY = prefs.getString("Pattern", "invalid");

                //Dans le cas où l'utilisateur n'a pas encore créé de pattern alors la valeur de PATTERN_KEY est invalid et on lui propose de se créer un nouveau pattern
                if(PATTERN_KEY.equals("invalid")){
                    Toast.makeText(Authentification.this, "Options --> Create new Pattern", Toast.LENGTH_LONG).show();

                    //Sinon il a déjà enregistré
                }else{
                    nombreEssai = prefs.getInt("nbTentative", 0);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    if(password.equals(PATTERN_KEY)){
                        writeToFile("Pattern Lock - Succes - Essai restant : "+nombreEssai+" "+formattedDate+"\n");
                        editor.putInt("nbTentative", 3);
                        editor.commit();
                        Intent intent = new Intent(Authentification.this, Bienvenue.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if ( nombre >= nbPointsMin) {
                            nombreEssai-- ;
                            editor.putInt("nbTentative", nombreEssai);
                            editor.commit();
                            writeToFile("Pattern Lock - Echec - Essai restant : "+nombreEssai+" "+formattedDate+"\n");
                            if (nombreEssai > 0 ) {
                                Toast.makeText(Authentification.this, "Pattern incorrect !\nNombre essai restant : " + nombreEssai, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Authentification.this, "ECHEC !\nVous n'avez plus d'essai restant !\nLa technique est bloquée ", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Authentification.this, Accueil.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {

                            Toast.makeText(Authentification.this, "Selectionnez au moins "+ nbPointsMin +" points !", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }
        });
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


}

