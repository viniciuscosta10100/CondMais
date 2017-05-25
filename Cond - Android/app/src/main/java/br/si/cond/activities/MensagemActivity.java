package br.si.cond.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import br.si.cond.R;
import br.si.cond.adapters.MensagensAdapter;
import br.si.cond.adapters.RecentesAdapter;
import br.si.cond.model.Mensagem;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.IInfiniteScrollListener;
import br.si.cond.utils.InfiniteScrollAdapter;
import br.si.cond.utils.InfiniteScrollListView;
import br.si.cond.utils.InfiniteScrollOnScrollListener;
import br.si.cond.utils.LoadTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class MensagemActivity extends ActionBarActivity implements IInfiniteScrollListener{

    Mensagem novaMensagem;

    Boolean sending=false;
    Long idVizinho;
    MensagensAdapter adapter;
    ArrayList<Mensagem> mensagens= new ArrayList<Mensagem>();

    InfiniteScrollListView lvMensagens;
    InfiniteScrollOnScrollListener scrollListener;
    EditText edtMsg;
    ImageButton btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lvMensagens = (InfiniteScrollListView)findViewById(R.id.lvMensagens);
        edtMsg = (EditText)findViewById(R.id.edtMsg);
        btnSend = (ImageButton)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(!sending) {
                    sending=true;
                    User vizinho = new User();
                    vizinho.setId(idVizinho);
                    novaMensagem = new Mensagem();
                    novaMensagem.setDestinatario(vizinho);
                    novaMensagem.setCreated_at(new Date());
                    novaMensagem.setRemetente(App.getUser(getApplicationContext()));
                    novaMensagem.setTexto(edtMsg.getText().toString());

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);

                    ((InfiniteScrollAdapter) lvMensagens.getAdapter()).setDoneLoadingFalse();

                    LoadTask sendMsgTask = new LoadTask(sendMsgAction);
                    sendMsgTask.execute();
                }

            }
        });

        scrollListener = new InfiniteScrollOnScrollListener(this);

        lvMensagens.setListener(scrollListener);
        adapter= new MensagensAdapter(getApplicationContext());
        lvMensagens.setAdapter(adapter);
        ((InfiniteScrollAdapter)lvMensagens.getAdapter()).setDoneLoading();

        if(getIntent().getExtras()!=null){
            String nome =getIntent().getExtras().getString("nome");
            idVizinho =getIntent().getExtras().getLong("idVizinho");

            getSupportActionBar().setTitle(nome);

            ActionTask recentesTask = new ActionTask(MensagemActivity.this,mensagensAction,"Carregando Conversa..");
            recentesTask.execute();
        }

    }

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
            String id_remetente = intent.getStringExtra("remetente_id");

            if(user_id.equalsIgnoreCase(App.getUser(getApplicationContext()).getId().toString())){
                if(id_remetente.equalsIgnoreCase(idVizinho.toString())) {
                    LoadTask recentesTask = new LoadTask(mensagensAction);
                    recentesTask.execute();
                }

            }



        }
    };

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

    Action mensagensAction = new Action() {
        ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {


                    lvMensagens.clearItems();
                    lvMensagens.appendItems(mensagens);
                    ((InfiniteScrollAdapter)lvMensagens.getAdapter()).setDoneLoading();


                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                mensagens = rs.getMensagens(App.getUser(getApplicationContext()).getId(), idVizinho);
                Collections.reverse(mensagens);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Action sendMsgAction = new Action() {
        ResultServer result=new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {

                if(result.getResult().equalsIgnoreCase("true")){
                    ArrayList<Mensagem> msgList = new ArrayList<Mensagem>();
                    msgList.add(novaMensagem);
                    lvMensagens.appendItems(msgList);

                    edtMsg.setText("");
                    ((InfiniteScrollAdapter)lvMensagens.getAdapter()).setDoneLoading();

                }else{
                    ((InfiniteScrollAdapter)lvMensagens.getAdapter()).setDoneLoading();
                    Toast.makeText(getApplicationContext(),"Erro ao enviar mensagem, tente em instantes.",Toast.LENGTH_LONG).show();
                }



            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                result=rs.sendMessage(novaMensagem);
                sending=false;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    @Override
    public void endIsNear() {

    }

    @Override
    public void onScrollCalled(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
