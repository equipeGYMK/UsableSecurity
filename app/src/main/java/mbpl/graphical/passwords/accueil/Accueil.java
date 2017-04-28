package mbpl.graphical.passwords.accueil;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.utils.CustomList;

import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;

public class Accueil extends Activity {

    ListView lv;
    Context context;

    public static int [] prgmImages={R.drawable.image_passfaces,R.drawable.image_patternlock};
    public static String [] prgmNameList={"Passfaces","Pattern Lock"};
    public static String [] descriptionList = new String[implementedMethods.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_tab_technics);
        context = this;

        MethodeManager methodeManager = new MethodeManager(getApplicationContext());
        methodeManager.open();

        for (int i = 0; i < implementedMethods.size(); i++) {
            if (!methodeManager.exist(implementedMethods.get(i))) {
                methodeManager.addMethode(implementedMethods.get(i));
            }
        }
        methodeManager.close();

        for (int i = 0; i < implementedMethods.size(); i++)
            descriptionList[i] = implementedMethods.get(i).getDescription();

        lv=(ListView) findViewById(R.id.mdpListView);
        lv.setAdapter(new CustomList(lv, this, prgmNameList,prgmImages, descriptionList,  "User"));

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        lv.setAdapter(new CustomList(lv, this, prgmNameList,prgmImages, descriptionList,  "User"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String description = "Application développée dans le cadre de l'UE PROJET : " +
                "\"Usable Security - Secure Usability\", pour le M1 informatique de l'Université " +
                "Paul Sabatier de Toulouse.";

        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setMessage(description)
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
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

}
