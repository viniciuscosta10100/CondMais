package br.si.cond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.model.Aviso;


public class AvisosAdapter extends ArrayAdapter<Aviso> {

    ArrayList<Aviso> avisos;
    Context ctx;
    public AvisosAdapter(Context ctx, ArrayList<Aviso> eventos){
        super(ctx, R.layout.row_aviso);
        this.avisos=eventos;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return avisos.size();
    }

    @Override
    public Aviso getItem(int position) {
        return avisos.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_aviso, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.lblMensagem = (TextView)view.findViewById(R.id.lblMensagem);
            holder.lblData = (TextView)view.findViewById(R.id.lblData);

            view.setTag(holder);


        }
        ViewHolder holder = (ViewHolder)view.getTag();
        Aviso aviso  = avisos.get(position);

        holder.lblMensagem.setText(aviso.getMensagem());
//        DateFormat df = new SimpleDateFormat("HH:mm:ss");
//        String hrAviso =df.format(Aviso.getHora());

        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        String data =dfDate.format(aviso.getCreated_at());

        holder.lblData.setText(aviso.getRemetente()+" - "+data);


        return view;
    }

    private class ViewHolder {

        TextView lblMensagem;
        TextView lblData;
    }



}
