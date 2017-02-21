package mbpl.graphical.passwords.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matteo on 07/04/2016.
 * Cette classe permet de créer la base de données
 * ainsi que la table méthode qui contiendra toutes les informations requises.
 */
public class MySQLiteDatabase extends SQLiteOpenHelper {

    private static final String METHODE = "table_methode";
    private static final String COL_ID = "methode_id";
    private static final String COL_NOM = "methode_name";
    private static final String COL_CATEGORIE = "categorie";
    private static final String COL_BRUTEFORCE = "bruteForce";
    private static final String COL_DICTIONARYATTACK = "dictionaryAttack";
    private static final String COL_SHOULDERSURFING = "shoulderSurfing";
    private static final String COL_SMUDGEATTACK = "smudgeAttack";
    private static final String COL_EYETRACKING = "eyeTracking";
    private static final String COL_SPYWARE = "spyWare";
    private static final String COL_INDICESECURITE = "indiceSecurite";
    private static final String COL_APPRENTISSAGE = "apprentissage";
    private static final String COL_MEMORISATION = "memorisation";
    private static final String COL_TEMPS = "temps";
    private static final String COL_SATISFACTION = "satisfaction";
    private static final String COL_INDICEUTILISABILITE = "indiceUtilisabilite";
    private static final String COL_TENTATIVEREUSSIE = "nb_tentative_reussie";
    private static final String COL_TENTATIVEECHOUEE = "nb_tentative_echouee";
    private static final String COL_TEMPSMOYEN = "temps_auth_moyen";
    private static final String COL_ESPACE_MDP = "espaceMdp";
    private static final String COL_MDP = "mdp";
    private static final String COL_PARAM1 = "param1";
    private static final String COL_PARAM2 = "param2";


    public MySQLiteDatabase(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    private static final String CREATE_TABLE_METHODES = "CREATE TABLE "
            + METHODE + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOM + " VARCHAR NOT NULL, "
            + COL_CATEGORIE + " VARCHAR NOT NULL, "
            + COL_BRUTEFORCE + " INTEGER NOT NULL, "
            + COL_DICTIONARYATTACK + " INTEGER NOT NULL, "
            + COL_SHOULDERSURFING + " INTEGER NOT NULL, "
            + COL_SMUDGEATTACK + " INTEGER NOT NULL, "
            + COL_EYETRACKING + " INTEGER NOT NULL, "
            + COL_SPYWARE + " INTEGER NOT NULL, "
            + COL_INDICESECURITE + " FLOAT NOT NULL, "
            + COL_APPRENTISSAGE + " INTEGER NOT NULL, "
            + COL_MEMORISATION + " INTEGER NOT NULL, "
            + COL_TEMPS + " INTEGER NOT NULL, "
            + COL_SATISFACTION + " INTEGER NOT NULL, "
            + COL_INDICEUTILISABILITE + " FLOAT NOT NULL, "
            + COL_TENTATIVEREUSSIE + " INTEGER NOT NULL, "
            + COL_TENTATIVEECHOUEE + " INTEGER NOT NULL, "
            + COL_TEMPSMOYEN + " FLOAT DEFAULT 0, "
            + COL_ESPACE_MDP + " INTEGER NOT NULL, "
            + COL_MDP + " TEXT, "
            + COL_PARAM1 + " INTEGER, "
            + COL_PARAM2 + " INTEGER);";


    /**
     * Cette méthode est appelée lors de la toute première création de la base
     * de données. On crée la table table_contacts dans la BDD.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_METHODES);
    }

    /**
     * On mets à jour la base de donnée.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + METHODE + ";");
        onCreate(db);
    }
}
