package mbpl.graphical.passwords.passfaces;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;
import mbpl.graphical.passwords.utils.Tools;

public class MemorisationCreation extends AppCompatActivity {

    private MethodeManager methodeManager;
    private List<Integer> trueMotDePasse;
    protected Methode methode = new Passfaces();
    Button buttonSubmit, buttonNext, buttonPrevious;
    ImageView imageView;
    private TextView textViewExplication, textViewZoom;
    int compteur = 1;
    boolean isFullScreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_creation);

        setTitle("Passfaces Mémorisation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSubmit = (Button) findViewById(R.id.buttonTerminerValidationCreationPassFace);
        buttonNext = (Button) findViewById(R.id.buttonNextValidationCreation);
        buttonPrevious = (Button) findViewById(R.id.buttonPreviousValidationCreation);
        textViewExplication = (TextView) findViewById(R.id.textViewExplicationValidationCreation);
        textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        imageView = (ImageView) findViewById(R.id.imageViewValidation);
        imageView.bringToFront();

        textViewExplication.setText("Regardez attentivement cette personne.\n" +
                "À qui pensez-vous qu'elle ressemble?\n" +
                "Elle vous rappelle quelqu'un?");

        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        trueMotDePasse = Tools.stringArrayToIntArray(methode.getMdp());

        addListenerOnButton();
    }

    /**
     * Modifie l'image et verifie les changement qui doivent être apportés
     */
    public void traitementVerification(){
        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
        bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
        imageView.setImageBitmap(bmp);

        if (isFullScreen)
            textViewZoom.setText("Cliquez sur l'image pour l'agrandir");

        EnableBouton();
    }

    @Override
    protected void onStart() {
        super.onStart();

        textViewZoom.setText("Cliquez sur l'image pour l'agrandir");
        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(0)));
        bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);

        imageView = (ImageView) findViewById(R.id.imageViewValidation);
        imageView.setImageBitmap(bmp);

        buttonNext.setEnabled(true);
        buttonPrevious.setEnabled(false);
    }

    public void EnableBouton(){

        if (compteur == 1) {
            buttonNext.setEnabled(true);
            buttonPrevious.setEnabled(false);
        }
        else if (compteur == trueMotDePasse.size()) {
            buttonNext.setEnabled(false);
            buttonPrevious.setEnabled(true);
        }
        else if ((compteur > 1) || (compteur < trueMotDePasse.size())){
            buttonNext.setEnabled(true);
            buttonPrevious.setEnabled(true);
        }
    }


    /**
     * Initialise les listeners des boutons de l'interface
     * Initialise le listener sur l'imageView
     */
    public void addListenerOnButton() {

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compteur++;
                traitementVerification();
                isFullScreen = false;
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compteur--;
                traitementVerification();
                isFullScreen = false;
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemorisationCreation.this, ApprentissageAvecAideCreation.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                if (!isFullScreen)
                {
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
                    bmp = Bitmap.createScaledBitmap(bmp, 512, 512, true);
                    imageView.setImageBitmap(bmp);
                    isFullScreen = true;

                    textViewZoom.setText("Cliquez sur l'image pour la rétrécir");
                }
                else
                {
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
                    bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
                    imageView.setImageBitmap(bmp);
                    isFullScreen = false;

                    textViewZoom.setText("Cliquez sur l'image pour l'agrandir");
                }
            }
        });
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
    protected int getDrawableN(int n) {
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
    }

}
