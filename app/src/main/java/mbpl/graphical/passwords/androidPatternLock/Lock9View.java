package mbpl.graphical.passwords.androidPatternLock;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.PatternLock;

public class Lock9View extends ViewGroup {

    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;

    private List<Pair<NodeView, NodeView>> lineList;
    private NodeView currentNode;

    private StringBuilder pwdSb;
    private CallBack callBack;

    private Drawable nodeSrc;
    private Drawable nodeOnSrc;
    private Context context;

    private AttributeSet attrs;
    int defStyleAttr;

    //Bd permettant de récupérer les paramètres de configuration
    private Methode methode =  new PatternLock();
    private MethodeManager methodeManager;

    private int nbPoints;
    private int compteurPoints = 0;
    private boolean verifPointsValide = false;
    private int compteur, nbPointSelec, valuePoints;
    private String diagType;


    public Lock9View(Context context) {
        this(context, null);
        this.context = context;
    }

    public Lock9View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public Lock9View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        this.context = context;
    }

    public Lock9View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr); // TODO api 21
        this.context = context;
        initFromAttributes(attrs, defStyleAttr);
    }

    public void initFromAttributes(AttributeSet attrs, int defStyleAttr) {
        // Je récupère mon tableau d'attributs depuis le paramètre que m'a donné le constructeur
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock9View, defStyleAttr, 0);


        //mise en place de la bd
        methodeManager = new MethodeManager(context);
        //Récupération du nombre de points et du nombre de points minimum définis par l'admin
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        nbPoints = methode.getParam1();
        methodeManager.close();


        //Recuperer les informations du TypedArray
        nodeSrc = a.getDrawable(R.styleable.Lock9View_nodeSrc);
        nodeOnSrc = a.getDrawable(R.styleable.Lock9View_nodeOnSrc);
        int lineColor = Color.argb(0, 0, 0, 0);
        lineColor = a.getColor(R.styleable.Lock9View_lineColor, lineColor);
        float lineWidth = 20.0f;
        lineWidth = a.getDimension(R.styleable.Lock9View_lineWidth, lineWidth);

        // Recycles the TypedArray, to be re-used by a later caller. After calling this function you must not ever touch the typed array again.
        a.recycle();


        //Notre attribut nous permettra de dessiner le trait ainsi que de définir sa couleur, sa taille.
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(lineColor);
        paint.setAntiAlias(true);


        //DisplyaMetrics permet de récupérer la hauteur et la largeur de l'écran
        DisplayMetrics dm = getResources().getDisplayMetrics();
        //Création d'un bitmap ayant les dimensions de l'écran. Config.ARGB_8888 est une norme pour la génération de couleur
        bitmap = Bitmap.createBitmap(dm.widthPixels, dm.widthPixels, Bitmap.Config.ARGB_8888);


        //Ici nous créons notre canvas dans laquelle nous allons donner une bitmap en paramètre. On pourra dessiner dans le bitmap à partir du canvas.
        canvas = new Canvas();
        canvas.setBitmap(bitmap);


        //Nous allons créer n+1 noeud
        for (int n = 0; n < nbPoints; n++) {
            //Classe noeud permettant de créer le noeud étendant l'objet view
            NodeView node = new NodeView(getContext(), n + 1);
            //Lock9View étend une viewgroup donc on pourra lui ajouter plusieurs. Ce sera un container comportant plusieurs vues
            addView(node);
        }
        //Création d'un tableau de pair contenant toutes les vues. Cela permettra de dessiner un trait d'une vue à une autre(une vue représentant un noeud).
        lineList = new ArrayList<Pair<NodeView,NodeView>>();
        //Permet de concaténer facilement les chaînes de caractère avec append
        pwdSb = new StringBuilder();

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //Si le layout n'a pas changé, on ne fait rien
        if (!changed) {
            return;
        }
        //Dans le cas contraire, On redessine en fonction des nouvelles dimensions
        //nbPoints
        int pointsUtils = getPointsUtils(nbPoints);
        //On récupère la hauteur et la largeur du canvas
        int largeurCanvas = right - left;
        int nodeWidth = largeurCanvas /pointsUtils;
        //Espace entre chaque noeud
        int nodePadding = nodeWidth / 8;


        //Création des n noeuds
        for (int n = 0; n < nbPoints; n++) {
            //Pour chaque noeud des enfants du groupview  lock9view, on va la positionner dans la canvas
            NodeView node = (NodeView) getChildAt(n);
            //Récupération du nombres de lignes et de colonnes en fonction du nombre de points
            int row = n / pointsUtils;
            int col = n % pointsUtils;

            //Enfin on dessine le noeud selon ses points left, top, right et bot en prenant en compte le décalage(padding)
            int l = col * nodeWidth + nodePadding;
            int t = row * nodeWidth + nodePadding;
            int r = col * nodeWidth + nodeWidth - nodePadding;
            int b = row * nodeWidth + nodeWidth - nodePadding;
            node.layout(l, t, r, b);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            //doigt appuyé
            case MotionEvent.ACTION_MOVE:
                //Permet de recupérer le noeud sur lequel le toucher a été effectué
                NodeView nodeAt = getNodeAt(event.getX(), event.getY());
                //si l'on a touché aucun noeud alors on termine en retournant true
                if (nodeAt == null && currentNode == null) {
                    return true;
                } else {
                    //On commence par intialiser la liste
                    clearScreenAndDrawList();
                    //Si il n'y a pas de current node alors on n'a qu'un seul point sinon currentNode représente l'ancien point sélectionné durant le chemin
                    if (currentNode == null) {
                        currentNode = nodeAt;
                        currentNode.setHighLighted(true);
                        pwdSb.append(currentNode.getNum());
                        compteurPoints++;
                    }
                    //Si le point sélectionné n'existe pas ou si le point a déjà été sélectionné alors on ne fait rien
                    else if (nodeAt == null || nodeAt.isHighLighted()) {
                        canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), event.getX(), event.getY(), paint);
                    } else {
                        //Cas où l'on a deux points
                        //ajout de l'algorithme pour s'assurer que l'on a visité tous les points entre le point de départ et d'arrivé.
                        verifPointsValide = verificationPoints(currentNode, nodeAt);

                        //si le point est valide
                        if (verifPointsValide) {
                            //changement de noeud
                            currentNode = nodeAt;
                            compteurPoints += nbPointSelec;
                        }
                        //sinon on dessine seulement le trait actuel
                        else
                            canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), event.getX(), event.getY(), paint);
                    }

                    invalidate();
                }
                return true;
            //relevé le doigt
            case MotionEvent.ACTION_UP:


                if (pwdSb.length() <= 0) {
                    return super.onTouchEvent(event);
                }

                if (callBack != null) {
                    callBack.onFinish(pwdSb.toString(), compteurPoints, pwdSb);
                    pwdSb.setLength(0);
                    compteurPoints = 0;
                }

                currentNode = null;
                lineList.clear();
                clearScreenAndDrawList();

                for (int n = 0; n < getChildCount(); n++) {
                    NodeView node = (NodeView) getChildAt(n);
                    node.setHighLighted(false);
                }

                //Mets à jour l'écran
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        for (Pair<NodeView, NodeView> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
    }


    private NodeView getNodeAt(float x, float y) {
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            if (!(x >= node.getLeft() && x < node.getRight())) {
                continue;
            }
            if (!(y >= node.getTop() && y < node.getBottom())) {
                continue;
            }
            return node;
        }
        return null;
    }

    private NodeView getNodeWithPosition(int position) {

        NodeView node = (NodeView) getChildAt(position - 1);
        return node;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    int getPointsUtils(int points){
        int result = 0;
        switch (points){

            case 9:
            {
                result = 3;
                break;
            }
            case 16:
            {
                result = 4;
                break;
            }
            case 25:
            {
                result = 5;
                break;
            }
            default:
                //nothing
        }
        return result;
    }


    public boolean verificationPoints(NodeView currentNode, NodeView nodeAt){

        String result;
        int i;
        Pair<NodeView, NodeView> pair;
        //récupérer le bon modulo selon la taille du sss
        int moduloNbPoints = getPointsUtils(nbPoints);

        //Récupérer la colonne de départ et d'arrivée
        int pointDepCol = currentNode.getNum() % moduloNbPoints;
        int pointArriveCol = nodeAt.getNum() % moduloNbPoints;
        //Récupérer la ligne de départ et d'arrivée
        int pointDepLig = (currentNode.getNum() - 1) / moduloNbPoints;
        int pointArriveLig = (nodeAt.getNum() - 1) / moduloNbPoints;

        //position points
        int curPos = currentNode.getNum();
        int aTPos = nodeAt.getNum();
        //Noeud intermédiaires
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;


        if (curPos > aTPos)
        {
            result =  getValueTraitementSup(curPos, aTPos, moduloNbPoints, pointDepCol, pointDepLig, pointArriveCol, pointArriveLig);
          //  System.out.println("result: " + result + " et le diagType: " + diagType + " et le compteur: " +compteur);
            System.out.println("QUe vaut result:" + result);
            switch  (result){
                case "horizontal":{
                    //fonction de traitement
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                case "vertical":{
                    //fonction de traitement
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                case "diagonal":{
                    if (diagType.equals("plus"))
                        valuePoints = moduloNbPoints - 1;
                    else
                        valuePoints = moduloNbPoints + 1;

                    //fonction de traitement
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                default:{
                    //dessiner le point et rendre le point highlited
                    canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                    currentNode.setHighLighted(true);
                    nodeAt.setHighLighted(true);
                    //Ajouter les bonnes valeurs au tableau
                    pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                    lineList.add(pair);
                    //incrémenter le nombre de points
                    nbPointSelec++;
                    //Ajout du noeud au mot de passe
                    pwdSb.append(currentNode.getNum());
                }
            }

            return true;

        }
        else if (curPos < aTPos)
        {
            result =  getValueTraitementInf(curPos, aTPos, moduloNbPoints, pointDepCol, pointDepLig, pointArriveCol, pointArriveLig);
            switch  (result){
                case "horizontal":{
                    //A cet instant on sait qu'aucun point n'a déjà été ajouté donc on peut ajouter tout le chemin et on va écrire le chemin pour chaque point
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                case "vertical":{
                    //A cet instant on sait qu'aucun point n'a déjà été ajouté donc on peut ajouter tout le chemin et on va écrire le chemin pour chaque point
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                case "diagonal":{
                    if (diagType.equals("plus"))
                        valuePoints = moduloNbPoints + 1;
                    else
                        valuePoints = moduloNbPoints - 1;

                    //A cet instant on sait qu'aucun point n'a déjà été ajouté donc on peut ajouter tout le chemin et on va écrire le chemin pour chaque point
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                default:{
                    //dessiner le point et rendre le point highlited
                    canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                    currentNode.setHighLighted(true);
                    nodeAt.setHighLighted(true);
                    //Ajouter les bonnes valeurs au tableau
                    pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                    lineList.add(pair);
                    //incrémenter le nombre de points
                    nbPointSelec++;
                    //Ajout du noeud au mot de passe
                    pwdSb.append(currentNode.getNum());
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Fonction lorsque le point de départ est supérieur au point d'arrivée
     * @param depPoint
     * @param finPoint
     * @param intervalle:  représente le nombre de points. Pour une grille 9x9, on aura un intervalle de 3. Une grille 16x16 aura un intervalle de 4. etc..
     * @return
     */
    public String getValueTraitementSup(int depPoint, int finPoint, int intervalle,  int colDep, int ligDep, int colArr, int ligArr)
    {
        int depPointTemp = depPoint;
        int finPointTemp = finPoint;


        //cas horizontal
        //On s'arrête lorsque les deux points sont égaux ou bien si notre compteur est égal à l'intervalle pour ne pas aller trop loin
        compteur = 1;
        if (ligDep == ligArr) {
            while (compteur < intervalle) {
                depPointTemp = depPointTemp - 1;
                if (depPointTemp == finPointTemp) {
                    valuePoints = 1;
                    return "horizontal";
                }
                compteur++;
            }
        }

        //cas vertical
        //Récupérer les bonnes valeurs. Etant donnée que l'on travaille verticalement, on incrémentera notre compteur de 3, 4 ou 5 selon la taille de notre grille.
        compteur = intervalle;
        depPointTemp = depPoint;
        if (colArr == colDep) {
            while (depPointTemp > finPointTemp) {
                depPointTemp -= intervalle;
                if (depPointTemp == finPointTemp){
                    valuePoints = intervalle;
                    return "vertical";
                }
                compteur += intervalle;
            }
        }



        //récuprer les colonnes
        if (colDep == 0)
            colDep = intervalle;

        if (colArr == 0)
            colArr = intervalle;

        System.out.println("La colonne de début est: " + colDep);
        System.out.println("La colonne de fin est: " + colArr);

        //On s'assure que la colonne du point d'arrivée est supérieure à la colonne du point de départ
        if (colArr > colDep) {
            //Cas diagonale
            //Récupérer les bonnes valeurs. Etant donnée que l'on travaille diagonalement, on incrémentera notre compteur de 3, 4 ou 5  + 1 selon la taille de notre grille.
            compteur = intervalle - 1;
            depPointTemp = depPoint;
            while (depPointTemp > finPointTemp) {
                System.out.println("On est arrivéSupDiag");
                System.out.println("test: " + depPointTemp);
                depPointTemp -= intervalle - 1;
                if (depPointTemp == finPointTemp) {
                    diagType = "plus";
                    return "diagonal";
                }
                compteur += intervalle - 1;
            }
        }
        else if (colDep > colArr){
            //Cas diagonale
            //Récupérer les bonnes valeurs. Etant donnée que l'on travaille diagonalement, on incrémentera notre compteur de 3, 4 ou 5  + 1 selon la taille de notre grille.
            compteur = intervalle + 1;
            depPointTemp = depPoint;
            while (depPointTemp > finPointTemp)
            {
                System.out.println("On est arrivéDiagInf");
                System.out.println("test: " + depPointTemp);
                depPointTemp -= intervalle + 1;
                if (depPointTemp == finPointTemp) {
                    diagType = "moins";
                    return "diagonal";
                }
                compteur += intervalle + 1;
            }
        }

        //sinon on retourne rien
        return "rien";
    }




    /**
     * Fonction lorsque le point de départ est supérieur au point d'arrivée
     * @param depPoint
     * @param finPoint
     * @param intervalle:  représente le nombre de points. Pour une grille 9x9, on aura un intervalle de 3. Une grille 16x16 aura un intervalle de 4. etc..
     * @return
     */
    public String getValueTraitementInf(int depPoint, int finPoint, int intervalle, int colDep, int ligDep, int colArr, int ligArr)
    {
        int depPointTemp = depPoint;
        int finPointTemp = finPoint;


        //cas horizontal
        //On s'arrête lorsque les deux points sont égaux ou bien si notre compteur est égal à l'intervalle pour ne pas aller trop loin
        compteur = 1;
        if (ligDep == ligArr) {
            while (compteur < intervalle) {
                depPointTemp = depPointTemp + 1;
                if (finPointTemp == depPointTemp){
                    valuePoints = 1;
                    return "horizontal";
                }
                compteur++;
            }
        }

        //cas vertical
        //Récupérer les bonnes valeurs. Etant donnée que l'on travaille verticalement, on incrémentera notre compteur de 3, 4 ou 5 selon la taille de notre grille.
        compteur = intervalle;
        depPointTemp = depPoint;
        if (colArr == colDep) {
            while (depPointTemp < finPointTemp) {
                depPointTemp += intervalle;
                if (finPointTemp == depPointTemp){
                    valuePoints = intervalle;
                    return "vertical";
                }
                compteur = compteur + intervalle;
            }
        }

        //Nécessaire pour le calcul des diag
        if (colArr == 0)
            colArr = intervalle;

        if (colDep == 0)
            colDep = intervalle;


        System.out.println("La colonne de début est: " + colDep);
        System.out.println("La colonne de fin est: " + colArr);
        System.out.println("salut sup");


        if (colArr > colDep) {
            //Cas diagonale
            //Récupérer les bonnes valeurs. Etant donnée que l'on travaille diagonalement, on incrémentera notre compteur de 3, 4 ou 5  + 1 selon la taille de notre grille.
            compteur = intervalle + 1;
            depPointTemp = depPoint;
            while (depPointTemp < finPointTemp) {
                depPointTemp += intervalle + 1;
                if (finPointTemp == depPointTemp) {
                    diagType = "plus";
                    return "diagonal";
                }
                compteur = compteur + intervalle + 1;
            }
        }
        else if (colDep > colArr) {
            System.out.println("salut inf");
            //Cas diagonale
            //Récupérer les bonnes valeurs. Etant donnée que l'on travaille diagonalement, on incrémentera notre compteur de 3, 4 ou 5  + 1 selon la taille de notre grille.
            compteur = intervalle - 1;
            depPointTemp = depPoint;
            while (depPointTemp < finPointTemp) {
                System.out.println("DEBUTMOINS: " + depPointTemp);
                depPointTemp += intervalle - 1;
                if (finPointTemp == depPointTemp) {
                    diagType = "moins";
                    return "diagonal";
                }
                compteur = compteur + intervalle - 1;
            }
        }

        //sinon on retourne rien
        return "rien";
    }



    public void ajoutPointsLigneSup(int compteur, int curPos)
    {
        Pair<NodeView, NodeView> pair;
        //Noeud intermédiaires
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;

        System.out.println("ValuePoint vaut: " + valuePoints + " et compteur: " + compteur);

        for (int j = 0; j < compteur; j += valuePoints) {
            //Récupérer les points
            noeudActuel =  getNodeWithPosition(curPos - j);
            prochainNoeud =  getNodeWithPosition(curPos - j - valuePoints);

            //dessiner le point et rendre le point highlited
            canvas.drawLine(noeudActuel.getCenterX(), noeudActuel.getCenterY(), prochainNoeud.getCenterX(), prochainNoeud.getCenterY(), paint);
            noeudActuel.setHighLighted(true);
            prochainNoeud.setHighLighted(true);

            //Ajouter les bonnes valeurs au tableau
            pair = new Pair<NodeView, NodeView>(noeudActuel, prochainNoeud);
            lineList.add(pair);
            //incrémenter le nombre de points
            nbPointSelec++;
            //Ajout du noeud au mot de passe
            pwdSb.append(noeudActuel.getNum());
        }
    }


    public void ajoutPointsLigneInf(int compteur, int curPos)
    {
        Pair<NodeView, NodeView> pair;
        //Noeud intermédiaires
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;

        for (int j = 0; j < compteur; j +=  valuePoints) {
            //Récupérer les points
            noeudActuel =  getNodeWithPosition(curPos + j);
            prochainNoeud =  getNodeWithPosition(curPos + j + valuePoints);

            //dessiner le point et rendre le point highlited
            canvas.drawLine(noeudActuel.getCenterX(), noeudActuel.getCenterY(), prochainNoeud.getCenterX(), prochainNoeud.getCenterY(), paint);
            noeudActuel.setHighLighted(true);
            prochainNoeud.setHighLighted(true);

            //Ajouter les bonnes valeurs au tableau
            pair = new Pair<NodeView, NodeView>(noeudActuel, prochainNoeud);
            lineList.add(pair);
            //incrémenter le nombre de points
            nbPointSelec++;
            //Ajout du noeud au mot de passe
            pwdSb.append(noeudActuel.getNum());
        }
    }



    public class NodeView extends View {

        private int num;
        private boolean highLighted;

        private NodeView(Context context) {
            super(context);
        }

        public NodeView(Context context, int num) {
            this(context);
            this.num = num;
            highLighted = false;
            if (nodeSrc == null) {
                setBackgroundResource(0);
            } else {
                setBackgroundDrawable(nodeSrc);
            }
        }

        public boolean isHighLighted() {
            return highLighted;
        }

        public void setHighLighted(boolean highLighted) {
            this.highLighted = highLighted;
            if (highLighted) {
                if (nodeOnSrc == null) {
                    setBackgroundResource(0);
                } else {
                    setBackgroundDrawable(nodeOnSrc);
                }
            } else {
                if (nodeSrc == null) {
                    setBackgroundResource(0);
                } else {
                    setBackgroundDrawable(nodeSrc);
                }
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

    }

    public interface CallBack {

        public void onFinish(String password, int nombre,  StringBuilder ok);

    }

}
