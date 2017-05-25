package br.si.cond.activities;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.adapters.AvisosAdapter;
import br.si.cond.adapters.ReservasAdapter;
import br.si.cond.model.Agendamento;
import br.si.cond.model.Aviso;
import br.si.cond.model.ComentarioReinvidicacao;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class MuralActivity extends ActionBarActivity {
    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    Dialog dialog;
    LinearLayout llEmpty;
    ListView lvAvisos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mural);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llEmpty = (LinearLayout)findViewById(R.id.llEmpty);
        lvAvisos = (ListView)findViewById(R.id.lvAvisos);

        findViewById(R.id.btnNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        if(App.getUser(getApplicationContext()).getTipo().equalsIgnoreCase("S"))
            findViewById(R.id.btnNew).setVisibility(View.VISIBLE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("refresh_activity"));

        ActionTask actionLoadGames = new ActionTask(MuralActivity.this, agendamentosAction, "Buscando avisos..");
        actionLoadGames.execute();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            ActionTask actionLoadGames = new ActionTask(MuralActivity.this, agendamentosAction, "aguarde..");
            actionLoadGames.execute();

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

    Action agendamentosAction = new Action() {
        ArrayList<Aviso> avisos = new ArrayList<Aviso>();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(avisos.size()>0) {
                        llEmpty.setVisibility(View.GONE);
                        AvisosAdapter adapter = new AvisosAdapter(mCtx,avisos);
                        lvAvisos.setAdapter(adapter);
                        lvAvisos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Aviso aviso = avisos.get(position);
                                if(App.getUser(getApplicationContext()).getTipo().equalsIgnoreCase("S"))
                                    showDialogMenu(aviso);

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

                User userLogged=App.getUser(getApplicationContext());
                avisos = rs.getAvisos(userLogged.getCondominio().getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Action sendAviso = new Action() {
        ResultServer result = new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {

                if(result.getResult().equalsIgnoreCase("true")){
                    Utils.showAlert("Aviso","Aviso enviado com sucesso.",mCtx);
                    dialog.dismiss();
                }else{
                    Toast.makeText(mCtx,"Erro ao enviar aviso, tente novamente em instantes",Toast.LENGTH_LONG);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                User userLogged=App.getUser(getApplicationContext());
                result = rs.sendAviso(param,userLogged.getId(),userLogged.getCondominio().getId());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    Action resendAviso = new Action() {
        ResultServer result = new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {

                if(result.getResult().equalsIgnoreCase("true")){
                    Utils.showAlert("Aviso","Aviso re-enviado com sucesso.",mCtx);
                    dialog.dismiss();

                    ActionTask actionLoadGames = new ActionTask(MuralActivity.this, agendamentosAction, "Buscando avisos..");
                    actionLoadGames.execute();
                }else{
                    Toast.makeText(mCtx,"Erro ao re-enviar aviso, tente novamente em instantes",Toast.LENGTH_LONG);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                Long idAviso = Long.parseLong(param);
                result = rs.resendAviso(idAviso);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    Action removeAvisoAction = new Action() {
        ResultServer result = new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {

                if(result.getResult().equalsIgnoreCase("true")){
                    Utils.showAlert("Aviso","Aviso removido com sucesso.",mCtx);
                    dialog.dismiss();

                    ActionTask actionLoadGames = new ActionTask(MuralActivity.this, agendamentosAction, "Buscando avisos..");
                    actionLoadGames.execute();
                }else{
                    Toast.makeText(mCtx,"Erro ao remover aviso, tente novamente em instantes",Toast.LENGTH_LONG);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                Long idAviso = Long.parseLong(param);
                result = rs.desativarAviso(idAviso);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void showDialog(){
        dialog= new Dialog(MuralActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_novo_aviso);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        final EditText txtMensagem = (EditText) dialog.findViewById(R.id.txtMsg);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        Button btnSalvar = (Button)dialog.findViewById(R.id.btnSalvar);


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
                    Toast.makeText(getApplicationContext(), "Informe um aviso.", Toast.LENGTH_SHORT).show();
                }

                if(mensagemOk){
                    ActionTask actionLoadGames = new ActionTask(MuralActivity.this, sendAviso, "Enviando aviso..");
                    sendAviso.param = txtMensagem.getText().toString();
                    actionLoadGames.execute();
                }
            }
        });



        dialog.show();
    }

    private void showDialogMenu(final Aviso aviso){
        dialog= new Dialog(MuralActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_menu_aviso);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        final TextView lblMensagem = (TextView) dialog.findViewById(R.id.lblMensagem);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        Button btnResend = (Button)dialog.findViewById(R.id.btnResend);
        Button btnRemover = (Button)dialog.findViewById(R.id.btnRemover);

        lblMensagem.setText(aviso.getMensagem());

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnResend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ActionTask actionLoadGames = new ActionTask(MuralActivity.this, resendAviso, "Re-enviando aviso..");
                resendAviso.param = aviso.getId().toString();
                actionLoadGames.execute();
            }
        });

        btnRemover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ActionTask actionLoadGames = new ActionTask(MuralActivity.this, removeAvisoAction, "Removendo aviso..");
                removeAvisoAction.param = aviso.getId().toString();
                actionLoadGames.execute();
            }
        });



        dialog.show();
    }
}
