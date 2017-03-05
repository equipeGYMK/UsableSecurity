package mbpl.graphical.passwords.genericDejaVu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.utils.CustomProgress;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;

/**
 * Created by benja135 on 05/03/16.
 * Activité de création des méthodes de type "Déjà Vu".
 * - affiche tout les image_passfaces de maniére ordonné dans une grille
 * - possibilité de scroller
 * - un clique sur une image l'ajoute à la liste des image_passfaces choisies
 * - suppression des image_passfaces de la liste possible
 * - passSize entre 1 et 12
 * - chargement avec un progress circle !
 */
public abstract class GenericCreation extends AppCompatActivity {

    // Variables protected à redéfinir dans chaque classe fille /!\
    protected GenericCreation here;
    protected Class nextClass;
    protected int nbImage;
    protected Methode methode;
    protected int tailleImage = 96; // optionnel
    protected int nbColonne = 10; // optionnel

    private final int minPassSize = 1;
    private final int maxPassSize = 12;
    private MethodeManager methodeManager;

    private GridLayout gridToolbar;
    private ArrayList pass = new ArrayList();
    ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        methodeManager = new MethodeManager(getApplicationContext());
        Loading allTheDisplaying = new Loading();
        allTheDisplaying.execute();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    /**
     * Permet de charger l'affichage tout en affichant un progress circle.
     */
    class Loading extends AsyncTask<Void, Void, GridLayout> {

        /**
         * Avant de commencer le chargement : on affiche le progress circle.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GenericCreation.this);
            progressDialog.setMessage("Chargement...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * Le coeur du chargement : filledGridWithListeners():
         *
         * @param params rien
         * @return gridLayout "chargé", càd avec les image_passfaces et les listeners.
         */
        @Override
        protected GridLayout doInBackground(Void... params) {
            return filledGridWithListeners();
        }

        /**
         * On va ici placer le gridLayout dans notre scrollLayout, puis afficher
         * notre gridToolbar, placer les listeners sur les boutons, et enfin
         * arrêter notre progress circle.
         *
         * @param gridLayout le gridLayout chargé
         */
        @Override
        protected void onPostExecute(GridLayout gridLayout) {
            setContentView(R.layout.deja_vu_creation);
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);
            scrollView.addView(gridLayout);

            gridToolbar = (GridLayout) findViewById(R.id.gridToolbar);
            drawGridToolbar();

            // Listener sur le bouton "DEL"
            Button btnDel = (Button) findViewById(R.id.btnDel);

