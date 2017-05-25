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
import br.si.cond.model.Mensagem;
import br.si.cond.model.User;


public class RecentesAdapter extends ArrayAdapter<Mensagem> {

    ArrayList<Mensagem> mensagens;
    Context ctx;
    public RecentesAdapter(Context ctx, ArrayList<Mensagem> mensagens){
        super(ctx, R.layout.row_recente);
        this.mensagens=mensagens;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return mensagens.size();
    }

    @Override
    public Mensagem getItem(int position) {
        return mensagens.get(position);
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
        Mensagem msg  = mensagens.get(position);

        holder.lblMensagem.setText(msg.getTexto());
        if(msg.getTexto().length()>25)
            holder.lblMensagem.setText(msg.getTexto().substring(0, 22)+"...");

        User user = App.getUser(ctx);

        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.setTime(msg.getCreated_at());
        calendar.add(Calendar.HOUR, -1);

        String data =dfDate.format(calendar.getTime());

        holder.lblData.setText(data);
        if(msg.getDestinatario().getId()==user.getId()){
            holder.lblNome.setText(msg.getRemetente().getNome());
            if(msg.getRemetente().getTipo().equalsIgnoreCase("M")){


                byte[] decodedString = Base64.decode(msg.getRemetente().getFoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.img.setImageBitmap(decodedByte);
            }else{
                holder.img.setImageResource(R.drawable.bg_sindico);
            }
        }else{
            holder.lblNome.setText(msg.getDestinatario().getNome());
            if(msg.getDestinatario().getTipo().equalsIgnoreCase("M")){

                byte[] decodedString = Base64.decode(msg.getDestinatario().getFoto(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.img.setImageBitmap(decodedByte);
            }else{
                holder.img.setImageResource(R.drawable.bg_sindico);
            }
        }


        return view;
    }

    private class ViewHolder {
        ImageView img;
        TextView lblNome;
        TextView lblMensagem;
        TextView lblData;
    }



}
