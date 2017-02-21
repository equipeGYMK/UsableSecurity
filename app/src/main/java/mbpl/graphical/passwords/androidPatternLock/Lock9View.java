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
    private int nbPointsMinimum;
    private int compteurPoints = 0;


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
        nbPointsMinimum = methode.getParam2();
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
                        canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(), nodeAt.getCenterX(), nodeAt.getCenterY(), paint);
                        nodeAt.setHighLighted(true);
                        Pair<NodeView, NodeView> pair = new Pair<NodeView, NodeView>(currentNode, nodeAt);
                        lineList.add(pair);

                        //changement de noeud
                        currentNode = nodeAt;
                        //Ajout du noeud au mot de passe
                        pwdSb.append(currentNode.getNum());
                        compteurPoints++;
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
        canvas.drawColor(Color.RED, PorterDuff.Mode.CLEAR);
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
