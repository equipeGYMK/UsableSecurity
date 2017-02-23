package mbpl.graphical.passwords.accueil;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.utils.CustomList;

import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;
//classeAccueilAdmind  yak test =)))
public class AccueilAdmin extends Activity {

    ListView lv;
    Context context;
    Button btnRetour;
    TextView titre;

    //Paramètre des différents éléments à afficher
    public static int [] prgmImages={R.drawable.images,R.drawable.images2};
    public static String [] prgmNameList={"PassFaces","Pattern Lock"};
    public static String [] descriptionList = new String[implementedMethods.size()];;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_tab_technics);

        //récupérer la vue et générer le titre
        titre = (TextView)findViewById(R.id.textViewTitre);
        titre.setText("Interface Admin");

        context = this;

        //créer la bd
        MethodeManager methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();

        // On crée les tables en BDD si elles n'existent pas encore
        for (int i = 0; i < implementedMethods.size(); i++) {
            if (!methodeManager.exist(implementedMethods.get(i))) {
                methodeManager.addMethode(implementedMethods.get(i));
            }
        }
        methodeManager.close();

        //Remplir mon tableau de description
        for (int i = 0; i < implementedMethods.size(); i++)
        descriptionList[i] = implementedMethods.get(i).getDescription();


        //Générer la liste avec la classe CustomList
        lv=(ListView) findViewById(R.id.mdpListView);
        lv.setAdapter(new CustomList(lv, this, prgmNameList,prgmImages, descriptionList, "Admin"));

        //Evenement bouton
        btnRetour = (Button) findViewById(R.id.buttonRetourList);
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(AccueilAdmin.this, AccueilAdminUser.class);
                startActivity(authentification);
                finish();
            }
        });

    }


    public void remplirDescription(){

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String description = "Application développée dans le cadre de l'UE TER/PROJET : " +
                "\"Usable Security - Secure Usability\", pour le M1 informatique de l'Université " +
                "Paul Sabatier de Toulouse.\n\nAuteurs : \n - LACHERAY Benjamin\n " +
                "- JEANMOUGIN Pierre\n - LE QUERE Lilian\n - MOUGEOT Matteo\n" +
                "Remerciements à M. PALANQUE Philippe pour son encadrement.";

        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setMessage(description)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setTitle("A propos").create();

        myAlert.show();

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent authentification = new Intent(AccueilAdmin.this, AccueilAdminUser.class);
        startActivity(authentification);
    }
}