            btnDel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (pass.size() > 0) {
                        pass.remove(pass.size() - 1);
                        drawGridToolbar();
                    }
                }
            });

            // Listener sur le bouton "Valider"
            Button btnValider = (Button) findViewById(R.id.btnValider);

            btnValider.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (pass.size() > 0) {
                        methodeManager.open();
                        methode = methodeManager.getMethode(methode);
                        methodeManager.setPassword(methode, pass.toString());
                        methodeManager.close();

                        Intent authentification = new Intent(here, nextClass);
                        startActivity(authentification);
                        finish();
                    }
                }
            });

            progressDialog.dismiss();
            Toast.makeText(GenericCreation.this, "Créez votre mot de passe ! :)", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Retourne un gridLayout rempli d'image_passfaces avec les listeners.
     */
    private GridLayout filledGridWithListeners() {

        GridLayout gridImages = new GridLayout(this);

        // On récup la largeur de l'écran
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;

        int nbLigne = (int) Math.ceil((float) nbImage / (float) nbColonne);
        gridImages.setColumnCount(nbColonne);
        gridImages.setRowCount(nbLigne);


        for (int i = 0; i < nbImage; i++) {
            ImageView iv;
            iv = new ImageView(this);

            // Crée un bitmap de l'image i
            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(), getDrawableN(i + 1));
            bmp = Bitmap.createScaledBitmap(bmp, tailleImage, tailleImage, true); // les image_passfaces prennent moins de place en mémoire après cette méthode

            // On ajoute l'image à l'ImageView
            iv.setImageBitmap(bmp);

            // Ajoute un listener sur l'image
            final int numImage = i + 1;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pass.size() < maxPassSize) {
                        pass.add(numImage);
                        drawGridToolbar();
                    }
                }
            });

            // On ajoute l'ImageView au GridLayout en mettant les bon paramétres
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.width = screenWidth / nbColonne;
            param.height = param.width;
            param.setMargins(0, 0, 0, 0);
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(i % nbColonne);
            param.rowSpec = GridLayout.spec(i / nbColonne);
            iv.setLayoutParams(param);
            gridImages.addView(iv);
        }
        return gridImages;
    }


    /**
     * Affiche la toolbar contenant les deux boutons de controle et
     * les image_passfaces choisi pour le mot de passe.
     */
    private void drawGridToolbar() {

        CustomProgress secureProgress = (CustomProgress) findViewById(R.id.customProgressSecure);
        secureProgress.setMaximumPercentage(((float) pass.size()) / ((float) maxPassSize));
        secureProgress.setProgressColor(ContextCompat.getColor(this, R.color.blue_500));
        secureProgress.setProgressBackgroundColor(ContextCompat.getColor(this, R.color.blue_200));
        secureProgress.setText(R.string.security);
        secureProgress.setTextSize(14);
        secureProgress.setTextColor(Color.WHITE);
        secureProgress.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        secureProgress.setPadding(20, 0, 0, 0);
        secureProgress.updateView(true);

        CustomProgress usabilityProgress = (CustomProgress) findViewById(R.id.customProgressUsability);
        usabilityProgress.setMaximumPercentage(1f - ((float) pass.size()) / ((float) maxPassSize));
        usabilityProgress.setProgressColor(ContextCompat.getColor(this, R.color.green_500));
        usabilityProgress.setProgressBackgroundColor(ContextCompat.getColor(this, R.color.green_200));
        usabilityProgress.setText(R.string.usability);
        usabilityProgress.setTextSize(14);
        usabilityProgress.setTextColor(Color.WHITE);
        usabilityProgress.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        usabilityProgress.setPadding(20, 0, 0, 0);
        usabilityProgress.updateView(true);


        Button btnDel = (Button) findViewById(R.id.btnDel);
        Button btnValider = (Button) findViewById(R.id.btnValider);
        gridToolbar.removeAllViews();

        gridToolbar.addView(btnDel);
        if (pass.size() > 0) {
            btnDel.setEnabled(true);
        } else {
            btnDel.setEnabled(false);
        }

        gridToolbar.addView(btnValider);
        if (pass.size() >= minPassSize) {
            btnValider.setEnabled(true);
        } else {
            btnValider.setEnabled(false);
        }

        for (int i = 0; i < pass.size(); i++) {
            ImageView iv;
            iv = new ImageView(this);

            Bitmap bmp;
            bmp = BitmapFactory.decodeResource(getResources(), getDrawableN((int) pass.get(i)));
            bmp = Bitmap.createScaledBitmap(bmp, 96, 96, true);

            iv.setImageBitmap(bmp);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();

            param.height = btnDel.getHeight() - 20;
            param.width = btnDel.getHeight() - 20;

            param.setMargins(0, 10, 0, 0);
            param.setGravity(Gravity.CENTER);

            param.columnSpec = GridLayout.spec(1 + (i % 6));
            param.rowSpec = GridLayout.spec(i / 6);
            iv.setLayoutParams(param);
            gridToolbar.addView(iv);
        }
    }


    /**
     * Retourne l'image n de res/drawable.
     *
     * @param n numéro de l'image à récupérer
     * @return id identifiant de l'image
     */
    protected abstract int getDrawableN(int n);


    @Override
    protected void onRestart() {
        super.onRestart();
        pass.clear();
        drawGridToolbar();
    }

}
