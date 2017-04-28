package mbpl.graphical.passwords.androidPatternLock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

import static mbpl.graphical.passwords.utils.Tools.writeToFile;

public class Authentification extends AppCompatActivity {

    private Lock9View lock9View;
    private MethodeManager methodeManager;
    protected Methode methode = new PatternLock();
    protected int nombreEssai, nbPointsMin;

    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification_pattern_lock);

        setTitle("Pattern Lock Authentification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        nombreEssai = prefs.getInt("param3", 0);

        System.out.println("param3 " + nombreEssai + " et " +  prefs.getInt("nbTentative", 0));

        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        nbPointsMin = methode.getParam2();

        lock9View.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password, int nombre,  StringBuilder ok) {

                PATTERN_KEY = prefs.getString("Pattern", "invalid");

                if(PATTERN_KEY.equals("invalid")){
                    Toast.makeText(Authentification.this, "Options --> Create new Pattern", Toast.LENGTH_LONG).show();
                }else{
                    nombreEssai = prefs.getInt("nbTentative", 0);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    if(password.equals(PATTERN_KEY)){
                        writeToFile("Pattern Lock - Succes - Essai restant : "+nombreEssai+" "+formattedDate+"\n");
                        editor.putInt("nbTentative", prefs.getInt("param3", 0));
                        editor.commit();
                        Intent intent = new Intent(Authentification.this, Bienvenue.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if ( nombre >= nbPointsMin) {
                            nombreEssai-- ;
                            editor.putInt("nbTentative", nombreEssai);
                            editor.commit();
                            writeToFile("Pattern Lock - Echec - Essai restants : "+nombreEssai+" "+formattedDate+"\n");
                            if (nombreEssai > 0 ) {
                                Toast.makeText(Authentification.this, "Pattern incorrect !\nNombre d'essais restants : " + nombreEssai, Toast.LENGTH_LONG).show();
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

