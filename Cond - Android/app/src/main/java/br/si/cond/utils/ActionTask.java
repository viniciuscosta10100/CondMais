package br.si.cond.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ActionTask extends AsyncTask<Void, Void, Boolean> {
	
	private ProgressDialog progressDialog;
	public final Context context;
	private final Action action;
	private final String message;

	public ActionTask(Context context, Action action, String message) {
		this.context = context;
		this.action = action;
		action.mCtx=context;
		this.message = message;
	}

	@Override
	protected void onPreExecute() {
		action.onPreExecute();
		super.onPreExecute();
		openProgress();
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		action.onPostExecute();
		super.onPostExecute(result);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		try{
			action.run();
		} catch (Throwable e){
			e.printStackTrace();
		} finally {
			try {
				closeProgress();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void openProgress(){
		try {
			progressDialog = MyProgressDialog.getProgress(context, message);
			progressDialog.show();
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	private void closeProgress(){
		try {
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCancelled() {
		if(progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onCancelled();
	}
}
