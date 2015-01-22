package demo.leon.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.TimerTask;


public class WeatherReporterTask extends TimerTask //implements LocationListener
{
	private Handler mHandler = null;
	private Context mCtx = null;
	private LocationManager mLocationMgr = null;

	public WeatherReporterTask(Handler handler, Context ctx)
	{
		mHandler = handler;
		mCtx = ctx;

		mLocationMgr = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);

		// When in the house, LocationManager.GPS_PROVIDER cannot use
		//mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 0, this);
	}

	@Override
	public void run()
	{
		Location location = mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Utils.WeatherInfo wi = null;
		String json = null;
		if (location != null)
			json = Utils.downloadJson(location.getLatitude(), location.getLongitude());
		else
			json = Utils.downloadJson(31.22, 121.46);		//default Shanghai

		wi = Utils.parseJson(json);

		Bundle b = new Bundle();
		b.putParcelable(WeatherService.TAG, wi);

		Message msg = new Message();
		msg.what = WeatherService.MSG_SPEAK;
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

}
