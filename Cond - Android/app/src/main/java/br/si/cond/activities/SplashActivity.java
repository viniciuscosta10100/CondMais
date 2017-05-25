package br.si.cond.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import br.si.cond.R;


public class SplashActivity extends Activity {
	ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		

		
		
		Animation animationIni  = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha);
		final Animation animationEnd  = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alphaend);

		animationIni.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation arg0) {
			}           
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}           
			@Override
			public void onAnimationEnd(Animation arg0) {
				animationEnd.reset();
				img.clearAnimation();
				img.startAnimation(animationEnd);
			}
		});
		animationEnd.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation arg0) {
			}           
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}           
			@Override
			public void onAnimationEnd(Animation arg0) {

				img.setAlpha(Float.valueOf("1"));
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				finish();
			}
		});


		animationIni.reset();


		img = (ImageView)findViewById(R.id.imgSplash);
		img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				img.clearAnimation();
				img.setAlpha(Float.valueOf("1"));
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				finish();
			}
		});

		img.clearAnimation();
		img.startAnimation(animationIni);




	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindDrawables(findViewById(R.id.RootView));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

}
