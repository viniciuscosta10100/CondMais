package br.si.cond.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.image.WebImageCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;

import br.si.cond.R;


public class Utils {

    public static final Double DISTANCE_100M =9.136706189890503E-4;

    public static String MD5(String s){
        try{
            MessageDigest m= MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            return new BigInteger(1,m.digest()).toString(16);
        }catch (Exception ex){
            ex.printStackTrace();
            return "";
        }

    }

    public static Double convertMetersIntegerToDistance(int meters){
        Double distance = (DISTANCE_100M*meters)/100;

        return distance;
    }
    public static Boolean yesOrNotToBoolean(String str){
        if(str.toUpperCase().equals("S"))
            return true;
        else
            return false;
    }
    public static String trueOrFalseToStr(Boolean val){
        if(val)
            return "S";
        else
            return "N";
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 1000;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static byte[] convertInputStreamToByteArray(InputStream inputStream)
    {
        byte[] bytes= null;

        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte data[] = new byte[1024];
            int count;

            while ((count = inputStream.read(data)) != -1)
            {
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();
            inputStream.close();

            bytes = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bytes;
    }
    public static boolean isNetworkAvailable(Context context) {
        if(context != null){
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static boolean isCPF(String CPF) {
// considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

// "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
// Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
// converte o i-esimo caractere do CPF em um numero:
// por exemplo, transforma o caractere '0' no inteiro 0
// (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

// Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

// Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    public static String imprimeCPF(String CPF) {
        return(CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }

    public static Integer getIdade(Date dataNascInput){


        Calendar dateOfBirth = new GregorianCalendar();

        dateOfBirth.setTime(dataNascInput);


        Calendar today = Calendar.getInstance();


        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);



        dateOfBirth.add(Calendar.YEAR, age);

        if (today.before(dateOfBirth)) {

            age--;

        }

        return age;

    }

    public static void showAlert(String titulo, String mensagem, Context mCtx){

            final Dialog dialog = new Dialog(mCtx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_info);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.FILL_PARENT;
            dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            TextView lblMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            TextView lblTitulo = (TextView) dialog.findViewById(R.id.lblTitulo);
            Button btnOk = (Button)dialog.findViewById(R.id.btnOk);

            lblMsg.setText(Html.fromHtml(mensagem));
            lblTitulo.setText(titulo);

            btnOk.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });



            dialog.show();

    }
    public static void showAlert(String titulo, String mensagem, Context mCtx,final Activity act){

        final Dialog dialog = new Dialog(mCtx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);

        TextView lblMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        TextView lblTitulo = (TextView) dialog.findViewById(R.id.lblTitulo);
        Button btnOk = (Button)dialog.findViewById(R.id.btnOk);

        lblMsg.setText(Html.fromHtml(mensagem));
        lblTitulo.setText(titulo);

        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                act.finish();
            }
        });



        dialog.show();

    }
    public static String bitmapToBase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }
    public static Bitmap base64ToBitmap(String input)
    {
        if(input != null) {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

        }
        return null;
    }


    public static Bitmap getBitmap(String url,Context mContext) {
        // Don't leak context
        WebImageCache webImageCache = new WebImageCache(mContext);

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if(url != null) {
            bitmap = webImageCache.get(url);
            if(bitmap == null) {
                bitmap = getBitmapFromUrl(url);
                if(bitmap != null){
                    webImageCache.put(url, bitmap);
                }
            }
        }

        return bitmap;
    }

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    private static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;

        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
