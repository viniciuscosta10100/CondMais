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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.si.cond.R;
import br.si.cond.model.Residencia;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;
import br.si.cond.utils.Action;
import br.si.cond.utils.ActionTask;
import br.si.cond.utils.ImageUtils;
import br.si.cond.utils.Mask;
import br.si.cond.utils.Utils;
import br.si.cond.ws.RestServices;
import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 1;

    User usuario;
    EditText edtCpf;
    Button btnVerify;

    EditText edtNome;
    EditText edtTelefone;
    EditText edtEmail;
    EditText edtSenha;
    EditText edtConfirmacao;
    ImageView imgPerfil;
    Button btnCadastrar;
    Spinner spnSexo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = new User();
        verifyCpf();
    }


    private void cadastro(){
        setContentView(R.layout.activity_cadastro);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);
        imgPerfil = (ImageView)findViewById(R.id.imgPerfil);
        edtNome = (EditText)findViewById(R.id.edtNome);
        edtTelefone = (EditText)findViewById(R.id.edtTelefone);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtCpf = (EditText)findViewById(R.id.edtCpf);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
        edtConfirmacao = (EditText)findViewById(R.id.edtSenhaConfirmacao);

        edtTelefone.addTextChangedListener(Mask.insert("(##)#####-####", edtTelefone));
        imgPerfil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                pickImage();
            }
        });
        spnSexo = (Spinner)findViewById(R.id.spnSexo);


        final String[] sexos={"Sexo","Feminino","Masculino"};
        final String[] sexos_selected={"","F","M"};

        ArrayAdapter<String> adapterFiltros = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,sexos);
        spnSexo.setAdapter(adapterFiltros);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean nomeOk=false;
                Boolean telefoneOk=false;
                Boolean sexoOk=false;
                Boolean emailOk=false;
                Boolean senhaOk=false;
                Boolean senhaSizeOk=false;
                Boolean fotoOk=false;

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

                if(edtNome.getText().toString().length()>4){
                    nomeOk=true;
                }else{
                    nomeOk=false;
                    Toast.makeText(getApplicationContext(),"Digite seu nome completo",Toast.LENGTH_SHORT).show();
                }
                if(edtTelefone.getText().toString().length()>8){
                    telefoneOk=true;
                }else{
                    telefoneOk=false;
                    Toast.makeText(getApplicationContext(),"Digite o seu telefone",Toast.LENGTH_SHORT).show();
                }
                if(edtEmail.getText().toString().length()>4){
                    emailOk=true;
                }else{
                    emailOk=false;
                    Toast.makeText(getApplicationContext(),"Digite um email valido, pois os dados dos jogos locados serão enviados para o email informado no cadastro.",Toast.LENGTH_SHORT).show();
                }

                if(edtSenha.getText().toString().length()>=6){
                    senhaSizeOk=true;
                }else{
                    senhaSizeOk=false;
                    Toast.makeText(getApplicationContext(),"Sua senha deve conter 6 caracteres no minimo",Toast.LENGTH_LONG).show();
                    edtSenha.setText("");
                    edtConfirmacao.setText("");
                }

                if(edtSenha.getText().toString().equals(edtConfirmacao.getText().toString())){
                    senhaOk=true;
                }else{
                    senhaOk=false;
                    Toast.makeText(getApplicationContext(),"As senha estão diferentes, digite-as novamente",Toast.LENGTH_LONG).show();
                    edtSenha.setText("");
                    edtConfirmacao.setText("");
                }

                if(fotoOk && nomeOk && telefoneOk && sexoOk && emailOk && senhaOk && senhaSizeOk) {
                    usuario.setNome(edtNome.getText().toString());
                    usuario.setTelefone(edtTelefone.getText().toString());
                    usuario.setSexo(sexo);
                    usuario.setPassword(edtSenha.getText().toString());
                    usuario.setEmail(edtEmail.getText().toString());
                    ActionTask actionLoadGames = new ActionTask(CadastroActivity.this, cadastroAction, "aguarde..");
                    actionLoadGames.execute();
                }
            }
        });

    }
    private void verifyCpf(){
        setContentView(R.layout.activity_cadastro_cpf);

        edtCpf = (EditText)findViewById(R.id.edtCpf);
        btnVerify = (Button)findViewById(R.id.btnVerify);

        edtCpf.addTextChangedListener(Mask.insert("###.###.###-##", edtCpf));

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cpf = edtCpf.getText().toString().replaceAll("\\.", "");
                cpf = cpf.replace("-","");
                Boolean cpfOk=false;
                if(Utils.isCPF(cpf)){
                    cpfOk=true;
                }else{
                    cpfOk=false;
                    Toast.makeText(getApplicationContext(), "Preencha um cpf valido", Toast.LENGTH_SHORT).show();
                }

                if(cpfOk) {
                    ActionTask actionLoadGames = new ActionTask(CadastroActivity.this, cpfAction, "Verificando CPF..");
                    cpfAction.param = edtCpf.getText().toString();
                    actionLoadGames.execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
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

    Action cpfAction = new Action() {
        Residencia residencia;

        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {


                    if(residencia.getId()>0){
                        usuario.setResidencia(residencia);
                        usuario.setCpf(edtCpf.getText().toString());
                        cadastro();
                    }else{
                        Toast.makeText(getApplicationContext(),residencia.getNumero(),Toast.LENGTH_LONG).show();
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

                RestServices rs = new RestServices();

                residencia = rs.verifyCpf(param);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


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


    Action cadastroAction = new Action() {
        Boolean resultado;
        @Override
        public void onPostExecute() {
            // TODO Auto-generated method stub
            Log.i("RETORNO", "OK");
            try {
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    if(resultado){
                        Utils.showAlert("Cadastro","Cadastro realizado com sucesso!",mCtx,CadastroActivity.this);
                    }else{
                        Toast.makeText(getApplicationContext(),"Erro ao cadastrar-se. Verifique se preencheu todos os campos e tente novamente.",Toast.LENGTH_LONG).show();
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

                resultado = rs.register(usuario);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
