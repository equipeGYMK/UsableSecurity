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

        // action bar
        setTitle("Phase de mémorisation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //récupérer les éléments
        buttonSubmit = (Button) findViewById(R.id.buttonTerminerValidationCreationPassFace);
        buttonNext = (Button) findViewById(R.id.buttonNextValidationCreation);
        buttonPrevious = (Button) findViewById(R.id.buttonPreviousValidationCreation);
        textViewExplication = (TextView) findViewById(R.id.textViewExplicationValidationCreation);
        textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        imageView = (ImageView) findViewById(R.id.imageViewValidation);
        imageView.bringToFront();

        //mettre en place le texte
        textViewExplication.setText("Regardez attentivement cette personne.\n" +
                "À quoi pensez-vous qu'elle ressemble?\n" +
                "Elle vous rappelle quelqu'un?");

        //récupération mot de passe
        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        trueMotDePasse = Tools.stringArrayToIntArray(methode.getMdp());


        for (int i = 0; i< trueMotDePasse.size();i++){
            System.out.println("mot de passe value: " + trueMotDePasse.get(i));
        }


        //Ajout des listeners. Achaque fois que l'on cliquera sur un bouton, on appellera la fonction traitementVerification avec le compteur modifié
        addListenerOnButton();
    }



    public void traitementVerification(){
        //Création du bitmap
        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
        bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
        imageView.setImageBitmap(bmp);


        //si l'image est agrandi et qu'on change d'image, il faut s'assurer que le texte soit le bon
        if (isFullScreen)
            textViewZoom.setText("Cliquez sur l'image pour l'agrandir");

        //Gérer les boutons selon le compteur
        EnableBouton();
    }




    @Override
    protected void onStart() {
        super.onStart();

        //Mettre le texte
        textViewZoom.setText("Cliquez sur l'image pour l'agrandir");
        //Première image au lancement
        Bitmap bmp;
        bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(0)));
        bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);

        // On ajoute l'image à l'ImageView
        imageView = (ImageView) findViewById(R.id.imageViewValidation);
        imageView.setImageBitmap(bmp);

        //laisser les boutons utiles
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


    // get the selected dropdown list value
    public void addListenerOnButton() {

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On incrémente le compteur
                compteur++;
                //appel de la fonction. callback
                traitementVerification();
                isFullScreen = false;
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //On décrémente le compteur
                compteur--;
                //appel de la fonction. callback
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
                    //agrandissement de l'image
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
                    bmp = Bitmap.createScaledBitmap(bmp, 512, 512, true);
                    imageView.setImageBitmap(bmp);
                    isFullScreen = true;

                    //texte
                    textViewZoom.setText("Cliquez sur l'image pour la rétrécir");
                }
                else
                {
                    //agrandissement de l'image
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(compteur - 1)));
                    bmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
                    imageView.setImageBitmap(bmp);
                    isFullScreen = false;

                    //texte
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


    protected int getDrawableN(int n) {
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
    }

}
