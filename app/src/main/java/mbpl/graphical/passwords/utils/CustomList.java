package mbpl.graphical.passwords.utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.accueil.Accueil;
import mbpl.graphical.passwords.accueil.AdminUser;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;
import mbpl.graphical.passwords.sqlite.Passfaces;
import mbpl.graphical.passwords.sqlite.PatternLock;

import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;

/**
 * Outil permettant de mettre en place des liste de méthode d'authentification avec les
 * images associées
 *
 *
 */
public class CustomList extends BaseAdapter{

    String interfaceNom;
    protected ListView mListView;
    String [] result;
    String [] description;
    Context context;
    int [] imageId;
    int valide;
    private static LayoutInflater inflater=null;
    boolean isValide;



    //Constructeur user
    public CustomList(ListView lv, Accueil mainActivity, String[] prgmNameList, int[] prgmImages, String[] description, String user) {
        // TODO Auto-generated constructor stub
        this.description = description;
        this.interfaceNom = user;
        mListView = lv;
        result = prgmNameList;
        context = mainActivity;
        imageId = prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        final Holder holder;

        rowView = inflater.inflate(R.layout.technics_list, null);
        holder = new Holder();

        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.descriptionTechnic = (TextView) rowView.findViewById(R.id.textView3);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.buttonDescrption = (Button) rowView.findViewById(R.id.buttonList);
        holder.descriptionTechnic.setVisibility(View.GONE);

        holder.mdpCreated = (TextView) rowView.findViewById(R.id.mdp_existant);

        holder.descriptionTechnic.setText(description[position]);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        // indicateur mot de passe cree
        if (interfaceNom.equals("User")) {

            isValide = false;
            Methode m;
            MethodeManager mm = new MethodeManager(context);
            mm.open();
            m = mm.getMethode(implementedMethods.get(position));
            valide = m.getParam2();
            mm.close();


            if ((valide == 1) && (implementedMethods.get(position) instanceof Passfaces))
                isValide = true;

            if (((!mm.defaultPassword(m)) && (isValide)) || ((!mm.defaultPassword(m)) && (implementedMethods.get(position) instanceof PatternLock))){
                holder.mdpCreated.setTextColor(Color.RED);
                holder.mdpCreated.setText("MDP Créé");

            } else {
                //holder.mdpCreated.setTextColor(Color.RED);
                holder.mdpCreated.setText("");
            }


        }

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent appel;
            appel = new Intent(context, AdminUser.class);
            appel.putExtra("methode", position);
            context.startActivity(appel);
            }
        });


        holder.buttonDescrption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout parentVue = (LinearLayout) v.getParent().getParent();
                TextView textDescrp = (TextView) parentVue.findViewById(R.id.textView3);

                if (textDescrp.isShown())
                {
                    holder.buttonDescrption.setText("Show Info");
                    textDescrp.setVisibility(View.GONE);
                }
                else
                {
                    holder.buttonDescrption.setText("Hide Info");
                    textDescrp.setVisibility(View.VISIBLE);
                }
            }
        });


        return rowView;
    }


    public class Holder
    {
        TextView tv, descriptionTechnic, mdpCreated;
        ImageView img;
        Button buttonDescrption;

    }
}