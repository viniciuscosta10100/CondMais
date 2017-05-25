package br.si.cond.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.activities.App;
import br.si.cond.model.ComentarioReinvidicacao;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.model.User;


public class ComentariosAdapter extends ArrayAdapter<ComentarioReinvidicacao> {

    ArrayList<ComentarioReinvidicacao> comentarios;
    Context ctx;
    public ComentariosAdapter(Context ctx, ArrayList<ComentarioReinvidicacao> comentarios){
        super(ctx, R.layout.row_comentario);
        this.comentarios=comentarios;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return comentarios.size();
    }

    @Override
    public ComentarioReinvidicacao getItem(int position) {
        return comentarios.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_comentario, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.lblTexto = (TextView)view.findViewById(R.id.lblMensagem);
            holder.lblRemetente = (TextView)view.findViewById(R.id.lblRemetente);
            holder.lblData = (TextView)view.findViewById(R.id.lblData);

            view.setTag(holder);


        }
        ViewHolder holder = (ViewHolder)view.getTag();
        ComentarioReinvidicacao comentario  = comentarios.get(position);

        holder.lblTexto.setText(comentario.getMensagem());



        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        String data =dfDate.format(comentario.getCreated_at());

        holder.lblData.setText(data);

        User user = App.getUser(ctx);
        if(comentario.getUser().getId() == user.getId())
            holder.lblRemetente.setText("Você");
        else{
            if(comentario.getUser().getTipo().equalsIgnoreCase("A"))
                holder.lblRemetente.setText(comentario.getUser().getNome()+" - ADMINISTRADOR(A)");
            else if(comentario.getUser().getTipo().equalsIgnoreCase("S"))
                holder.lblRemetente.setText(comentario.getUser().getNome()+" - SINDICO(A)");
            else if(comentario.getUser().getTipo().equalsIgnoreCase("M"))
                holder.lblRemetente.setText(comentario.getUser().getNome()+" - MORADOR(A)");

        }



        return view;
    }

    private class ViewHolder {
        TextView lblTexto;
        TextView lblRemetente;
        TextView lblData;
    }



}
