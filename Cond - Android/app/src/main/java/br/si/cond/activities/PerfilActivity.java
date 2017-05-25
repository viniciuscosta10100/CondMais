package br.si.cond.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.si.cond.R;
import br.si.cond.adapters.DependenteAdapter;
import br.si.cond.adapters.VizinhosAdapter;
import br.si.cond.model.ComentarioReinvidicacao;
import br.si.cond.model.Mensagem;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.ImageUtils;
import br.si.cond.utils.InfiniteScrollAdapter;
import br.si.cond.utils.Mask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends ActionBarActivity {

    Dialog dialog;
    ArrayList<User> dependentes = new ArrayList<User>();
    CircleImageView imgPerfil;
    ImageView imgCondominio;
    TextView lblNome;
    TextView lblCondominio;
    TextView lblBloco;
    Button btnSair;
    Button btnNovoMembro;
    Button btnAlterar;
    User usuario;

    GridView gdMoradores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        init();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        init();
    }

    private void init(){
        gdMoradores = (GridView)findViewById(R.id.gdMoradores);
        lblNome = (TextView)findViewById(R.id.lblNome);
        lblCondominio = (TextView)findViewById(R.id.lblCondominio);
        lblBloco = (TextView)findViewById(R.id.lblBloco);
        imgPerfil = (CircleImageView)findViewById(R.id.imgPerfil);
        imgCondominio = (ImageView)findViewById(R.id.imgCondominio);
        btnSair = (Button)findViewById(R.id.btnSair);
        btnNovoMembro = (Button)findViewById(R.id.btnNovoMembro);
        btnAlterar = (Button)findViewById(R.id.buttonAlterar);

        usuario = App.getUser(getApplicationContext());

        if(usuario.getFoto() != null)
            imgPerfil.setImageBitmap(Utils.base64ToBitmap(usuario.getFoto()));
        dependentes = new ArrayList<User>();

        lblNome.setText(usuario.getNome().toUpperCase());
        lblCondominio.setText(usuario.getCondominio().getNome().toUpperCase());
        if(usuario.getResidencia() != null && usuario.getResidencia().getBloco() != null)
            lblBloco.setText(usuario.getResidencia().getBloco().getNome().toUpperCase() + " - " + usuario.getResidencia().getNumero().toUpperCase());

        new Thread() {
            @Override
            public void run() {
                Bitmap bit = Utils.getBitmap(usuario.getCondominio().getFotoUrl(), getApplicationContext());
                final Bitmap bitmapOriginal= bit;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Bitmap bitmap = ImageUtils.fastblur(bitmapOriginal, 10);
                        imgCondominio.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();

        btnSair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ActionTask actionLoadGames = new ActionTask(PerfilActivity.this, logoutAction, "Saindo..");
                actionLoadGames.execute();
            }
        });
        btnNovoMembro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(dependentes.size()<=5){
                    showDialogNovoMembro();
                }else{
                    Toast.makeText(getApplicationContext(),"Limite maximo de 5 membros",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAlterar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(),AlterarActivity.class));
            }
        });

        ActionTask actionLoadGames = new ActionTask(PerfilActivity.this, dependentesAction, "aguarde..");
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

    Action dependentesAction = new Action() {

        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    DependenteAdapter adapter = new DependenteAdapter(getApplicationContext(),dependentes);
                    gdMoradores.setAdapter(adapter);


                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {
                User userLogged=App.getUser(getApplicationContext());
                RestServices rs = new RestServices();

                dependentes = rs.getMoradores(userLogged.getResidencia().getId());

                ArrayList<User> novoDependentes = dependentes;
                dependentes = new ArrayList<User>();

                for(User usr : novoDependentes){
                    if(userLogged.getId()!=usr.getId())
                        dependentes.add(usr);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    Action logoutAction = new Action() {

        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                App.logout(getApplicationContext());
                Intent it = new Intent(getApplicationContext(),LoginActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {
                User userLogged=App.getUser(getApplicationContext());
                RestServices rs = new RestServices();

                rs.logout(userLogged.getId());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void showDialogNovoMembro(){
        dialog= new Dialog(PerfilActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_novo_dependente);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        final EditText txtCpf = (EditText) dialog.findViewById(R.id.txtCpf);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        Button btnSalvar = (Button)dialog.findViewById(R.id.btnSalvar);

        txtCpf.addTextChangedListener(Mask.insert("###.###.###-##", txtCpf));
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String cpf = txtCpf.getText().toString().replaceAll("\\.", "");
                cpf = cpf.replace("-","");
                Boolean cpfOk=false;
                if(Utils.isCPF(cpf)){
                    cpfOk=true;
                }else{
                    cpfOk=false;
                    Toast.makeText(getApplicationContext(), "Preencha um cpf valido", Toast.LENGTH_SHORT).show();
                }

                if(cpfOk) {
                    ActionTask actionLoadGames = new ActionTask(PerfilActivity.this, saveMembroAction, "Aguarde..");
                    saveMembroAction.param = txtCpf.getText().toString();
                    actionLoadGames.execute();
                }
            }
        });



        dialog.show();
    }


    Action saveMembroAction = new Action() {
        ResultServer result=new ResultServer();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {

                if(result.getResult().equalsIgnoreCase("true")){
                    Utils.showAlert("Novo Membro","Membro convidado com sucesso. O mesmo deve realizar o cadastro pelo aplicativo.",mCtx,PerfilActivity.this);

                }else{
                    Toast.makeText(getApplicationContext(),"Erro ao convidar membro.",Toast.LENGTH_LONG).show();
                }



            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                RestServices rs = new RestServices();

                result=rs.saveMembro(param,App.getUser(mCtx).getId().toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
