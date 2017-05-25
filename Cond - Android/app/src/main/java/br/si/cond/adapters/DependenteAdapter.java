package br.si.cond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.model.User;
import br.si.cond.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class DependenteAdapter extends BaseAdapter {
    private Context context;
    private final ArrayList<User> dependentes;

    public DependenteAdapter(Context context, ArrayList<User> dependentes) {
        this.context = context;
        this.dependentes = dependentes;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            User user = dependentes.get(position);
            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.grid_dependente, null);

            // set value into textview
            TextView lblNome = (TextView) gridView
                    .findViewById(R.id.lblNome);
            lblNome.setText(user.getNome());

            // set image based on selected text
            CircleImageView imgUser = (CircleImageView) gridView
                    .findViewById(R.id.imgUser);


            imgUser.setImageBitmap(Utils.base64ToBitmap(user.getFoto()));

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return dependentes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
