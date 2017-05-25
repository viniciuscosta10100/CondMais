package br.si.cond.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.adapters.ReservasAdapter;
import br.si.cond.model.Agendamento;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class ReservasActivity extends ActionBarActivity  {

    LinearLayout llEmpty;
    ListView lvReservas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llEmpty = (LinearLayout)findViewById(R.id.llEmpty);
        lvReservas = (ListView)findViewById(R.id.lvReservas);

        findViewById(R.id.btnNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NovaReservaActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


        ActionTask actionLoadGames = new ActionTask(ReservasActivity.this, agendamentosAction, "Buscando reservas..");
        actionLoadGames.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_reservas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    Action agendamentosAction = new Action() {
        ArrayList<Agendamento> reservas = new ArrayList<Agendamento>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(reservas.size()>0) {
                        llEmpty.setVisibility(View.GONE);
                        ReservasAdapter adapter = new ReservasAdapter(mCtx,reservas);
                        lvReservas.setAdapter(adapter);
                    }else
                        llEmpty.setVisibility(View.VISIBLE);



                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                reservas = rs.getAgendamentos(App.getUser(getApplicationContext()).getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


}
