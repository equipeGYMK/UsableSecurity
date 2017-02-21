package mbpl.graphical.passwords.passfaces;

import android.os.Bundle;

import mbpl.graphical.passwords.accueil.AccueilUser;
import mbpl.graphical.passwords.genericDejaVu.GenericCreation;
import mbpl.graphical.passwords.sqlite.Passfaces;


/**
 * Created by benja135 on 26/04/16.
 * Activité de création de la méthode "passfaces".
 */
public class Creation extends GenericCreation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.here = Creation.this;
        this.nextClass = ValidationCreation.class;
        this.nbImage = 20;
        this.methode = new Passfaces();
        this.nbColonne = 5;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getDrawableN(int n) {
        return getResources().getIdentifier("visage_" + n, "drawable", getPackageName());
    }

}
