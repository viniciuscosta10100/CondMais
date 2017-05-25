package br.si.cond.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.si.cond.R;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.Mask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;

public class AlterarActivity  extends ActionBarActivity {

    private static final int REQUEST_CODE = 1;

    private final String[] sexos_selected={"","F","M"};

    User usuario;
    EditText txtNome;
    EditText txtTelefone;
    Spinner spnSexo;
    ImageView imgPerfil;
    Button btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = App.getUser(getApplicationContext());
        setContentView(R.layout.activity_atualizar);
        txtNome = (EditText)findViewById(R.id.edtNome);
        txtTelefone = (EditText)findViewById(R.id.edtTelefone);
        spnSexo = (Spinner)findViewById(R.id.spnSexo);
        imgPerfil = (ImageView)findViewById(R.id.imgPerfil);
        btnEditar = (Button)findViewById(R.id.btnAlterar);
        preencherCampos();
    }

    private void preencherCampos(){
        txtTelefone.addTextChangedListener(Mask.insert("(##)#####-####", txtTelefone));

        txtNome.setText(usuario.getNome());
        txtTelefone.setText(usuario.getTelefone());

        final String[] sexos={"Sexo","Feminino","Masculino"};

        ArrayAdapter<String> adapterFiltros = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,sexos);
        spnSexo.setAdapter(adapterFiltros);
        for (int i = 0; i < spnSexo.getCount(); i++) {
            if (spnSexo.getItemAtPosition(i).equals(usuario.getSexo())) {
                spnSexo.setSelection(i);
                break;
            }
        }

        imgPerfil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pickImage();
            }
        });
        imgPerfil.setImageBitmap(Utils.base64ToBitmap(usuario.getFoto()));
        definirEventoAlterar();
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


                int height = bitmap.getHeight()*150/bitmap.getWidth();
                Bitmap bResized = Bitmap.createScaledBitmap(bitmap, 150, height, true);
                String fotoStr = Utils.bitmapToBase64(bResized);
                stream.close();



                usuario.setFoto(fotoStr);
                imgPerfil.setImageBitmap(bResized);
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

    private void definirEventoAlterar(){
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean nomeOk;
                Boolean telefoneOk;
                Boolean sexoOk;
                Boolean fotoOk;

                String sexo = sexos_selected[spnSexo.getSelectedItemPosition()];

                if(sexo.length()>0){
                    sexoOk=true;
                }else{
                    sexoOk=false;
                    Toast.makeText(getApplicationContext(),"Selecione o sexo",Toast.LENGTH_SHORT).show();
                }
                if(usuario.getFoto()!=null){
                    fotoOk=true;
                }else{
                    fotoOk=false;
                    Toast.makeText(getApplicationContext(),"Selecione uma foto.",Toast.LENGTH_SHORT).show();
                }

                if(txtNome.getText().toString().length()>4){
                    nomeOk=true;
                }else{
                    nomeOk=false;
                    Toast.makeText(getApplicationContext(),"Digite seu nome completo",Toast.LENGTH_SHORT).show();
                }
                if(txtTelefone.getText().toString().length()>8){
                    telefoneOk=true;
                }else{
                    telefoneOk=false;
                    Toast.makeText(getApplicationContext(),"Digite o seu telefone",Toast.LENGTH_SHORT).show();
                }



                if(fotoOk && nomeOk && telefoneOk && sexoOk) {
                    usuario.setNome(txtNome.getText().toString());
                    usuario.setTelefone(txtTelefone.getText().toString());
                    usuario.setSexo(sexo);
                    ActionTask actionLoadGames = new ActionTask(AlterarActivity.this, alterarAction, "aguarde..");
                    actionLoadGames.execute();
                }
            }
        });
    }

    Action alterarAction = new Action() {
        Boolean resultado;
        @Override
        public void onPostExecute() {
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(resultado){
                        Utils.showAlert("Sucesso","Dados alterados com sucesso!",mCtx,AlterarActivity.this);
                        App.saveUser(mCtx,usuario);

                    }else{
                        Toast.makeText(getApplicationContext(),"Erro ao alterar. Verifique se preencheu todos os campos e tente novamente.",Toast.LENGTH_LONG).show();
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

                resultado = rs.update(usuario);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
