package mbpl.graphical.passwords.passfaces;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;

import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.sqlite.Passfaces;
import mbpl.graphical.passwords.utils.GenericAuthentification;


/**
 * Created by benja135 on 26/04/16.
 * Activité d'authentification de la méthode "passfaces". OK
 */
public class ApprentissageSansAideCreation extends GenericAuthentification {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.here = ApprentissageSansAideCreation.this;
        this.nextClass = Accueil.class;
        this.nbImage = 20;
        this.methode = new Passfaces();
        super.onCreate(savedInstanceState);

        // action bar
        setTitle(Html.fromHtml("<small>Passfaces Apprentissage sans aide</small>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected int getDrawableN(int n) {
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
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

