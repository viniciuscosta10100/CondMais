package br.si.cond.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.si.cond.R;
import br.si.cond.model.Agendamento;
import br.si.cond.model.Ambiente;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class NovaReservaActivity extends ActionBarActivity implements OnDateSelectedListener {
    ArrayList<Ambiente> ambientes = new ArrayList<Ambiente>();
    Spinner spnAmbientes;
    Spinner spnHorarios;
    MaterialCalendarView calendarView;

    Date dataSelected = new Date();
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_agendamento);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        calendarView =(MaterialCalendarView)findViewById(R.id.calendarView);


        calendarView.setOnDateChangedListener(this);
        ActionTask actionLoadGames = new ActionTask(NovaReservaActivity.this, ambientesAction, "Aguarde..");
        actionLoadGames.execute();
    }
    private void saveReserva(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_novo_agendamento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ac_save:
                saveReserva();

                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    Action ambientesAction = new Action() {

        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {


                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                ambientes = rs.getAmbientes(App.getUser(getApplicationContext()).getCondominio().getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public void showAlert(String title){

        final Dialog dialog = new Dialog(NovaReservaActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ambiente_reserva);
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        Button btnOk = (Button)dialog.findViewById(R.id.btnOk);
        TextView lblTitle = (TextView)dialog.findViewById(R.id.lblTitle);

        lblTitle.setText(title);
        spnAmbientes =(Spinner)dialog.findViewById(R.id.spnAmbientes);
        spnHorarios =(Spinner)dialog.findViewById(R.id.spnHorario);
        String[] ambientes_str = new String[ambientes.size()+1];
        int index=1;
        ambientes_str[0]="Selecione o ambiente";
        for(Ambiente amb : ambientes){
            ambientes_str[index]=amb.getNome();
            index++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,ambientes_str);
        spnAmbientes.setAdapter(adapter);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Boolean ambienteOk=true;
                Boolean hrOk=true;

                String hr = spnHorarios.getSelectedItem().toString();

                if(spnAmbientes.getSelectedItemPosition()<1){
                    ambienteOk=false;
                    Toast.makeText(getApplicationContext(),"Selecione o ambiente",Toast.LENGTH_LONG).show();
                }
                if(spnHorarios.getSelectedItemPosition()<1){
                    hrOk=false;
                    Toast.makeText(getApplicationContext(),"Selecione o horario",Toast.LENGTH_LONG).show();
                }

                if(ambienteOk && hrOk){
                    ActionTask actionLoadGames = new ActionTask(NovaReservaActivity.this, saveReservaAction, "Aguarde..");
                    saveReservaAction.param=hr;
                    saveReservaAction.param2=ambientes.get(spnAmbientes.getSelectedItemPosition()-1).getId().toString();
                    actionLoadGames.execute();
                }

            }
        });



        spnAmbientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position>0) {

                    Ambiente amb =ambientes.get(position - 1);

                    ActionTask actionLoadGames = new ActionTask(NovaReservaActivity.this, agendamentosAction, "Buscando horarios disponiveis, aguarde..");
                    agendamentosAction.param = amb.getId().toString();
                    actionLoadGames.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        dialog.show();

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        String data =FORMATTER.format(date.getDate());
        dataSelected = date.getDate();
        showAlert(data);
    }


    Action agendamentosAction = new Action() {
        ArrayList<Agendamento> reservas = new ArrayList<Agendamento>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    ArrayList<String> horarios = new ArrayList<String>();
                    horarios.add("Selecione o horario");
                    for(Integer i =0;i<24;i++){
                        Boolean disponivel=true;
                        String hr=String.format("%02d", i)+":00:00";
                        for(Agendamento reserva : reservas){
                            DateFormat df = new SimpleDateFormat("HH:mm:ss");
                            String hrAgendamento =df.format(reserva.getHora());

                            if(hr.equalsIgnoreCase(hrAgendamento)){
                                disponivel=false;
                            }
                        }
                        if(disponivel)
                            horarios.add(hr);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,horarios);
                    spnHorarios.setAdapter(adapter);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String data =df.format(dataSelected);
                reservas = rs.getAgendamentos(data,Long.parseLong(param));


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    Action saveReservaAction = new Action() {
        ResultServer result = new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    if(result.getResult().equalsIgnoreCase("true"))
                        Utils.showAlert("Atenção",result.getMessage(),mCtx,NovaReservaActivity.this);
                    else
                        Utils.showAlert("Atenção",result.getMessage(),mCtx);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String data =df.format(dataSelected);
                User user = App.getUser(getApplicationContext());
                result = rs.saveReserva(data,param,param2,user.getId().toString());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


}
