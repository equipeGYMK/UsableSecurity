package mbpl.graphical.passwords.sqlite;

import mbpl.graphical.passwords.adminConfiguration.PatternLockConfiguration;
import mbpl.graphical.passwords.androidPatternLock.Creation;
import mbpl.graphical.passwords.androidPatternLock.Authentification;
import mbpl.graphical.passwords.androidPatternLock.Information;

/**
 * Created by Admin on 30/01/2017.
 */

public class PatternLock extends Methode {

    public PatternLock() {
        this.id = 1;
        this.creation = Creation.class;
        this.authentification = Authentification.class;
        this.configuration = PatternLockConfiguration.class;
        this.information = Information.class;

        this.description = "L'utilisateur dispose d’une grille de taille 3x3 et le mot de passe de l’utilisateur est un dessin sur cette grille par une séquence" +
                " de lignes reliant les points. Lors du choix du mot de passe, l’utilisateur choisit sa séquence et pour s’authentifier il doit le redessiner sur l’écran." +
                " Au moins 4 points doivent être choisis";
        this.nom = "Pattern Lock";
        this.categorie = "toucher";
        this.bruteForce = 0;
        this.dictionaryAttack = 2;
        this.shoulderSurfing = 2;
        this.smudgeAttack = 5;
        this.eyeTracking = 3;
        this.spyWare = 3;
        this.espaceMdp = 1;
        this.indiceSecurite = 2.29f;
        this.apprentissage = 2;
        this.memorisation = 3;
        this.temps = 3;
        this.satisfaction = 3;
        this.indiceUtilisabilite = 2.75f;

        this.nb_tentative_echouee = 0;
        this.nb_tentative_reussie = 0;
        this.temps_auth_moyen = 0f;
        this.mdp = "";

        this.param1 = 9;
        this.param2 = 4;
    }
}
