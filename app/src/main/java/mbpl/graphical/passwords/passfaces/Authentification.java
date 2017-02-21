package mbpl.graphical.passwords.passfaces;

import android.os.Bundle;

import mbpl.graphical.passwords.genericDejaVu.GenericAuthentification;
import mbpl.graphical.passwords.sqlite.Passfaces;


/**
 * Created by benja135 on 26/04/16.
 * Activité d'authentification de la méthode "passfaces".
 */
public class Authentification extends GenericAuthentification {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.here = Authentification.this;
        this.nextClass = Bienvenue.class;
        this.nbImage = 20;
        this.methode = new Passfaces();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getDrawableN(int n) {
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
    }

}
