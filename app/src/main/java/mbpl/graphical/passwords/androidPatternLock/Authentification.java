package mbpl.graphical.passwords.androidPatternLock;


import mbpl.graphical.passwords.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class Authentification extends AppCompatActivity {

        private Lock9View lock9View;


    //Créer le fichier de préférence dans lequel on stockera les données
    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification_pattern_lock);

        // action bar
        setTitle("Pattern Lock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);

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
                    if(password.equals(PATTERN_KEY)){
                        Intent intent = new Intent(Authentification.this, Bienvenue.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Authentification.this, "Login success!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Authentification.this, "Pattern incorrect!", Toast.LENGTH_SHORT).show();
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

