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
import br.si.cond.model.Agendamento;
import br.si.cond.model.Reinvidicacao;


public class ReivindicacoesAdapter extends ArrayAdapter<Reinvidicacao> {

    ArrayList<Reinvidicacao> reinvidicacoes;
    Context ctx;
    public ReivindicacoesAdapter(Context ctx, ArrayList<Reinvidicacao> reinvidicacoes){
        super(ctx, R.layout.row_reinvidicacao);
        this.reinvidicacoes=reinvidicacoes;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return reinvidicacoes.size();
    }

    @Override
    public Reinvidicacao getItem(int position) {
        return reinvidicacoes.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_reinvidicacao, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.lblTexto = (TextView)view.findViewById(R.id.lblMensagem);
            holder.lblStatus = (TextView)view.findViewById(R.id.lblStatus);
            holder.lblData = (TextView)view.findViewById(R.id.lblData);
            holder.img = (ImageView)view.findViewById(R.id.imgReinvidicacao);

            view.setTag(holder);


        }
        ViewHolder holder = (ViewHolder)view.getTag();
        Reinvidicacao reinvidicacao  = reinvidicacoes.get(position);

        holder.lblTexto.setText(reinvidicacao.getMensagem());
        if(reinvidicacao.getMensagem().length()>20)
            holder.lblTexto.setText(reinvidicacao.getMensagem().substring(0,17)+"..");

        DateFormat df = new SimpleDateFormat("HH:mm:ss");


        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        String data =dfDate.format(reinvidicacao.getCreated_at());

        holder.lblData.setText(data);

        String status="";
        if(reinvidicacao.getStatus().equalsIgnoreCase("A")){
            status="Aguardando resposta da administradora";
        }else if(reinvidicacao.getStatus().equalsIgnoreCase("M")){
            status="Aguardando resposta do morador";
        }else if(reinvidicacao.getStatus().equalsIgnoreCase("S")){
            status="Aguardando resposta do sindico";
        }else if(reinvidicacao.getStatus().equalsIgnoreCase("C")){
            status="Concluído";
        }

        holder.lblStatus.setText(status);

        byte[] decodedString = Base64.decode(reinvidicacao.getFoto(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.img.setImageBitmap(decodedByte);

        return view;
    }

    private class ViewHolder {
        ImageView img;
        TextView lblTexto;
        TextView lblStatus;
        TextView lblData;
    }



}
