package demo.leon.weather;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppLinkReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context ctx, Intent intent)
	{
		// TODO Auto-generated method stub
		Intent serviceIntent = null;
		if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
		{
			WeatherService.startService(ctx);
		}
		// Stop the AppLinkService on BT disconnection
		else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
		{
			WeatherService.stopService(ctx);
		}
		else if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY))
		{
			// signal your service to stop audio playback
		}
	}

}
