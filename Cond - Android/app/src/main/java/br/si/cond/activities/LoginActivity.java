package br.si.cond.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import br.si.cond.R;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class LoginActivity extends ActionBarActivity {

    EditText edtEmail;
    EditText edtSenha;

    Button btnEntrar;
    Button btnCadastrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtSenha =(EditText)findViewById(R.id.edtSenha);
        edtEmail =(EditText)findViewById(R.id.edtEmail);

        btnEntrar =(Button)findViewById(R.id.btnEntrar);
        btnCadastrar =(Button)findViewById(R.id.btnCadastrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionTask actionLoadGames = new ActionTask(LoginActivity.this, loginAction, "Autenticando..");
                actionLoadGames.execute();

            }
        });
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),CadastroActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = App.getUser(getApplicationContext());

        if(user!=null){
            if(user.getTipo().equalsIgnoreCase("M"))
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            else
                startActivity(new Intent(getApplicationContext(), MainSindicoActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Action loginAction = new Action() {
        User user = new User();
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(user.getId()!=null){
                        App.saveUser(mCtx,user);
                        Toast.makeText(mCtx, "Bem vindo(a) " + user.getNome(), Toast.LENGTH_LONG).show();
                        if(user.getTipo().equalsIgnoreCase("M"))
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), MainSindicoActivity.class));
                    }else{
                        Toast.makeText(mCtx,"Credenciais invalidas",Toast.LENGTH_LONG).show();
                    }

//                    resizeListView(lvAulas);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
        @Override
        public void run () {
            try {

                String regid;
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regid = gcm.register("906062816451");
                Log.i("GCM", "Registration id: " + regid);

                RestServices rs = new RestServices();

                user = rs.auth(edtEmail.getText().toString(), edtSenha.getText().toString(),regid);


            } catch (Exception ex) {
                ex.printStackTrace();
                user.setId(null);
            }
        }
    };
}
