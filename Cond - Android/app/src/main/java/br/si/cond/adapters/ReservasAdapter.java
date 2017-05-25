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
import br.si.cond.model.Agendamento;


public class ReservasAdapter extends ArrayAdapter<Agendamento> {

    ArrayList<Agendamento> reservas;
    Context ctx;
    public ReservasAdapter(Context ctx, ArrayList<Agendamento> eventos){
        super(ctx, R.layout.row_reserva);
        this.reservas=eventos;
        this.ctx=ctx;
    }

    @Override
    public int getCount() {


        return reservas.size();
    }

    @Override
    public Agendamento getItem(int position) {
        return reservas.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.row_reserva, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.lblAmbiente = (TextView)view.findViewById(R.id.lblAmbiente);
            holder.lblData = (TextView)view.findViewById(R.id.lblData);

            view.setTag(holder);


        }
        ViewHolder holder = (ViewHolder)view.getTag();
        Agendamento agendamento  = reservas.get(position);

        holder.lblAmbiente.setText(agendamento.getAmbiente().getNome());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String hrAgendamento =df.format(agendamento.getHora());

        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        String data =dfDate.format(agendamento.getData());

        holder.lblData.setText(data+" - "+hrAgendamento);


        return view;
    }

    private class ViewHolder {

        TextView lblAmbiente;
        TextView lblData;
    }



}
