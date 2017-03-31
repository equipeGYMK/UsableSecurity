package mbpl.graphical.passwords.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by benja135 on 26/04/16.
 */
public class MethodeManager {

    protected static final int VERSION_BDD = 1;
    protected static final String NOM_BDD = "methode.db";

    protected static final String TABLE_NAME = "table_methode";
    protected static final String COL_ID = "methode_id";
    protected static final int NUM_COL_ID = 0;
    protected static final String COL_NOM = "methode_name";
    protected static final int NUM_COL_NOM = 1;
    protected static final String COL_CATEGORIE = "categorie";
    protected static final int NUM_COL_CATEGORIE = 2;
    protected static final String COL_BRUTEFORCE = "bruteForce";
    protected static final int NUM_COL_BRUTEFORCE = 3;
    protected static final String COL_DICTIONARYATTACK = "dictionaryAttack";
    protected static final int NUM_COL_DICTIONARYATTACK = 4;
    protected static final String COL_SHOULDERSURFING = "shoulderSurfing";
    protected static final int NUM_COL_SHOULDERSURFING = 5;
    protected static final String COL_SMUDGEATTACK = "smudgeAttack";
    protected static final int NUM_COL_SMUDGEATTACK = 6;
    protected static final String COL_EYETRACKING = "eyeTracking";
    protected static final int NUM_COL_EYETRACKING = 7;
    protected static final String COL_SPYWARE = "spyWare";
    protected static final int NUM_COL_SPYWARE = 8;
    protected static final String COL_INDICESECURITE = "indiceSecurite";
    protected static final int NUM_COL_INDICESECURITE = 9;
    protected static final String COL_APPRENTISSAGE = "apprentissage";
    protected static final int NUM_COL_APPRENTISSAGE = 10;
    protected static final String COL_MEMORISATION = "memorisation";
    protected static final int NUM_COL_MEMORISATION = 11;
    protected static final String COL_TEMPS = "temps";
    protected static final int NUM_COL_TEMPS = 12;
    protected static final String COL_SATISFACTION = "satisfaction";
    protected static final int NUM_COL_SATISFACION = 13;
    protected static final String COL_INDICEUTILISABILITE = "indiceUtilisabilite";
    protected static final int NUM_COL_INDICEUTILISABILITE = 14;
    protected static final String COL_TENTATIVEREUSSIE = "nb_tentative_reussie";
    protected static final int NUM_COL_TENTATIVEREUSSIE = 15;
    protected static final String COL_TENTATIVEECHOUEE = "nb_tentative_echouee";
    protected static final int NUM_COL_TENTATIVEECHOUEE = 16;
    protected static final String COL_TEMPSMOYEN = "temps_auth_moyen";
    protected static final int NUM_COL_TEMPSMOYEN = 17;
    protected static final String COL_ESPACE_MDP = "espaceMdp";
    protected static final int NUM_COL_ESPACE_MDP = 18;
    protected static final String COL_MDP = "mdp";
    protected static final int NUM_COL_MDP = 19;
    protected static final String COL_PARAM1 = "param1";
    protected static final int NUM_COL_PARAM1 = 20;
    protected static final String COL_PARAM2 = "param2";
    protected static final int NUM_COL_PARAM2 = 21;
    protected static final String COL_TENTATIVE = "nb_tentative";
    protected static final int NUM_COL_TENTATIVE = 22;

    protected SQLiteDatabase db;
    protected MySQLiteDatabase maBaseSQLite;


    public MethodeManager(Context context) {
        maBaseSQLite = new MySQLiteDatabase(context, NOM_BDD, null, VERSION_BDD);
    }

    /**
     * Retourne la méthode depuis la bdd.
     *
     * @return la méthode
     */
    public Methode getMethode(Methode methode) {

        int id = methode.getId();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + "=" + id, null);

        if (c.moveToFirst()) {
            methode.setNb_tentative_echouee(c.getInt(NUM_COL_TENTATIVEECHOUEE));
            methode.setNb_tentative_reussie(c.getInt(NUM_COL_TENTATIVEREUSSIE));
            methode.setTemps_auth_moyen(c.getFloat(NUM_COL_TEMPSMOYEN));
            methode.setMdp(c.getString(NUM_COL_MDP));
            methode.setParam1(c.getInt(NUM_COL_PARAM1));
            methode.setParam2(c.getInt(NUM_COL_PARAM2));
            c.close();
        }

