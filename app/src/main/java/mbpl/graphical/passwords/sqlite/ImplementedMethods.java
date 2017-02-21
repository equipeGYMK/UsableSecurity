package mbpl.graphical.passwords.sqlite;

import java.util.ArrayList;
import java.util.List;

import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.Passfaces;
import mbpl.graphical.passwords.sqlite.PatternLock;

/**
 * Created by benja135 on 28/04/16.
 * Facilite l'intégration d'une nouvelle méthode.
 */
public class ImplementedMethods {

    public static List<Methode> implementedMethods = new ArrayList<Methode>();

    static {
        // TODO AJOUTER VOTRE METHODE A CETTE LISTE
        implementedMethods.add(new Passfaces());
        implementedMethods.add(new PatternLock());
    }

    private ImplementedMethods() {}

}
