package br.si.cond.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import br.si.cond.R;
import br.si.cond.model.User;
import br.si.cond.utils.ImageUtils;
import br.si.cond.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends ActionBarActivity {

    User usuario;
    LinearLayout btnAgenda;
    LinearLayout btnMensagens;
    LinearLayout btnAvisos;
    LinearLayout btnEnquetes;
    LinearLayout llPerfil;

    CircleImageView imgPerfil;
    ImageView imgCondominio;
    TextView lblNome;
    TextView lblCondominio;
    TextView lblBloco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        init();
    }

    private void init(){
        lblNome = (TextView)findViewById(R.id.lblNome);
        lblCondominio = (TextView)findViewById(R.id.lblCondominio);
        lblBloco = (TextView)findViewById(R.id.lblBloco);
        imgPerfil = (CircleImageView)findViewById(R.id.imgPerfil);
        imgCondominio = (ImageView)findViewById(R.id.imgCondominio);
        btnAgenda = (LinearLayout)findViewById(R.id.btnAgenda);
        btnMensagens = (LinearLayout)findViewById(R.id.btnMensagens);
        btnAvisos = (LinearLayout)findViewById(R.id.btnAvisos);
        btnEnquetes = (LinearLayout)findViewById(R.id.btnEnquetes);
        llPerfil = (LinearLayout)findViewById(R.id.llPerfil);

        usuario = App.getUser(getApplicationContext());

        btnAgenda.setOnTouchListener(touch);
        btnMensagens.setOnTouchListener(touch);
        btnAvisos.setOnTouchListener(touch);
        btnEnquetes.setOnTouchListener(touch);

        imgPerfil.setImageBitmap(Utils.base64ToBitmap(usuario.getFoto()));

        lblNome.setText(usuario.getNome().toUpperCase());
        lblCondominio.setText(usuario.getCondominio().getNome().toUpperCase());
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

        llPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PerfilActivity.class));
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
}
