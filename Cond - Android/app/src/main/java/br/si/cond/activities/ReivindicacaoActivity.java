package br.si.cond.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import br.si.cond.R;
import br.si.cond.adapters.AvisosAdapter;
import br.si.cond.adapters.ComentariosAdapter;
import br.si.cond.adapters.ReivindicacoesAdapter;
import br.si.cond.model.ComentarioReinvidicacao;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class ReivindicacaoActivity extends ActionBarActivity {

    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    Dialog dialog;
    private Reinvidicacao reivindicacao;

    ImageView imgReinvidicacao;
    TextView lblMensagem;
    ListView lvComentarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reivindicacao);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        lblMensagem = (TextView)findViewById(R.id.lblMensagem);
        imgReinvidicacao = (ImageView)findViewById(R.id.imgReinvidicacao);
        lvComentarios = (ListView)findViewById(R.id.lvComentarios);

        reivindicacao = new Reinvidicacao();

        if(getIntent().getExtras()!=null){
            Long id_reivindicacao = getIntent().getExtras().getLong("id_reivindicacao");

            reivindicacao = new Reinvidicacao();
            reivindicacao.setId(id_reivindicacao);




            ActionTask actionLoadGames = new ActionTask(ReivindicacaoActivity.this, reinvidicacaoAction, "Aguarde..");
            actionLoadGames.execute();
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
            String id_reivindicacao = intent.getStringExtra("message");

            long idReivindicacao = Long.valueOf(id_reivindicacao);
            if(idReivindicacao == reivindicacao.getId()) {
                ActionTask actionLoadGames = new ActionTask(ReivindicacaoActivity.this, reinvidicacaoAction, "Aguarde..");
                actionLoadGames.execute();
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reivindicacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ac_interacao:
                    showDialog();

                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void showDialog(){
        dialog= new Dialog(ReivindicacaoActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_comentario);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        final EditText txtMensagem = (EditText) dialog.findViewById(R.id.txtMsg);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        Button btnSalvar = (Button)dialog.findViewById(R.id.btnSalvar);
        final CheckBox checkConcluido = (CheckBox)dialog.findViewById(R.id.checkConcluido);


        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Boolean mensagemOk=false;

                if(txtMensagem.getText().toString().length()>4){
                    mensagemOk=true;
                }else{
                    mensagemOk=false;
                    Toast.makeText(getApplicationContext(), "Informe uma mensagem.", Toast.LENGTH_SHORT).show();
                }

                if(mensagemOk){
                    User user = App.getUser(getApplicationContext());

                    ComentarioReinvidicacao comentario = new ComentarioReinvidicacao();
                    comentario.setMensagem(txtMensagem.getText().toString());
                    comentario.setReinvidicacao_id(reivindicacao.getId());
                    comentario.setUser_id(user.getId());
                    comentario.setConcluido(checkConcluido.isChecked());


                    ActionTask actionLoadGames = new ActionTask(ReivindicacaoActivity.this, saveAction, "Enviando resposta..");
                    saveAction.param = gson.toJson(comentario);
                    actionLoadGames.execute();
                }
            }
        });



        dialog.show();
    }

    Action saveAction = new Action() {
        ResultServer resultado;
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(resultado.getResult().equalsIgnoreCase("true")){
                        Utils.showAlert("Atenção","Resposta enviada com sucesso!",mCtx,ReivindicacaoActivity.this);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(),"Erro ao enviar resposta, tente novamente em instantes.",Toast.LENGTH_LONG).show();
                    }

                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                ComentarioReinvidicacao comentario = gson.fromJson(param,ComentarioReinvidicacao.class);
                resultado = rs.saveComentarioReivindicacao(comentario);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Action reinvidicacaoAction = new Action() {
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    lblMensagem.setText(reivindicacao.getMensagem());

                    byte[] decodedString = Base64.decode(reivindicacao.getFoto(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgReinvidicacao.setImageBitmap(decodedByte);

                    ComentariosAdapter adapter = new ComentariosAdapter(mCtx,reivindicacao.getComentarios());
                    lvComentarios.setAdapter(adapter);



                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                reivindicacao = rs.getReinvidicacao(reivindicacao.getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
