package br.si.cond.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.adapters.ReivindicacoesAdapter;
import br.si.cond.adapters.ReservasAdapter;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class ReivindicacoesActivity extends ActionBarActivity {
    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();


    LinearLayout llEmpty;
    ListView lvReinvidicacoes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reinvidicacoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llEmpty = (LinearLayout)findViewById(R.id.llEmpty);
        lvReinvidicacoes = (ListView)findViewById(R.id.lvReinvidicacoes);

        findViewById(R.id.btnNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NovaReivindicacaoActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


        ActionTask actionLoadGames = new ActionTask(ReivindicacoesActivity.this, reinvidicacoesAction, "Buscando reivindicações..");
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

    Action reinvidicacoesAction = new Action() {
        ArrayList<Reinvidicacao> reinvidicacoes = new ArrayList<Reinvidicacao>();
        @Override
        public void onPostExecute() {
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(reinvidicacoes.size()>0) {
                        llEmpty.setVisibility(View.GONE);
                        ReivindicacoesAdapter adapter = new ReivindicacoesAdapter(mCtx,reinvidicacoes);
                        lvReinvidicacoes.setAdapter(adapter);

                        lvReinvidicacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Reinvidicacao reivindicacao = reinvidicacoes.get(position);

                                Intent it = new Intent(getApplicationContext(),ReivindicacaoActivity.class);
                                Bundle bundle=new Bundle();

                                bundle.putLong("id_reivindicacao",reivindicacao.getId());

                                it.putExtras(bundle);

                                startActivity(it);
                            }
                        });
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

                reinvidicacoes = rs.getReinvidicacoes(App.getUser(getApplicationContext()).getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
