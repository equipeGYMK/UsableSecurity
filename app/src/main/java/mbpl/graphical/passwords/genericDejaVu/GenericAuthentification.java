package mbpl.graphical.passwords.genericDejaVu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.androidPatternLock.Authentification;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.utils.Tools;

import static mbpl.graphical.passwords.utils.Tools.writeToFile;

/**
 * Created by benja135 on 05/03/16.
 * Activité d'authentification des méthodes de type "Déjà Vu".
 */
public abstract class GenericAuthentification extends AppCompatActivity {

    // Variables protected à redéfinir dans chaque classe fille /!\
    protected GenericAuthentification here = GenericAuthentification.this;
    protected Class nextClass = Accueil.class;
    protected int nbImage;
    protected Methode methode;
    protected int tailleImage; // optionnel

    private int nbLigne = 6;
    private int nbColonne = 4;
    private MethodeManager methodeManager;

    private List<Integer> trueMotDePasse;
    private List<Integer> inputMotDePasse = new ArrayList<>();

    private SharedPreferences prefs;
    private static String MY_PREFS_NAME = "PassFaces";
    SharedPreferences.Editor editor;

    int nombreEssai;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();


        methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        trueMotDePasse = Tools.stringArrayToIntArray(methode.getMdp());
        int nbImageParPhase = methode.getParam1();
        switch (nbImageParPhase) {
            case 6:
                nbLigne = 3;
                nbColonne = 2;
                break;
            case 9:
                nbLigne = 3;
                nbColonne = 3;
                break;
            case 96:
                nbLigne = 12;
                nbColonne = 8;
                tailleImage = 96;
                break;
            default:
                nbLigne = 6;
                nbColonne = 4;
                break;
        }

        tailleImage = (width*10)/(nbColonne*10);
        methodeManager.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawAndSetListeners(trueMotDePasse.get(0));
    }

    /**
     * Affiche des image_passfaces aléatoirement et ajoute un listener sur chacun d'entre eux.
     * imageToBeDisplayed sera forcément affiché (à une position aléatoire).
     *
     * @param imageToBeDisplayed image à afficher (pour nous le caractére du mdp courant)
     */
    protected void drawAndSetListeners(int imageToBeDisplayed) {

        boolean[] imageAlreadyDisplayed = new boolean[nbImage];
        for (int i = 0; i < nbImage; i++) {
            imageAlreadyDisplayed[i] = false;
        }

        int positionImageToBeDisplayed = Tools.randomInto(0, nbLigne * nbColonne - 1);

        GridLayout gridLayout = new GridLayout(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y - Tools.getStatusBarHeight(getResources()) - Tools.getActionBarHeight(getTheme(),getResources());

        gridLayout.setColumnCount(nbColonne);
        gridLayout.setRowCount(nbLigne);

        for (int l = 0; l < nbLigne; l++) {
            for (int c = 0; c < nbColonne; c++) {

                ImageView imageView;
                imageView = new ImageView(this);

                // Crée un bitmap d'une image piochée aléatoirement (ou pas)
                int numImage;
                if ((l * nbColonne + c) == positionImageToBeDisplayed) {
                    numImage = trueMotDePasse.get(inputMotDePasse.size());
                } else {
                    do {
                        numImage = Tools.randomInto(1, nbImage);
                    } while (imageAlreadyDisplayed[numImage - 1] || numImage == imageToBeDisplayed);
                }
                imageAlreadyDisplayed[numImage - 1] = true;

                Bitmap bmp;
                bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(numImage));
                bmp = Bitmap.createScaledBitmap(bmp, tailleImage, tailleImage, true);

                // On ajoute l'image à l'ImageView
                imageView.setImageBitmap(bmp);

                // Ajoute un listener sur l'image
                final int finalNumImage = numImage;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePhase(finalNumImage);
                    }
                });

                // On ajoute l'ImageView au GridLayout en mettant les bon paramétres
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = screenHeight / nbLigne;
                param.width = screenWidth / nbColonne;
                param.setMargins(0, 0, 0, 0);
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(l);
                imageView.setLayoutParams(param);
                gridLayout.addView(imageView);
            }
        }

        setContentView(gridLayout);
    }


    /**
     * A chaque appui sur un image, cette méthode est appelé.
     * Elle permet de changer de phase, c'est à dire de rappeler la méthode d'affichage
     * avec le prochain image du mot de passe ou de détecter la fin de la saisie et donc
     * de retourner au menu de l'application.
     *
     * @param selectedImage numéro de l'image selectionné lors de la phase
     */
    protected void changePhase(int selectedImage) {
        inputMotDePasse.add(selectedImage);

        if (inputMotDePasse.size() == trueMotDePasse.size()) {

            nombreEssai = prefs.getInt("nbTentative", 0);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            if (inputMotDePasse.equals(trueMotDePasse)) {
                writeToFile("PassFaces - Succes - Essai restant : "+nombreEssai+" "+formattedDate+"\n");
                editor.putInt("nbTentative", 3);
                editor.commit();
                inputMotDePasse.clear();
                Intent accueil = new Intent(here, nextClass);
                startActivity(accueil);
            } else {
                nombreEssai-- ;
                editor.putInt("nbTentative", nombreEssai);
                editor.commit();
                writeToFile("PassFaces - Echec - Essai restant : "+nombreEssai+" "+formattedDate+"\n");
                if (nombreEssai > 0 ) {
                    Toast.makeText(GenericAuthentification.this, "Authentification Echouée !\nNombre essai restant : " + nombreEssai, Toast.LENGTH_LONG).show();
                    inputMotDePasse.clear();
                    drawAndSetListeners(trueMotDePasse.get(inputMotDePasse.size()));
                } else {
                    Toast.makeText(GenericAuthentification.this, "ECHEC !\nVous n'avez plus d'essai restant !\nLa technique est bloquée ", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(here, Accueil.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            drawAndSetListeners(trueMotDePasse.get(inputMotDePasse.size()));
        }
    }


    /**
     * Retourne l'image n de res/drawable.
     *
     * @param n numéro de l'image à récupérer
     * @return id identifiant de l'image
     */
    protected abstract int getDrawableN(int n);


}