        return methode;
    }

    /**
     * Retourne vrai si une méthode est dans la base de donnée, faux le contraire.
     *
     * @return boolean.
     */
    public boolean exist(Methode methode) {

        int id = methode.getId();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + "=" + id, null);

        return (c.getCount() > 0);
    }

    /**
     * On ouvre la table en lecture/écriture.
     */
    public void open() {
        db = maBaseSQLite.getWritableDatabase();
    }

    /**
     * On ferme l'accès à la BDD.
     */
    public void close() {
        db.close();
    }

    /**
     * Ajout d'une méthode dans la base.
     *
     * @param methode
     * @return retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
     */
    public long addMethode(Methode methode) {

        ContentValues values = new ContentValues();
        values.put(COL_ID, methode.getId());
        values.put(COL_NOM, methode.getNom());
        values.put(COL_CATEGORIE, methode.getCategorie());
        values.put(COL_BRUTEFORCE, methode.getBruteForce());
        values.put(COL_DICTIONARYATTACK, methode.getDictionaryAttack());
        values.put(COL_SHOULDERSURFING, methode.getShoulderSurfing());
        values.put(COL_SMUDGEATTACK, methode.getSmudgeAttack());
        values.put(COL_EYETRACKING, methode.getEyeTracking());
        values.put(COL_SPYWARE, methode.getSpyWare());
        values.put(COL_INDICESECURITE, methode.getIndiceSecurite());
        values.put(COL_APPRENTISSAGE, methode.getApprentissage());
        values.put(COL_MEMORISATION, methode.getMemorisation());
        values.put(COL_TEMPS, methode.getTemps());
        values.put(COL_SATISFACTION, methode.getSatisfaction());
        values.put(COL_INDICEUTILISABILITE, methode.getIndiceUtilisabilite());
        values.put(COL_TENTATIVEREUSSIE, methode.getNb_tentative_reussie());
        values.put(COL_TENTATIVEECHOUEE, methode.getNb_tentative_echouee());
        values.put(COL_TEMPSMOYEN, methode.getTemps_auth_moyen());
        values.put(COL_ESPACE_MDP, methode.getEspaceMdp());
        values.put(COL_MDP, methode.getMdp());
        values.put(COL_PARAM1, methode.getParam1());
        values.put(COL_PARAM2, methode.getParam2());

        return db.insertWithOnConflict(TABLE_NAME, null,
                values, SQLiteDatabase.CONFLICT_FAIL);

    }

    /**
     * Supprime la méthode de la base de donnée
     *
     * @param methode
     * @return le nombre de lignes supprimées
     */
    public int removeMethode(Methode methode) {
        long id = methode.getId();
        return db.delete(TABLE_NAME, COL_ID + " = " + id, null);
    }

    /**
     * Utiliser de préférence la méthode removeMethode(Methode methode).
     *
     * @param id
     * @return le nombre de lignes supprimées
     */
    public int removeMethode(int id) {
        return db.delete(TABLE_NAME, COL_ID + " = " + id, null);
    }


    /**
     * Met à jour la Methode passé en paramètre pour les tentatives
     * et l'authentification moyen dans la bdd.
     *
     * @param methode
     * @param tentative_echouee
     * @param tentative_reussi
     * @param auth_moyen
     * @return le nombre de lignes updated
     */
    public int setStats(Methode methode, int tentative_echouee, int tentative_reussi, float auth_moyen) {
        int id = methode.getId();
        ContentValues values = new ContentValues();
        values.put(COL_TENTATIVEECHOUEE, tentative_echouee);
        values.put(COL_TENTATIVEREUSSIE, tentative_reussi);
        values.put(COL_TEMPSMOYEN, auth_moyen);
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }


    /**
     * Met à jour le nombre de tentatives réussies en additionnant avec ce qu'il y avait avant
     * et met à jour le temps moyen.
     *
     * @param methode
     * @return le nombre de lignes updated
     */
    public int addTentativeReussie(Methode methode) {
        int id = methode.getId();
        int newTentativeReussi = methode.getNb_tentative_reussie() + 1;
       /* float newAuthenMoyen = (methode.getTemps_auth_moyen() * (float) methode.getNb_tentative_reussie()
                + auth_moyen) / (float) newTentativeReussi;*/
        ContentValues values = new ContentValues();
        values.put(COL_TENTATIVEREUSSIE, newTentativeReussi);
       /* values.put(COL_TEMPSMOYEN, newAuthenMoyen);*/
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }

    /**
     * Met à jour le nombre de tentatives échouée en additionnant avec ce qu'il y avait avant.
     *
     * @param methode
     * @return le nombre de lignes updated
     */
    public int addTentativeEchouee(Methode methode) {
        int id = methode.getId();
        int newTentativeEchouee = methode.getNb_tentative_echouee() + 1;
        ContentValues values = new ContentValues();
        values.put(COL_TENTATIVEECHOUEE, newTentativeEchouee);
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }

    /**
     * Met à jour la méthode passé en paramètre pour les configurations dans la bdd.
     *
     * @param methode
     * @param nbIcone
     * @param doublon
     * @return
     */
    public int updateConfiguration(Methode methode, int nbIcone, boolean doublon) {
        int id = methode.getId();
        ContentValues values = new ContentValues();
        values.put(COL_PARAM1, nbIcone);
        values.put(COL_PARAM2, doublon);
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }

    /**
     * Met a jour le mot de passe dans la bdd.
     *
     * @param methode
     * @param str
     * @return
     */
    public int setPassword(Methode methode, String str) {
        int id = methode.getId();
        ContentValues values = new ContentValues();
        values.put(COL_MDP, str);
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }

    /**
     * Retourne vrai si le mot de passe n'a pas été changé.
     *
     * @param methode
     * @return boolean
     */
    public boolean defaultPassword(Methode methode) {
        return methode.getMdp().compareTo("") == 0;
    }



    public int setParam(Methode methode, int param1, int param2){

        int id = methode.getId();
        ContentValues values = new ContentValues();
        values.put(COL_PARAM1, param1);
        values.put(COL_PARAM2, param2);
        return db.update(TABLE_NAME, values, COL_ID + " = " + id, null);
    }


    /**
     * Retourne true si la méthode contient des doublons.
     *
     * @param methode
     * @return
     */
    public int doublon(Methode methode) {
        return methode.getParam2();
    }

    /**
     * Sélection de tous les enregistrements de la table
     *
     * @return cursor
     */
    public Cursor getCursorMethode() {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
