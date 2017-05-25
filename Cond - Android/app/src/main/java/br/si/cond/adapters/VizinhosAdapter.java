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
import java.util.Calendar;

import br.si.cond.R;
import br.si.cond.activities.App;
import br.si.cond.model.Mensagem;
import br.si.cond.model.User;


public class VizinhosAdapter extends ArrayAdapter<User> {

    ArrayList<User> vizinhos;
    Context ctx;
    public VizinhosAdapter(Context ctx, ArrayList<User> vizinhos){
        super(ctx, R.layout.row_recente);
        this.vizinhos=vizinhos;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return vizinhos.size();
    }

    @Override
    public User getItem(int position) {
        return vizinhos.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_recente, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.lblNome = (TextView)view.findViewById(R.id.lblNome);
            holder.lblMensagem = (TextView)view.findViewById(R.id.lblMensagem);
            holder.lblData = (TextView)view.findViewById(R.id.lblData);
            holder.img = (ImageView)view.findViewById(R.id.imgUser);

            view.setTag(holder);


        }
        ViewHolder holder = (ViewHolder)view.getTag();
        User vizinho  = vizinhos.get(position);

        if(vizinho.getTipo().equalsIgnoreCase("M")){

            byte[] decodedString = Base64.decode(vizinho.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.img.setImageBitmap(decodedByte);
            holder.lblMensagem.setText("Residencia: "+vizinho.getResidencia().getNumero().toString());
        }else{
            holder.lblMensagem.setText("SINDICO");
            holder.img.setImageResource(R.drawable.bg_sindico);
        }

        holder.lblNome.setText(vizinho.getNome());
        holder.lblData.setText("");



        return view;
    }

    private class ViewHolder {
        ImageView img;
        TextView lblNome;
        TextView lblMensagem;
        TextView lblData;
    }



}
