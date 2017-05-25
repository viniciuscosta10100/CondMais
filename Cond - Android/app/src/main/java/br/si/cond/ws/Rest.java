package br.si.cond.ws;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Rest{

    DefaultHttpClient httpClient;
    HttpContext localContext;
    private String ret;

    HttpParams myParams = null;
    HttpResponse response = null;
    HttpPost httpPost = null;
    HttpGet httpGet = null;
    String webServiceUrl;

    //The serviceName should be the name of the Service you are going to be using.
    public Rest(String serviceName){
        myParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(myParams, 9999999);
        HttpConnectionParams.setSoTimeout(myParams, 9999999);
        httpClient = new DefaultHttpClient(myParams);
        localContext = new BasicHttpContext();
        webServiceUrl = serviceName;
    }

    public String webInvoke(String methodName, String data, String contentType) throws IOException{
        ret = null;

        httpPost = new HttpPost(webServiceUrl + methodName);
        httpPost.setParams(myParams);
        response = null;

        StringEntity tmp = null;

        if (contentType != null) {
            httpPost.setHeader("Content-Type", contentType);
        } else {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        try {
            tmp = new StringEntity(data);
        } catch (UnsupportedEncodingException e) {
            Log.e("HttpUtils", "UnsupportedEncodingException : "+e);
        }

        httpPost.setEntity(tmp);

        Log.d("Servico", webServiceUrl + "?" + data);

        try {
            response = httpClient.execute(httpPost,localContext);

            if (response != null) {
                ret = EntityUtils.toString(response.getEntity());
            }
        } catch (ClientProtocolException exception) {
            Log.e("ClientProtocolException", exception.getMessage());
        } catch (IOException exception){
            Log.e("IOException", exception.getMessage());
            throw exception;
        }

        return ret;
    }

    //Use this method to do a HttpGet/WebGet on the web service
    public String webGet(String url) {
        httpGet = new HttpGet(url);
        Log.e("WebGetURL: ",url);

        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            //Log.e("HttpClient:", e.getMessage());
            e.printStackTrace();
        }

        // we assume that the response body contains the error message
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            // Log.e("EntityUtils:", e.getMessage());
            e.printStackTrace();
        }

        return ret;
    }

    //Use this method to do a HttpGet/WebGet on the web service
    public String doPost(String url, List<NameValuePair> params) {
        httpPost = new HttpPost(url);
        try {


            httpPost.setHeader(HTTP.CONTENT_TYPE,
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setHeader("Accept", "application/json");
            // Add your data
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.e("WebGetURL: ",url);

        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            Log.e("Http Client:", e.getMessage());
        }
        // we assume that the response body contains the error message
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.e("EntityUtils:", e.getMessage());
        }
        return ret;
    }

    public String doPost(String url, JSONObject json) {
        httpPost = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String doPost(String url, String json) {
        httpPost = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(json);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String doPostForm(String url, UrlEncodedFormEntity formEntity) {
        httpPost = new HttpPost(url);
        httpPost.setEntity(formEntity);
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public InputStream getHttpStream(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        } // end try-catch

        return in;
    }

    public void clearCookies() {
        httpClient.getCookieStore().clear();
    }

    public void abort() {
        try {
            if (httpClient != null) {
                System.out.println("Abort.");
                httpPost.abort();
            }
        } catch (Exception e) {
            System.out.println("Your App Name Here" + e);
        }
    }
}