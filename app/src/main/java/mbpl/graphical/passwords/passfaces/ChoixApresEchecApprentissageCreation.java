package mbpl.graphical.passwords.passfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import mbpl.graphical.passwords.R;

public class ChoixApresEchecApprentissageCreation extends AppCompatActivity {

    private Button btnNouveau, btnApprentissage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_apres_echec_apprentissage_creation);

        //Récupérer les vues
        btnNouveau = (Button) findViewById(R.id.genererNouveau);
        btnApprentissage = (Button) findViewById(R.id.accederMemorisation);

        // action bar
        setTitle("Choix PassFace");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNouveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(ChoixApresEchecApprentissageCreation.this, Presentation.class);
                startActivity(authentification);
            }
        });

        btnApprentissage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authentification = new Intent(ChoixApresEchecApprentissageCreation.this, MemorisationCreation.class);
                startActivity(authentification);
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

}
