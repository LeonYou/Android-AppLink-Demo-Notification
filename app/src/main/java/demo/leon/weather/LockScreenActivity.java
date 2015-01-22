package demo.leon.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;


public class LockScreenActivity extends Activity
{
	private static LockScreenActivity mInstance = null;

	public static void show(Context ctx, boolean show)
	{
		if (show && mInstance == null)
		{
			Intent intent = new Intent(ctx, LockScreenActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
			ctx.startActivity(intent);
		}
		else if(!show && mInstance != null)
		{
			mInstance.finish();
			mInstance = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();

		LockScreenActivity.mInstance = this;
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
