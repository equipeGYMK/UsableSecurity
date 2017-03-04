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

import mbpl.graphical.passwords.accueil.AccueilAdmin;
import mbpl.graphical.passwords.accueil.AccueilUser;
import mbpl.graphical.passwords.R;
import mbpl.graphical.passwords.sqlite.Methode;
import mbpl.graphical.passwords.sqlite.MethodeManager;

import static android.app.PendingIntent.getActivity;
import static mbpl.graphical.passwords.sqlite.ImplementedMethods.implementedMethods;

public class CustomList extends BaseAdapter{

    String interfaceNom;
    protected ListView mListView;
    String [] result;
    String [] description;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;


    //Constructeur user
    public CustomList(ListView lv, AccueilUser mainActivity, String[] prgmNameList, int[] prgmImages, String[] description, String user) {
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


    //constructeur admin
    public CustomList(ListView lv, AccueilAdmin mainActivity, String[] prgmNameList, int[] prgmImages, String[] description,String admin) {
        // TODO Auto-generated constructor stub
        this.description = description;
        this.interfaceNom = admin;
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

            Methode m;
            MethodeManager mm = new MethodeManager(context);
            mm.open();
            m = mm.getMethode(implementedMethods.get(position));
            if (!mm.defaultPassword(m)) {
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

                //On va avoir le cas où l'on se trouve dans la partie user
                if (interfaceNom.equals("User")) {

                    //Si l'attribut mdp est différent de nul alors on s'est déjà authentifié
                    Intent appel;
                    Methode m;
                    MethodeManager mm = new MethodeManager(context);
                    mm.open();
                    m = mm.getMethode(implementedMethods.get(position));
                    if (!mm.defaultPassword(m)) {
                        appel = new Intent(context, implementedMethods.get(position).getAuthentification());
                    } else {
                        appel = new Intent(context, implementedMethods.get(position).getCreation());
                    }

                    mm.close();
                    context.startActivity(appel);
                }
                //Cas où l'on se trouve dans l'interfac admin
                else if (interfaceNom.equals("Admin")){

                    Intent appel;
                    //On va effectuer les différents cas en comparant le nom des techniques
                    appel = new Intent(context, implementedMethods.get(position).getConfiguration());
                    context.startActivity(appel);

                }
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