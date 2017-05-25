package br.si.cond.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.si.cond.R;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class NovaReivindicacaoActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 1;
    private Reinvidicacao reivindicacao;

    ImageView imgReivindicacao;
    EditText txtMensagem;
    Button btnSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_reivindicacao);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        imgReivindicacao = (ImageView)findViewById(R.id.imgReinvidicacao);
        txtMensagem = (EditText)findViewById(R.id.txtMensagem);
        btnSalvar = (Button)findViewById(R.id.btnSalvar);

        imgReivindicacao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Boolean fotoOk=false;
                Boolean mensagemOk=false;

                if(reivindicacao.getFoto()!= null && reivindicacao.getFoto().length()>4){
                    fotoOk=true;
                }else{
                    fotoOk=false;
                    Toast.makeText(getApplicationContext(), "Selecione uma foto.", Toast.LENGTH_SHORT).show();
                }

                if(txtMensagem.getText().toString().length()>4){
                    mensagemOk=true;
                }else{
                    mensagemOk=false;
                    Toast.makeText(getApplicationContext(), "Informe uma mensagem.", Toast.LENGTH_SHORT).show();
                }

                if(fotoOk && mensagemOk){
                    User userLogged=App.getUser(getApplicationContext());

                    reivindicacao.setUser_id(userLogged.getId());
                    reivindicacao.setCondominio_id(userLogged.getCondominio().getId());
                    reivindicacao.setMensagem(txtMensagem.getText().toString());

                    if(userLogged.getTipo().equalsIgnoreCase("M"))
                        reivindicacao.setStatus("S");
                    else if(userLogged.getTipo().equalsIgnoreCase("S"))
                        reivindicacao.setStatus("A");

                    ActionTask actionLoadGames = new ActionTask(NovaReivindicacaoActivity.this, saveAction, "Enviando reivindicações..");
                    actionLoadGames.execute();
                }
            }
        });

        reivindicacao = new Reinvidicacao();
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

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps

                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                String fotoUri = data.getDataString();
                Uri foto = Uri.parse(fotoUri);
                ExifInterface ei = new ExifInterface(foto.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bitmap, 180);
                        break;
                    // etc.
                }


                int height = bitmap.getHeight()*250/bitmap.getWidth();
                Bitmap bResized = Bitmap.createScaledBitmap(bitmap, 250, height, true);
                String fotoStr = Utils.bitmapToBase64(bResized);
                stream.close();



                reivindicacao.setFoto(fotoStr);
                imgReivindicacao.setImageBitmap(bResized);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private Bitmap rotateImage(Bitmap bitmap,float rotate){
        Matrix matrix = new Matrix();
        matrix.preRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
                        Utils.showAlert("Reivindicação","Reivindicação enviada com sucesso!",mCtx,NovaReivindicacaoActivity.this);
                    }else{
                        Toast.makeText(getApplicationContext(),"Erro ao enviar reivindicação, tente novamente em instantes.",Toast.LENGTH_LONG).show();
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

                resultado = rs.saveReivindicacao(reivindicacao);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
