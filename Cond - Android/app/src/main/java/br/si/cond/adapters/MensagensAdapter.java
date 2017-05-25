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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.si.cond.R;
import br.si.cond.activities.App;
import br.si.cond.model.Mensagem;
import br.si.cond.model.User;
import br.si.cond.utils.InfiniteScrollAdapter;


public class MensagensAdapter extends InfiniteScrollAdapter {

    ArrayList<Mensagem> list= new ArrayList<Mensagem>();
    User userLogged;
    Context mContext;
    public MensagensAdapter(Context c)
    {
        super(c);
        mContext = c;
        this.list=list;
        userLogged = App.getUser(c);
    }

    public ArrayList<Mensagem> getList(){
        return this.list;
    }


    public void addJogos(ArrayList<Mensagem> newJogos){
        list.addAll(newJogos);
        this.notifyDataSetChanged();
    }

    @Override
    public void clear(){
        list = new ArrayList<Mensagem>();
        this.notifyDataSetChanged();
    }


    @Override
    public Collection getItems() {
        // TODO Auto-generated method stub
        return list;
    }

    @Override
    public void addItems(Collection items) {
        // TODO Auto-generated method stub
        if (items.size() > 0) {
            this.list.addAll(items);
        } else {
            super.setDoneLoading();
        }
        notifyDataSetChanged();
    }

    @Override
    public Object getRealItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public View getRealView(LayoutInflater inflater, int position,
                            View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_msg_remetente, parent, false);
        Mensagem msg  = list.get(position);

        if(userLogged.getId()==msg.getRemetente().getId())
            view = LayoutInflater.from(mContext).inflate(R.layout.row_msg_remetente, parent, false);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.row_msg_destinatario, parent, false);

        TextView lblMensagem = (TextView)view.findViewById(R.id.lblMensagem);
        TextView lblData = (TextView)view.findViewById(R.id.lblData);


        lblMensagem.setText(msg.getTexto());



        DateFormat dfDate = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.setTime(msg.getCreated_at());
        calendar.add(Calendar.HOUR, -1);

        String data =dfDate.format(calendar.getTime());

        lblData.setText(data);

        return view;
    }

    @Override
    public View getLoadingView(LayoutInflater inflater, ViewGroup parent) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.list_loading, null);
    }



}
