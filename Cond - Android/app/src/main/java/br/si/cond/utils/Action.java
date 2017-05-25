package br.si.cond.utils;

import android.content.Context;

public abstract class Action {
	public String param="";
    public String param2="";
	public Context mCtx;
	public void onPreExecute(){}
	
	public abstract void run();
	
	public void onPostExecute(){}
	
}
