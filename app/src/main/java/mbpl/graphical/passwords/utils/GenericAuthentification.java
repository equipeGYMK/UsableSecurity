package mbpl.graphical.passwords.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.passfaces.ApprentissageAvecAideCreation;
import mbpl.graphical.passwords.passfaces.ApprentissageSansAideCreation;
import mbpl.graphical.passwords.passfaces.ChoixApresEchecApprentissageCreation;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;

import static mbpl.graphical.passwords.utils.Tools.writeToFile;

/**
 * Created by benja135 on 05/03/16.
 * Activité d'authentification des méthodes de type "Déjà Vu".
 */
public abstract class GenericAuthentification extends AppCompatActivity {


    //mettre l'image bmp en global afin de pouvoir l'étendre et l'appliquer dans les listeners
    protected Bitmap bmp, bmpReference;
    protected ImageView imageView;
    protected GridLayout gridLayout;
    protected int idCompteur = 0;
    //animtion
    protected Animation animRotate;
    //Vue de la bonne image actuelle
    protected ImageView currentImage;
    //booléen de vérification animation
    protected boolean falseImagePicked = false;

    // Variables protected à redéfinir dans chaque classe fille /!\
    protected GenericAuthentification here;
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
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        gridLayout = new GridLayout(this);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y - Tools.getStatusBarHeight(getResources()) - Tools.getActionBarHeight(getTheme(),getResources());

        gridLayout.setColumnCount(nbColonne);
        gridLayout.setRowCount(nbLigne);

        for (int l = 0; l < nbLigne; l++) {
            for (int c = 0; c < nbColonne; c++) {

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


                bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(numImage));
                bmp = Bitmap.createScaledBitmap(bmp, tailleImage, tailleImage, true);
                // On ajoute l'image à l'ImageView
                imageView.setImageBitmap(bmp);

                //récupérer l'image actuelle
                if (numImage == trueMotDePasse.get(inputMotDePasse.size()))
                    currentImage = imageView;

                // Ajoute un listener sur l'image
                final int finalNumImage = numImage;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (here instanceof ApprentissageAvecAideCreation)
                        {
                            if (finalNumImage != trueMotDePasse.get(inputMotDePasse.size()))
                            {
                                ImageView l = getImageView(v.getId());
                                bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(finalNumImage));
                                bmp = Bitmap.createScaledBitmap(bmp, tailleImage, tailleImage, true);
                                bmp = bmp.copy(bmp.getConfig(), true);     //lets bmp to be mutable
                                Paint paint = new Paint();
                                paint.setColor(Color.RED);
                                paint.setStrokeWidth(12);
                                Canvas canvas = new Canvas(bmp);
                                canvas.drawLine(0, 0, bmp.getHeight(), bmp.getWidth(), paint);
                                canvas.drawLine(bmp.getHeight(), 0, 0, bmp.getWidth(), paint);
                                l.setImageBitmap(bmp);


                                //Permet de s'assurer que l'animation de se reproduit pas plusieurs fois
                                if(!falseImagePicked) {
                                    //animation de la bonne image et création de labitmap
                                    bmpReference = BitmapFactory.decodeResource(getResources(), getDrawableN(trueMotDePasse.get(inputMotDePasse.size())));
                                    bmpReference = Bitmap.createScaledBitmap(bmpReference, tailleImage, tailleImage, true);
                                    bmpReference = bmpReference.copy(bmpReference.getConfig(), true);     //autoriser la modification
                                    animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.roatate);
                                    currentImage.startAnimation(animRotate);

                                    //mettre le booléen à true afin d'éviter de relancer l'animation plusieurs fois
                                    falseImagePicked = true;
                                }
                            }
                            else
                                changePhase(finalNumImage);

                        }
                        else
                            changePhase(finalNumImage);
                    }
                });

                // On ajoute l'ImageView au GridLayout en mettant les bons paramétres
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = screenHeight / nbLigne;
                param.width = screenWidth / nbColonne;
                param.setMargins(0, 0, 0, 0);
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(l);
                imageView.setLayoutParams(param);
                imageView.setId(idCompteur);
                gridLayout.addView(imageView);

                //incrémenter le compteur et ajouter le couple (id, numImage) au tableau)
                idCompteur++;
            }
        }
        setContentView(gridLayout);
        idCompteur = 0;
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
                writeToFile("PassFaces - Succes - Essais restants : "+nombreEssai+" "+formattedDate+"\n");
                editor.putInt("nbTentative", 3);
                editor.commit();
                inputMotDePasse.clear();
                Intent intent = new Intent(here, nextClass);
                startActivity(intent);
            } else {
                nombreEssai-- ;
                editor.putInt("nbTentative", nombreEssai);
                editor.commit();
                writeToFile("PassFaces - Echec - Essais restants : "+nombreEssai+" "+formattedDate+"\n");
                if (nombreEssai > 0 ) {
                    Toast.makeText(GenericAuthentification.this, "Authentification échouée !\nNombre d'essais restants : " + nombreEssai, Toast.LENGTH_LONG).show();
                    inputMotDePasse.clear();
                    drawAndSetListeners(trueMotDePasse.get(inputMotDePasse.size()));
                } else {
                    if (here instanceof ApprentissageSansAideCreation){
                        Toast.makeText(GenericAuthentification.this, "Vous avez échoué la phase d'apprentissage", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(here, ChoixApresEchecApprentissageCreation.class);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        Toast.makeText(GenericAuthentification.this, "ECHEC !\nVous n'avez plus d'essais restants !\nLa technique est bloquée ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(here, Accueil.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        } else {
            drawAndSetListeners(trueMotDePasse.get(inputMotDePasse.size()));
            falseImagePicked = false;
        }
    }


    /**
     * Retourne l'image n de res/drawable.
     *
     * @param n numéro de l'image à récupérer
     * @return id identifiant de l'image
     */
    protected abstract int getDrawableN(int n);


    /**
     *
     */
    protected  void validiteImageClique(Bitmap bitMap){


        bitMap = bitMap.copy(bitMap.getConfig(), true);     //lets bmp to be mutable
        Canvas canvas = new Canvas(bitMap);                 //draw a canvas in defined bmp
        Paint paint = new Paint();                          //define paint and paint color
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setStrokeWidth(0.5f);
        paint.setAntiAlias(true);                           //smooth edges
        canvas.drawCircle(50, 50, 3, paint);
    }


    public ImageView getImageView(int Id){

        ImageView resultImage = null;
        int childSize = gridLayout.getChildCount();
        for(int k = 0; k < childSize; k++) {
            if( gridLayout.getChildAt(k) instanceof ImageView) {
                if (gridLayout.getChildAt(k).getId() == Id)
                    resultImage = (ImageView) gridLayout.getChildAt(k);
            }
        }
        return resultImage;
    }
}
