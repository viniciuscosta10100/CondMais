package br.si.cond.ws;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.si.cond.model.Agendamento;
import br.si.cond.model.Ambiente;
import br.si.cond.model.Aviso;
import br.si.cond.model.ComentarioReinvidicacao;
import br.si.cond.model.Condominio;
import br.si.cond.model.Mensagem;
import br.si.cond.model.Reinvidicacao;
import br.si.cond.model.Residencia;
import br.si.cond.model.ResultServer;
import br.si.cond.model.User;


public class RestServices {

    private final String urlServer = "http://192.168.43.239:3000";
    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public ArrayList<Agendamento> getAgendamentos(String data, Long ambiente_id) {
        ArrayList<Agendamento> agendamentos = new ArrayList<Agendamento>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/agendamentos.json?ambiente_id="+ambiente_id.toString()+"&data="+ data.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonAgendamentos = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonAgendamentos.length();i++){
                JSONObject obj = jsonAgendamentos.getJSONObject(i);
                Agendamento agendamento = gsonCustom.fromJson(obj.toString(),Agendamento.class);
                agendamentos.add(agendamento);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return agendamentos;
    }

    public ArrayList<Aviso> getAvisos(Long condominio_id) {

        ArrayList<Aviso> avisos = new ArrayList<Aviso>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/avisos.json?condominio_id="+ condominio_id.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonAgendamentos = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonAgendamentos.length();i++){
                JSONObject obj = jsonAgendamentos.getJSONObject(i);
                Aviso aviso = gsonCustom.fromJson(obj.toString(),Aviso.class);
                avisos.add(aviso);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return avisos;
    }

    public ArrayList<Agendamento> getAgendamentos(Long idUser) {

        ArrayList<Agendamento> agendamentos = new ArrayList<Agendamento>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/agendamentos.json?user_id="+ idUser.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonAgendamentos = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonAgendamentos.length();i++){
                JSONObject obj = jsonAgendamentos.getJSONObject(i);
                Agendamento agendamento = gsonCustom.fromJson(obj.toString(),Agendamento.class);
                agendamentos.add(agendamento);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return agendamentos;
    }

    public Reinvidicacao getReinvidicacao(Long idReivindicacao) {

        Reinvidicacao reinvidicacao = new Reinvidicacao();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/reinvidicacoes/"+idReivindicacao.toString()+".json";
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            reinvidicacao= gsonCustom.fromJson(response,Reinvidicacao.class);
            JSONArray jsonComentarios = new JSONObject(response).getJSONArray("comentarios");
            Log.i("response", response);
            ArrayList<ComentarioReinvidicacao> comentarios = new ArrayList<ComentarioReinvidicacao>();
            for(int i = 0;i<jsonComentarios.length();i++){
                JSONObject obj = jsonComentarios.getJSONObject(i);
                ComentarioReinvidicacao comentario = gsonCustom.fromJson(obj.toString(),ComentarioReinvidicacao.class);
                comentarios.add(comentario);
            }

            reinvidicacao.setComentarios(comentarios);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return reinvidicacao;
    }

    public ArrayList<Mensagem> getMensagensRecentes(Long idUser) {

        ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/mensagens.json?user_id="+ idUser.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonReinvidicacoes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonReinvidicacoes.length();i++){
                JSONObject obj = jsonReinvidicacoes.getJSONObject(i);
                Mensagem msg = gsonCustom.fromJson(obj.toString(),Mensagem.class);
                mensagens.add(msg);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return mensagens;
    }
    public ArrayList<Mensagem> getMensagens(Long idUser, Long idVizinho) {

        ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/getMensagens.json?user_id="+idUser.toString()+"&vizinho_id="+idVizinho.toString() ;
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonReinvidicacoes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonReinvidicacoes.length();i++){
                JSONObject obj = jsonReinvidicacoes.getJSONObject(i);
                Mensagem msg = gsonCustom.fromJson(obj.toString(),Mensagem.class);
                mensagens.add(msg);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return mensagens;
    }

