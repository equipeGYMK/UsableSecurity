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

    private Methode methode =  new PatternLock();
    private MethodeManager methodeManager;

    private int nbPoints;
    private int compteurPoints = 0;
    private boolean verifPointsValide = false;
    private int compteur, nbPointSelec, valuePoints;
    private String diagType;

    /**
     * Construit un nouveau Lock9View
     * @param context
     */
    public Lock9View(Context context) {
        this(context, null);
        this.context = context;
    }
    
    /**
     * Construit un nouveau Lock9View
     * @param context
     * @param attrs
     */
    public Lock9View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    /**
     * Construit un nouveau Lock9View
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Lock9View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        this.context = context;
    }

    /**
     * Construit un nouveau Lock9View
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public Lock9View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initFromAttributes(attrs, defStyleAttr);
    }
    
    /**
     * Initialise le lock9view en fonction des attributs et du syle d'attribut
     * @param attrs
     * @param defStyleAttr
     */
    public void initFromAttributes(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock9View, defStyleAttr, 0);
        methodeManager = new MethodeManager(context);
        methodeManager.open();
        methode = methodeManager.getMethode(methode);
        nbPoints = methode.getParam1();
        methodeManager.close();

        nodeSrc = a.getDrawable(R.styleable.Lock9View_nodeSrc);
        nodeOnSrc = a.getDrawable(R.styleable.Lock9View_nodeOnSrc);
        int lineColor = Color.argb(0, 0, 0, 0);
        lineColor = a.getColor(R.styleable.Lock9View_lineColor, lineColor);
        float lineWidth = 20.0f;
        lineWidth = a.getDimension(R.styleable.Lock9View_lineWidth, lineWidth);

        a.recycle();

        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(lineColor);
        paint.setAntiAlias(true);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        bitmap = Bitmap.createBitmap(dm.widthPixels, dm.widthPixels, Bitmap.Config.ARGB_8888);

        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        for (int n = 0; n < nbPoints; n++) {
            NodeView node = new NodeView(getContext(), n + 1);
            addView(node);
        }
        lineList = new ArrayList<Pair<NodeView,NodeView>>();
        pwdSb = new StringBuilder();

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }
        int pointsUtils = getPointsUtils(nbPoints);
        int largeurCanvas = right - left;
        int nodeWidth = largeurCanvas /pointsUtils;
        int nodePadding = nodeWidth / 8;

        for (int n = 0; n < nbPoints; n++) {
            NodeView node = (NodeView) getChildAt(n);
            int row = n / pointsUtils;
            int col = n % pointsUtils;
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
            case MotionEvent.ACTION_MOVE:
                NodeView nodeAt = getNodeAt(event.getX(), event.getY());
                if (nodeAt == null && currentNode == null) {
                    return true;
                } else {
                    clearScreenAndDrawList();
                    if (currentNode == null) {
                        currentNode = nodeAt;
                        currentNode.setHighLighted(true);
                        pwdSb.append(currentNode.getNum());
                        compteurPoints++;
                    }
                    else if (nodeAt == null || nodeAt.isHighLighted()) {
                        canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), event.getX(), event.getY(), paint);
                    } else {
                        verifPointsValide = verificationPoints(currentNode, nodeAt);

                        if (verifPointsValide) {
                            currentNode = nodeAt;
                            compteurPoints += nbPointSelec;
                        }
                        else
                            canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), event.getX(), event.getY(), paint);
                    }

                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:


                if (pwdSb.length() <= 0) {
                    return super.onTouchEvent(event);
                }

                if (callBack != null) {
                    pwdSb.append(currentNode.getNum());
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

                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Nettoie l'écran et dessine dessus la liste LineList
     */
    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        for (Pair<NodeView, NodeView> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
    }

    /**
     * Retourne la node au coordonées données
     * @param x abscisse de la node
     * @param y ordonnée de la node
     * @return Nodeview d'après les coordonées
     */
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

    /**
     * Retourne la node a la position donnée
     * @param position position de la node
     * @return NodeView selon la position
     */
    private NodeView getNodeWithPosition(int position) {
        NodeView node = (NodeView) getChildAt(position - 1);
        return node;
    }

    /**
    *
    * @param callBack callBack to set
    */
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    /**
    * Retourne le nombre de points sur une ligne
    * @param points nombre de points total
    * @return le nombre de points sur une ligne
    */
    public int getPointsUtils(int points){
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

    /**
    * Verification du point donnée selon s'il a déjà été selectionné
    * @param currentNode
    * @param nodeAt
    * @return point valide
    */
    public boolean verificationPoints(NodeView currentNode, NodeView nodeAt){

        String result;
        int i;
        Pair<NodeView, NodeView> pair;
        int moduloNbPoints = getPointsUtils(nbPoints);

        int pointDepCol = currentNode.getNum() % moduloNbPoints;
        int pointArriveCol = nodeAt.getNum() % moduloNbPoints;
        int pointDepLig = (currentNode.getNum() - 1) / moduloNbPoints;
        int pointArriveLig = (nodeAt.getNum() - 1) / moduloNbPoints;

        int curPos = currentNode.getNum();
        int aTPos = nodeAt.getNum();
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;

        if (curPos > aTPos)
        {
            result =  getValueTraitementSup(curPos, aTPos, moduloNbPoints, pointDepCol, pointDepLig, pointArriveCol, pointArriveLig);
            System.out.println("QUe vaut result:" + result);
            switch  (result){
                case "horizontal":{
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                case "vertical":{
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                case "diagonal":{
                    if (diagType.equals("plus"))
                        valuePoints = moduloNbPoints - 1;
                    else
                        valuePoints = moduloNbPoints + 1;
                    ajoutPointsLigneSup(compteur, curPos);
                    break;
                }
                default:{
                    canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                    currentNode.setHighLighted(true);
                    nodeAt.setHighLighted(true);
                    pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                    lineList.add(pair);
                    nbPointSelec++;
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
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                case "vertical":{
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                case "diagonal":{
                    if (diagType.equals("plus"))
                        valuePoints = moduloNbPoints + 1;
                    else
                        valuePoints = moduloNbPoints - 1;
                    ajoutPointsLigneInf(compteur, curPos);
                    break;
                }
                default:{
                    canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                    currentNode.setHighLighted(true);
                    nodeAt.setHighLighted(true);
                    pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                    lineList.add(pair);
                    nbPointSelec++;
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

        if (colDep == 0)
            colDep = intervalle;

        if (colArr == 0)
            colArr = intervalle;

        if (colArr > colDep) {
            compteur = intervalle - 1;
            depPointTemp = depPoint;
            while (depPointTemp > finPointTemp) {
                depPointTemp -= intervalle - 1;
                if (depPointTemp == finPointTemp) {
                    diagType = "plus";
                    return "diagonal";
                }
                compteur += intervalle - 1;
            }
        }
        else if (colDep > colArr){
            compteur = intervalle + 1;
            depPointTemp = depPoint;
            while (depPointTemp > finPointTemp)
            {
                depPointTemp -= intervalle + 1;
                if (depPointTemp == finPointTemp) {
                    diagType = "moins";
                    return "diagonal";
                }
                compteur += intervalle + 1;
            }
        }
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

        if (colArr == 0)
            colArr = intervalle;

        if (colDep == 0)
            colDep = intervalle;

        if (colArr > colDep) {
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
            compteur = intervalle - 1;
            depPointTemp = depPoint;
            while (depPointTemp < finPointTemp) {
                depPointTemp += intervalle - 1;
                if (finPointTemp == depPointTemp) {
                    diagType = "moins";
                    return "diagonal";
                }
                compteur = compteur + intervalle - 1;
            }
        }

        return "rien";
    }
    
    /**
     * Ajoute un point sur la ligne superieur
     * @param compteur
     * @param curPos
     */
    public void ajoutPointsLigneSup(int compteur, int curPos)
    {
        Pair<NodeView, NodeView> pair;
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;

        for (int j = 0; j < compteur; j += valuePoints) {
            noeudActuel =  getNodeWithPosition(curPos - j);
            prochainNoeud =  getNodeWithPosition(curPos - j - valuePoints);

            canvas.drawLine(noeudActuel.getCenterX(), noeudActuel.getCenterY(), prochainNoeud.getCenterX(), prochainNoeud.getCenterY(), paint);
            noeudActuel.setHighLighted(true);
            prochainNoeud.setHighLighted(true);

            pair = new Pair<NodeView, NodeView>(noeudActuel, prochainNoeud);
            lineList.add(pair);
            nbPointSelec++;
            pwdSb.append(noeudActuel.getNum());
        }
    }

    /**
     * Ajoute un point sur la ligne inferieur
     * @param compteur
     * @param curPos
     */
    public void ajoutPointsLigneInf(int compteur, int curPos)
    {
        Pair<NodeView, NodeView> pair;
        NodeView noeudActuel, prochainNoeud;
        nbPointSelec = 0;

        for (int j = 0; j < compteur; j +=  valuePoints) {
            noeudActuel =  getNodeWithPosition(curPos + j);
            prochainNoeud =  getNodeWithPosition(curPos + j + valuePoints);

            canvas.drawLine(noeudActuel.getCenterX(), noeudActuel.getCenterY(), prochainNoeud.getCenterX(), prochainNoeud.getCenterY(), paint);
            noeudActuel.setHighLighted(true);
            prochainNoeud.setHighLighted(true);

            pair = new Pair<NodeView, NodeView>(noeudActuel, prochainNoeud);
            lineList.add(pair);
            nbPointSelec++;
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

        void onFinish(String password, int nombre, StringBuilder ok);

    }

}
