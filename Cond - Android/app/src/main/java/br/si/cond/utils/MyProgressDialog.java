package br.si.cond.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import br.si.cond.R;


public class MyProgressDialog extends ProgressDialog {

    TextView lblMsg;
    private String mensagem="";
	public MyProgressDialog(Context context,String msg) {
		super(context);
        this.mensagem=msg;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress);
        lblMsg = (TextView)findViewById(R.id.lblMsg);
        lblMsg.setText(mensagem);



	}
	public static ProgressDialog getProgress(Context context,String msg) {
		MyProgressDialog dialog = new MyProgressDialog(context,msg);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		return dialog;
	}
}
