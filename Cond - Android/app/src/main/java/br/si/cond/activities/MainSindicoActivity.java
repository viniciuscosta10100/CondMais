package br.si.cond.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.si.cond.R;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.ImageUtils;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainSindicoActivity extends ActionBarActivity {


    User usuario;
    LinearLayout btnMensagens;
    LinearLayout btnAgenda;
    LinearLayout btnAvisos;
    LinearLayout llPerfil;
    Button btnSair;
    ImageView imgCondominio;
    TextView lblNome;
    TextView lblCondominio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sindico);
        lblNome = (TextView)findViewById(R.id.lblNome);
        lblCondominio = (TextView)findViewById(R.id.lblCondominio);
        imgCondominio = (ImageView)findViewById(R.id.imgCondominio);
        btnAgenda = (LinearLayout)findViewById(R.id.btnAgenda);
        btnMensagens = (LinearLayout)findViewById(R.id.btnMensagens);
        btnAvisos = (LinearLayout)findViewById(R.id.btnAvisos);
        llPerfil = (LinearLayout)findViewById(R.id.llPerfil);
        btnSair = (Button)findViewById(R.id.btnSair);

        usuario = App.getUser(getApplicationContext());

        btnAgenda.setOnTouchListener(touch);
        btnMensagens.setOnTouchListener(touch);
        btnAvisos.setOnTouchListener(touch);


        lblNome.setText(usuario.getNome().toUpperCase());
        lblCondominio.setText(usuario.getCondominio().getNome().toUpperCase());

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

        llPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PerfilActivity.class));
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ActionTask actionLoadGames = new ActionTask(MainSindicoActivity.this, logoutAction, "Saindo..");
                actionLoadGames.execute();
            }
        });

    }

    View.OnTouchListener touch =new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            Integer tag = Integer.valueOf(v.getTag().toString());
            ImageView icon = (ImageView)v.findViewById(R.id.icon);
            TextView lbl = (TextView) v.findViewById(R.id.text);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lbl.setTextColor(getResources().getColor(R.color.color_principal));
                switch (tag){
                    case 1:
                        icon.setImageResource(R.drawable.ic_mural_hover);
                        break;
                    case 2:
                        icon.setImageResource(R.drawable.ic_message_hover);
                        break;
                    case 3:
                        icon.setImageResource(R.drawable.ic_agenda_hover);
                        break;
                    case 4:
                        icon.setImageResource(R.drawable.ic_chamado_hover);
                        break;
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                lbl.setTextColor(getResources().getColor(R.color.color_text));
                switch (tag){
                    case 1:
                        icon.setImageResource(R.drawable.ic_mural);
                        startActivity(new Intent(getApplicationContext(),MuralActivity.class));
                        break;
                    case 2:
                        icon.setImageResource(R.drawable.ic_message);
                        startActivity(new Intent(getApplicationContext(),MensagensActivity.class));
                        break;
                    case 3:
                        icon.setImageResource(R.drawable.ic_agenda);
                        startActivity(new Intent(getApplicationContext(),ReservasActivity.class));
                        break;
                    case 4:
                        icon.setImageResource(R.drawable.ic_chamado);
                        startActivity(new Intent(getApplicationContext(),ReivindicacoesActivity.class));
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            }

            return false;
        }
    };
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
}
