package br.si.cond.utils;

import android.os.AsyncTask;

public class LoadTask extends AsyncTask<Void, Void, Boolean> {
	
	private final Action action;

	public LoadTask(Action action) {
		this.action = action;
	}

	@Override
	protected void onPreExecute() {
		action.onPreExecute();
		super.onPreExecute();
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
		}
		return null;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
