package br.si.cond.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.adapters.RecentesAdapter;
import br.si.cond.adapters.ReivindicacoesAdapter;
import br.si.cond.adapters.VizinhosAdapter;
import br.si.cond.model.Mensagem;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.LoadTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;
import info.hoang8f.android.segmented.SegmentedGroup;

public class MensagensActivity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener {

    ListView lvMensagens;

    int mode=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_mensagens);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);

        lvMensagens =(ListView) findViewById(R.id.lvMensagens);
        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.segmented2);
        segmented2.setTintColor(Color.parseColor("#FFFFFF"),Color.parseColor("#ffc900"));

        segmented2.setOnCheckedChangeListener(this);


        ((RadioButton) findViewById(R.id.button21)).setChecked(true);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.button21:
                showRecentes();
                return;
            case R.id.button22:
                showVizinhos();
                return;

        }
    }

    private void showRecentes(){
        mode=1;
        setSupportProgressBarIndeterminateVisibility(true);

        ActionTask recentesTask = new ActionTask(MensagensActivity.this,recentesAction,"Carregando Conversas");
        recentesTask.execute();
    }
    private void showVizinhos(){
        mode=2;
        ActionTask vizinhosTask = new ActionTask(MensagensActivity.this,vizinhosAction,"Carregando Vizinhos");
        vizinhosTask.execute();
    }


    Action recentesAction = new Action() {
        ArrayList<Mensagem> recentes = new ArrayList<Mensagem>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    RecentesAdapter adapter = new RecentesAdapter(getApplicationContext(),recentes);
                    lvMensagens.setAdapter(adapter);

                    lvMensagens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Mensagem mensagem = recentes.get(position);
                            Intent it = new Intent(getApplicationContext(),MensagemActivity.class);
                            Bundle bundle=new Bundle();
                            if(App.getUser(getApplicationContext()).getId()==mensagem.getRemetente().getId()) {
                                bundle.putLong("idVizinho", mensagem.getDestinatario().getId());
                                bundle.putString("nome",mensagem.getDestinatario().getNome());
                            }else {
                                bundle.putLong("idVizinho", mensagem.getRemetente().getId());
                                bundle.putString("nome",mensagem.getRemetente().getNome());
                            }


                            it.putExtras(bundle);

                            startActivity(it);
                        }
                    });


                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                recentes = rs.getMensagensRecentes(App.getUser(getApplicationContext()).getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Action vizinhosAction = new Action() {
        ArrayList<User> vizinhos = new ArrayList<User>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    VizinhosAdapter adapter = new VizinhosAdapter(getApplicationContext(),vizinhos);
                    lvMensagens.setAdapter(adapter);

                    lvMensagens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            User user = vizinhos.get(position);


                            Intent it = new Intent(getApplicationContext(),MensagemActivity.class);
                            Bundle bundle=new Bundle();

                            bundle.putLong("idVizinho",user.getId());
                            bundle.putString("nome",user.getNome());

                            it.putExtras(bundle);

                            startActivity(it);
                        }
                    });


                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                vizinhos = rs.getVizinhos(App.getUser(getApplicationContext()).getCondominio().getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("refresh_activity"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String user_id = intent.getStringExtra("user_id");

            if(user_id.equalsIgnoreCase(App.getUser(getApplicationContext()).getId().toString())){
                if(mode==1) {
                    LoadTask recentesTask = new LoadTask(recentesAction);
                    recentesTask.execute();
                }

            }



        }
    };
}
