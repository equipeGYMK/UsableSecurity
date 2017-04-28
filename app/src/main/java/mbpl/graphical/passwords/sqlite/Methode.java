package mbpl.graphical.passwords.sqlite;

/**
 * Created by benja135 on 26/04/16.
 */
public abstract class Methode {

    protected int id, bruteForce, dictionaryAttack, shoulderSurfing, smudgeAttack, eyeTracking, spyWare, espaceMdp;
    protected int apprentissage, memorisation, temps, satisfaction, nb_tentative_reussie, nb_tentative_echouee;
    protected String nom, categorie, mdp, description, nameSavePref;
    protected Float indiceSecurite, indiceUtilisabilite, temps_auth_moyen;
    protected int param1, param2;
    protected Class creation, authentification, configuration, information;

    /**
     * @return l'id de la méthode 
     */
    public int getId() {
        return id;
    }

     /**
     * @return l'indice de vulnerabilite au BruteForce
     */
    public int getBruteForce() {
        return bruteForce;
    }

     /**
     * @return l'indice de vulnerabilite au à l'attaque par dictionnaire
     */
    public int getDictionaryAttack() {
        return dictionaryAttack;
    }

     /**
     * @return l'indice de vulnerabilite au smudge attack
     */
    public int getSmudgeAttack() {
        return smudgeAttack;
    }

     /**
     * @return la taille du mot de passe courant
     */
    public int getEspaceMdp() {
        return espaceMdp;
    }

     /**
     * @return le temps d'authentification du dernier essai 
     */
    public int getTemps() {
        return temps;
    }

     /**
     * @return le nombre de tentative échoué sur cette méthode
     */
    public int getNb_tentative_echouee() {
        return nb_tentative_echouee;
    }

     /**
     * @return le nom de la méthode
     */
    public String getNom() {
        return nom;
    }

     /**
     * @return la catégorie de la méthode 
     */
    public String getCategorie() {
        return categorie;
    }

     /**
     * @return l'indice de sécurité de la méthode
     */
    public Float getIndiceSecurite() {
        return indiceSecurite;
    }

     /**
     * @return l'indice de d'utilisabilité de la méthode
     */
    public Float getIndiceUtilisabilite() {
        return indiceUtilisabilite;
    }

     /**
     * @return le temps d'authentification moyen de la méthode
     */
    public Float getTemps_auth_moyen() {
        return temps_auth_moyen;
    }

     /**
     * @return le nombre de tentatives réussi de la méthode
     */
    public int getNb_tentative_reussie() {
        return nb_tentative_reussie;
    }

     /**
     * @return l'indice de satisfaction
     */
    public int getSatisfaction() {
        return satisfaction;
    }

     /**
     * @return l'indice de mémorisation
     */
    public int getMemorisation() {
        return memorisation;
    }

     /**
     * @return l'indice d'apprentissage
     */
    public int getApprentissage() {
        return apprentissage;
    }

     /**
     * @return l'indice de vulnerabilite au spyware
     */
    public int getSpyWare() {
        return spyWare;
    }

     /**
     * @return l'indice de vulnerabilite à l'eyetracking
     */
    public int getEyeTracking() {
        return eyeTracking;
    }

     /**
     * @return l'indice de vulnerabilite au shouldersurfing
     */
    public int getShoulderSurfing() {
        return shoulderSurfing;
    }

     /**
     * @return le nom de pref dans lequel sont sauvegardé les données
     */
    public String getNameSavePref() {
        return nameSavePref;
    }

     /**
     * @return defini le nom de pref dans lequel seront sauvegardé les données
     */
    public void setNameSavePref(String nameSavePref) {
        this.nameSavePref = nameSavePref;
    }

     /**
     * @return le mot de passe courant
     */
    public String getMdp() {
        return mdp;
    }

     /**
     * @param id enregistrer l'id
     */
    public void setId(int id) {
        this.id = id;
    }

     /**
     * @param nb_tentative_reussie enregistrement du nombre de tentative réussi
     */
    public void setNb_tentative_reussie(int nb_tentative_reussie) {
        this.nb_tentative_reussie = nb_tentative_reussie;
    }

    /**
     * @param nb_tentative_echouee nombre de tentative echouee a enregistrer
     */
    public void setNb_tentative_echouee(int nb_tentative_echouee) {
        this.nb_tentative_echouee = nb_tentative_echouee;
    }

    /**
     * @param temps_auth_moyen enregistre le temps d'authentification moyen
     */
    public void setTemps_auth_moyen(Float temps_auth_moyen) {
        this.temps_auth_moyen = temps_auth_moyen;
    }

    /**
     * @param motDePasse enregistre le mot de passe
     */
    public void setMdp(String motDePasse) {
        this.mdp = motDePasse;
    }

      
    /**
     * @return le nombre de caractère dans le mot de passe
     */
    
    public int getParam1() {
        return param1;
    }

    /**
     * @param param1 enregistre le nombre de caractère dans le mot de passe 
     */
    public void setParam1(int param1) {
        this.param1 = param1;
    }

    /**
     * @return le nombre de caractère minimum dans le mot de passe
     */
    public int getParam2() {
        return param2;
    }

    /**
     * @param param2 enregistre le nombre de caractère minimum dans le mdp
     */
    public void setParam2(int param2) {
        this.param2 = param2;
    }

    /**
     * @return retourne la classe d'activité de creation
     */
    public Class getCreation() {
        return creation;
    }

    /**
     * @return retourne la classe d'activité d'authentification
     */
    public Class getAuthentification() {
        return authentification;
    }

    /**
     * @return retourne la classe d'activité de configuration
     */
    public Class getConfiguration(){
        return configuration;
    }

    /**
     * @return retourne la classe d'activité d'information
     */
    public Class getInformation() {
        return information;
    }



    /**
     * @return retourne la description de la méthode
     */
    public String getDescription() {
        return description;
    }
}
