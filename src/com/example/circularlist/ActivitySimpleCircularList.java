package com.example.circularlist;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * @author swapnil
 * 
 * @Desc Eqn of Circle (x-cx)*(x-cx)+(y-cy)*(y-cy)=rad*rad;
 * 
 * x=cx+radious*cos(theta)
 * 
 * y=cy+radious*sin(theta)
 */
public class ActivitySimpleCircularList extends Activity 
{

	private  RelativeLayout llVIewHolder;
	
	private double centerX;
	
	private double centerY;
	
	private double deltThetaRadian=40*Math.PI/180;
	
	private GestureDetector gestureDetector;
	
	private GestureListener gestureListener;
	
	private double thetaRadianCurr;
	
	private boolean enableTranslation;
	
	private boolean enableRotation;
	
	private ValueAnimator valueAnimator;

	private ObjectAnimator animTranslateX;

	private ObjectAnimator animTranslateY;
	
	private static int radCircle;
	
	private static final int MIN_PUSH_DISTANCE = 120;
	
	private static final int MIN_VELOCITY_TO_ROTATE = 1000;
	
	private class GestureListener extends SimpleOnGestureListener   
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			Log.v("FLING", "Flinged");

			float diffX=e1.getX() - e2.getX();
			float deltaX = Math.abs(diffX);
			float deltaY = Math.abs(e1.getY() - e2.getY());
			Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));
			double velocity=Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
			boolean rotateClockWIse = false;

			if(e1.getX()>e2.getX() && e1.getY()<centerY && e2.getY()<centerY ) rotateClockWIse=false;

			else if(e1.getX()<e2.getX() && e1.getY()<centerY && e2.getY()<centerY )rotateClockWIse=true;

			else if(e1.getX()>e2.getX() && e1.getY()>centerY && e2.getY()>centerY ) rotateClockWIse=true;

			else if(e1.getX()<e2.getX() && e1.getY()>centerY && e2.getY()>centerY )rotateClockWIse=false;

			else rotateClockWIse=true;
			Log.v("SPEED:", "e1X: "+e1.getX()+" e2X"+e2.getX()+" "+rotateClockWIse);

			//int noOfRotation=(int)(velocity/MIN_VELOCITY_TO_ROTATE);

			double endTheta=(velocity*Math.PI*2)/MIN_VELOCITY_TO_ROTATE; //1 rotation(360) requires MIN_VELOCITY_TO_ROTATE

			long timeOfRotation=(long)((endTheta/velocity)*1000000); // velocity=distance/time

			//Log.v("SPEED:", "NO OF ROT "+noOfRotation+" endTheta: "+endTheta+" velocity: "+velocity+" timeOfRot: "+endTheta/velocity);
			if( deltaX> MIN_PUSH_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY_TO_ROTATE) 
			{
				Toast.makeText(getApplicationContext(), "rotate ClockWise: "+rotateClockWIse, Toast.LENGTH_SHORT).show();
				rotateViews(rotateClockWIse,timeOfRotation,endTheta);
				return false; // Right to left
			}  


			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_simple_circular_list);
		initData();
		initUI();
	}

	private void initData() 
	{

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		centerX = width/2;
		centerY=height/2;
		radCircle=width/3;
	}

	private void initUI() 
	{


		llVIewHolder = (RelativeLayout)findViewById(R.id.llViewHolder);

		AnimationUtils.loadAnimation(this, R.anim.deaccelerate);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run()
			{
				updateList();

			}


		}, 20);

		llVIewHolder.setOnGenericMotionListener(new OnGenericMotionListener() {

			@Override
			public boolean onGenericMotion(View arg0, MotionEvent arg1) {

				return false;
			}


		});

		findViewById(R.id.btnTranslate).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				String text="";
				enableTranslation=!enableTranslation;
				if(enableTranslation) text="Disable Translation";
				else text="Enable Translation";
				((Button)findViewById(R.id.btnTranslate)).setText(text);
				//((Button)findViewById(R.id.btnTranslate)).setText("Disab Translation");
			}
		});

		findViewById(R.id.btnRotate).setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				String text="";

				enableRotation=!enableRotation;
				if(enableRotation) text="Disable Rotation";
				else text="Enable Rotation";
				((Button)findViewById(R.id.btnRotate)).setText(text);
			}
		});

		gestureListener=new GestureListener();
		gestureDetector=new GestureDetector(getApplicationContext(), gestureListener);
		llVIewHolder.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent event)
			{

				float deltaX;
				float deltaY;

				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:

					if(valueAnimator!=null && valueAnimator.isRunning()) valueAnimator.cancel();  

					deltaX = (float) (centerX - event.getX());
					deltaY = (float) (centerY - event.getY());
					thetaRadianCurr = Math.atan2(deltaY, deltaX);
					thetaRadianCurr=thetaRadianCurr-Math.PI;
					for(int i=0;i<llVIewHolder.getChildCount();i++)
					{
						View v=llVIewHolder.getChildAt(i);
						//setLocationOnScreen(i,v, deltaX, deltaY, angleInRadian*(i+1));
						if(enableTranslation)setLocationOnScreenUsingTranslateANimation(v,  thetaRadianCurr+(i)*deltThetaRadian);

						if(enableRotation)startRotateANimation(v,  thetaRadianCurr+(i)*deltThetaRadian);
						else startRotateANimation(v,  0);
					}

					break;
				case MotionEvent.ACTION_UP:



					break;
				case MotionEvent.ACTION_MOVE:

					
					deltaX = (float) (centerX - event.getX());
					deltaY = (float) (centerY - event.getY());
					thetaRadianCurr = Math.atan2(deltaY, deltaX);

					thetaRadianCurr=thetaRadianCurr-Math.PI;
					//thetaRadian=thetaRadian-deltThetaRadian;
					Log.v("Tag", " Rad: "+thetaRadianCurr);
					//thetaRadian=2*Math.PI-thetaRadian;
					for(int i=0;i<llVIewHolder.getChildCount();i++)
					{
						View v=llVIewHolder.getChildAt(i);
						//setLocationOnScreen(i,v, deltaX, deltaY, angleInRadian*(i+1));
						setLocationOnScreen(v,  thetaRadianCurr+(i)*deltThetaRadian);
					}

					break;


				default:
					break;
				}
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});
	}


	private void rotateViews(boolean clockWise, long timeToRoundCircle, double endTheta) 
	{
		double startVal=thetaRadianCurr;

		double endVal=endTheta;

		if(!clockWise) endVal= (-endVal);

		endVal=endVal+startVal;//endP0s= prevTheta+ resultantTheta.

		Log.v("TEST", "StartVal: "+startVal+ " endVal: "+endVal+" clockWise: "+clockWise);

		valueAnimator = ValueAnimator.ofFloat((float)startVal,(float) (endVal));
		valueAnimator.setDuration(timeToRoundCircle);
		valueAnimator.setInterpolator(new DecelerateInterpolator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
		{
			public void onAnimationUpdate(ValueAnimator animation) 
			{
				double theta=Double.parseDouble(animation.getAnimatedValue()+"");
				for(int i=0;i<llVIewHolder.getChildCount();i++)
				{
					View v=llVIewHolder.getChildAt(i);
					setLocationOnScreen(v,theta+i*deltThetaRadian);
				}
			}
		});
		valueAnimator.start();


	}

	private void updateList() 
	{
		for(int i=0;i<llVIewHolder.getChildCount();i++)
		{
			View v=llVIewHolder.getChildAt(i);

			setLocationOnScreen(v,i*deltThetaRadian);
			
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					
					startRotateANimation(view, 2*2*Math.PI);//rotate view 2*360 degree
				}
			});
		}



	}


	public void setLocationOnScreen( View v, double angleInRadiantheta)
	{	

		final PointF pointOnCircle = new PointF();
		pointOnCircle.x = (float) (centerX + ((float)(radCircle*(Math.cos(angleInRadiantheta)))));
		pointOnCircle.y = (float) (centerY + ((float)(radCircle*(Math.sin(angleInRadiantheta)))));
		v.setTranslationX(pointOnCircle.x);
		v.setTranslationY(pointOnCircle.y);


	} 


	public void setLocationOnScreenUsingTranslateANimation(final View v,double angleInRadiantheta)
	{	


		final PointF pointOnCircle = new PointF();
		pointOnCircle.x = (float) (centerX + ((float)(radCircle*(Math.cos(angleInRadiantheta)))));
		pointOnCircle.y = (float) (centerY + ((float)(radCircle*(Math.sin(angleInRadiantheta)))));

		animTranslateX = ObjectAnimator.ofFloat(v, "X", pointOnCircle.x);//translationX
		animTranslateX.setDuration(1000);
		animTranslateX.start();

	    animTranslateY = ObjectAnimator.ofFloat(v, "Y", pointOnCircle.y);//translationY
		animTranslateY.setDuration(1000);
		animTranslateY.start();




	} 

	public void startRotateANimation( View v,double angleInRadiantheta)
	{	

		ObjectAnimator animX = ObjectAnimator.ofFloat(v, "rotation", 0.0f,(float)(angleInRadiantheta*180/Math.PI));
		animX.setDuration(1000);
		//animX.setRepeatCount(repeatCount);
		animX.start();

	} 


/*	public void rotateUsingValueAnimator( View v,float fromAngle ,float angleInRadiantheta)
	{


	}*/
}