    public ArrayList<Reinvidicacao> getReinvidicacoes(Long idUser) {

        ArrayList<Reinvidicacao> reinvidicacoes = new ArrayList<Reinvidicacao>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/reinvidicacoes.json?user_id="+ idUser.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonReinvidicacoes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonReinvidicacoes.length();i++){
                JSONObject obj = jsonReinvidicacoes.getJSONObject(i);
                Reinvidicacao reinvidicacao = gsonCustom.fromJson(obj.toString(),Reinvidicacao.class);
                reinvidicacoes.add(reinvidicacao);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return reinvidicacoes;
    }
    public ArrayList<User> getVizinhos(Long condominio_id) {

        ArrayList<User> vizinhos = new ArrayList<User>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/moradores.json?condominio_id="+ condominio_id.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonReinvidicacoes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonReinvidicacoes.length();i++){
                JSONObject obj = jsonReinvidicacoes.getJSONObject(i);
                User vizinho = gsonCustom.fromJson(obj.toString(),User.class);
                vizinhos.add(vizinho);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return vizinhos;
    }
    public ArrayList<User> getMoradores(Long residencia_id) {

        ArrayList<User> vizinhos = new ArrayList<User>();
        Gson gsonCustom =new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

        try {
            String host=urlServer+"/moradores.json?residencia_id="+ residencia_id.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonReinvidicacoes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonReinvidicacoes.length();i++){
                JSONObject obj = jsonReinvidicacoes.getJSONObject(i);
                User vizinho = gsonCustom.fromJson(obj.toString(),User.class);
                vizinhos.add(vizinho);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return vizinhos;
    }

    public ArrayList<Ambiente> getAmbientes(Long idCondominio) {

        ArrayList<Ambiente> ambientes = new ArrayList<Ambiente>();


        try {
            String host=urlServer+"/ambientes.json?condominio_id="+ idCondominio.toString();
            Rest webService = new Rest(host);
            String response = webService.webGet(host);

            JSONArray jsonAmbientes = new JSONArray(response);
            Log.i("response", response);
            for(int i = 0;i<jsonAmbientes.length();i++){
                JSONObject obj = jsonAmbientes.getJSONObject(i);
                Ambiente ambiente = gson.fromJson(obj.toString(),Ambiente.class);
                ambientes.add(ambiente);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return ambientes;
    }

    public Residencia verifyCpf(String cpf){
        Residencia result = new Residencia();
        String host = urlServer+"/verify_cpf";
        String response="";
        Rest webService = new Rest(host);

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cpf",cpf));


            response = webService.doPost(host,params);
            JSONObject obj=new JSONObject(response);

            if(obj.has("result")){
                result.setId(new Long(-1));
                result.setNumero(obj.getString("message"));
            }else{
                result = gson.fromJson(response,Residencia.class);

                result.getCondominio().setFotoUrl(urlServer + obj.getJSONObject("condominio").getJSONObject("foto").getJSONObject("foto").getString("url"));
            }



        } catch (Exception exception) {
            result = null;


            exception.printStackTrace();
        }

        return result;
    }

    public ResultServer saveReserva(String data, String hora,String ambienteId,String usuarioId){
        String host = urlServer+"/agendamentos";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("agendamento[user_id]",usuarioId));
            params.add(new BasicNameValuePair("agendamento[data]",data));
            params.add(new BasicNameValuePair("agendamento[hora]",hora));
            params.add(new BasicNameValuePair("agendamento[ambiente_id]",ambienteId.toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer sendMessage(Mensagem msg){
        String host = urlServer+"/sendmsg";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("remetente_id",msg.getRemetente().getId().toString()));
            params.add(new BasicNameValuePair("destinatario_id",msg.getDestinatario().getId().toString()));
            params.add(new BasicNameValuePair("mensagem",msg.getTexto()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer resendAviso(Long aviso_id){
        String host = urlServer+"/resend";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("aviso_id",aviso_id.toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer desativarAviso(Long aviso_id){
        String host = urlServer+"/desativarAviso";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("aviso_id",aviso_id.toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer sendAviso(String mensagem,Long user_id,Long condominio_id){
        String host = urlServer+"/sendAviso.json";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id",user_id.toString()));
            params.add(new BasicNameValuePair("condominio_id",condominio_id.toString()));
            params.add(new BasicNameValuePair("mensagem",mensagem.toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer logout(Long idUser){
        String host = urlServer+"/logout";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id",idUser.toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer saveMembro(String cpf,String user_id){
        String host = urlServer+"/novomembro";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id",user_id));
            params.add(new BasicNameValuePair("cpf",cpf));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }

    public ResultServer saveReivindicacao(Reinvidicacao reivindicacao){
        String host = urlServer+"/reinvidicacoes";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("reinvidicacao[user_id]",reivindicacao.getUser_id().toString()));
            params.add(new BasicNameValuePair("reinvidicacao[condominio_id]",reivindicacao.getCondominio_id().toString()));
            params.add(new BasicNameValuePair("reinvidicacao[status]",reivindicacao.getStatus()));
            params.add(new BasicNameValuePair("reinvidicacao[mensagem]",reivindicacao.getMensagem()));
            params.add(new BasicNameValuePair("reinvidicacao[foto]",reivindicacao.getFoto()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }
    public ResultServer saveComentarioReivindicacao(ComentarioReinvidicacao comentario){
        String host = urlServer+"/comentario_reinvidicacoes";
        Rest webService = new Rest(host);
        ResultServer result = new ResultServer();
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comentario_reinvidicacao[user_id]",comentario.getUser_id().toString()));
            params.add(new BasicNameValuePair("comentario_reinvidicacao[reinvidicacao_id]",comentario.getReinvidicacao_id().toString()));
            params.add(new BasicNameValuePair("comentario_reinvidicacao[mensagem]",comentario.getMensagem()));
            params.add(new BasicNameValuePair("comentario_reinvidicacao[concluido]",comentario.getConcluido().toString()));

            String response = webService.doPost(host,params);

            result = gson.fromJson(response,ResultServer.class);


        } catch (Exception exception) {
            result.setResult("false");
            result.setResult("Erro inesperado, tente em instantes.");
            exception.printStackTrace();
        }

        return result;
    }

    public Boolean register(User usuario){
        String host = urlServer+"/users";
        Rest webService = new Rest(host);
        Boolean resultado = false;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user[nome]",usuario.getNome()));
            params.add(new BasicNameValuePair("user[cpf]",usuario.getCpf()));
            params.add(new BasicNameValuePair("user[email]",usuario.getEmail()));
            params.add(new BasicNameValuePair("user[telefone]",usuario.getTelefone()));
            params.add(new BasicNameValuePair("user[sexo]",usuario.getSexo().toString()));
            params.add(new BasicNameValuePair("user[residencia_id]",usuario.getResidencia().getId().toString()));
            params.add(new BasicNameValuePair("user[foto]",usuario.getFoto()));
            params.add(new BasicNameValuePair("user[password]",usuario.getPassword()));

            String response = webService.doPost(host,params);

            ResultServer result = gson.fromJson(response,ResultServer.class);

            resultado = Boolean.parseBoolean(result.getResult());

        } catch (Exception exception) {
            resultado = false;
            exception.printStackTrace();
        }

        return resultado;
    }

    public Boolean update(User usuario){
        String host = urlServer+"/alterar_usuario";
        Rest webService = new Rest(host);
        Boolean resultado = false;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user[id]",usuario.getId().toString()));
            params.add(new BasicNameValuePair("user[nome]",usuario.getNome()));
            params.add(new BasicNameValuePair("user[telefone]",usuario.getTelefone()));
            params.add(new BasicNameValuePair("user[sexo]",usuario.getSexo().toString()));
            params.add(new BasicNameValuePair("user[foto]",usuario.getFoto()));

            String response = webService.doPost(host,params);

            ResultServer result = gson.fromJson(response,ResultServer.class);

            resultado = Boolean.parseBoolean(result.getResult());

        } catch (Exception exception) {
            resultado = false;
            exception.printStackTrace();
        }

        return resultado;
    }

    public User auth(String email,String senha, String gcm){
        User usuario = new User();
        Condominio cond = new Condominio();
        Residencia residencia = new Residencia();

        residencia.setCondominio(cond);
        usuario.setResidencia(residencia);
        String host = urlServer+"/users/sign_in";
        Rest webService = new Rest(host);

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user[email]",email));
            params.add(new BasicNameValuePair("user[password]", senha));
            params.add(new BasicNameValuePair("gcm", gcm));

            String response = webService.doPost(host,params);
            JSONObject obj=new JSONObject(response);
            usuario = gson.fromJson(response,User.class);


            usuario.getCondominio().setFotoUrl(urlServer + obj.getJSONObject("condominio").getJSONObject("foto").getString("url"));
        } catch (Exception exception) {
            usuario.setId(null);
            exception.printStackTrace();
        }

        return usuario;
    }

    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };


    private class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF,
                                JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
        }
    }
}

