package mbpl.graphical.passwords.passfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.GridLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;

public class Presentation extends AppCompatActivity {

    private MethodeManager methodeManager;
    protected Methode methode = new Passfaces();
    private ArrayList pass = new ArrayList();
    private Button boutonContinuer;
    private GridLayout gridPresentation;

    private SharedPreferences prefs;
    private static String MY_PREFS_NAME = "PassFaces";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        // action bar
        setTitle("Pass Face");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        initButton();

        genererMotDePasse();
        afficherMotdePasse();
    }

    private void initButton(){
        boutonContinuer = (Button) findViewById(R.id.button_pf_presentation);
        boutonContinuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(Presentation.this, ValidationCreation.class);
                startActivity(authentification);
            }
        });
    }

    private void genererMotDePasse(){

        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();

        //Générer le mot de passe aléatoirement parmis les 20 images
        int nbImage_mdp = prefs.getInt("param1", 4);
        Random r = new Random();
        ArrayList numDispo =  new ArrayList();
        for (int i = 1; i <= Passfaces.nbImageBD; i++){
            numDispo.add(i);
        }

        for(int i = 0; i < nbImage_mdp ; i++){
            int random_image;
            if(i < nbImage_mdp - 1 ) {
                random_image = r.nextInt(20 - i);
            } else {
                random_image = 0;
            }
            pass.add(numDispo.get(random_image));
            numDispo.remove(numDispo.get(random_image));
        }
        methodeManager.setPassword(methode, pass.toString());
        methodeManager.close();
    }

    private void afficherMotdePasse(){
        gridPresentation = (GridLayout) findViewById(R.id.grid_presentation);

        for (int i = 0; i < pass.size(); i++) {
            ImageView iv;
            iv = new ImageView(this);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(), getDrawableN((int) pass.get(i)));
            bmp = Bitmap.createScaledBitmap(bmp, (width*10)/45, (width*10)/45, true);

            iv.setImageBitmap(bmp);

            android.widget.GridLayout.LayoutParams param = new android.widget.GridLayout.LayoutParams();

            param.setMargins(0, 10, 0, 0);
            param.setGravity(Gravity.LEFT);

            param.columnSpec = android.widget.GridLayout.spec(1 + (i % 4));
            param.rowSpec = android.widget.GridLayout.spec(i / 4);
            iv.setLayoutParams(param);
            gridPresentation.addView(iv);
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

    /**
     * Retourne l'image n de res/drawable.
     *
     * @param n numéro de l'image à récupérer
     * @return id identifiant de l'image
     */
    protected int getDrawableN(int n){
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
    }

}
