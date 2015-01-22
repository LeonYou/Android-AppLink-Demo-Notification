package demo.leon.weather;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * Created by leon on 14/12/30.
 */
public class Utils
{
	public static class WeatherInfo implements Parcelable
	{
		public String city;
		public String description;
		public int curTemp;
		public int minTemp;
		public int maxTemp;
		public int humidity;
		public int pressure;

		@Override
		public int describeContents()
		{
			return 0;
		}

		@Override
		public void writeToParcel(Parcel parcel, int i)
		{
			parcel.writeString(city);
			parcel.writeString(description);
			parcel.writeInt(curTemp);
			parcel.writeInt(minTemp);
			parcel.writeInt(maxTemp);
			parcel.writeInt(humidity);
			parcel.writeInt(pressure);
		}
	}

	// Kevin -> C Degree
	private static double k2c(double k)
	{
		return k - 273.15;
	}

	private static int roundHalfUp(double val)
	{
		return new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
	}

	public static WeatherInfo parseJson(String json)
	{
		WeatherInfo wi = new WeatherInfo();
		try
		{
			JSONObject jObj = new JSONObject(json);
			JSONObject mainObj = jObj.getJSONObject("main");
			JSONObject weatherObj = jObj.getJSONArray("weather").getJSONObject(0);
			wi.city = jObj.getString("name");
			wi.description = weatherObj.getString("description");
			wi.curTemp = roundHalfUp(k2c(mainObj.getDouble("temp")));
			wi.maxTemp = roundHalfUp(k2c(mainObj.getDouble("temp_max")));
			wi.minTemp = roundHalfUp(k2c(mainObj.getDouble("temp_min")));
			wi.humidity = mainObj.getInt("humidity");
			wi.pressure = roundHalfUp(mainObj.getDouble("pressure"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}


		return wi;
	}

	public static String downloadJson(double latitude, double longitude)
	{
		StringBuffer buf = new StringBuffer();
		String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&lang=zh_cn",
								   latitude, longitude);
		try
		{
			HttpClient getClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = getClient.execute(request);


			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				InputStream is = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				String line = "";
				while ((line = br.readLine()) != null)
					buf.append(line + "\r\n");

				is.close();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buf.toString();
	}

//	public static String downloadJson(String city)
//	{
//		StringBuffer buf = new StringBuffer();
//		String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&lang=zh_cn",
//								   city);
//		try
//		{
//			HttpClient getClient = new DefaultHttpClient();
//			HttpGet request = new HttpGet(url);
//			HttpResponse response = getClient.execute(request);
//
//
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//			{
//				InputStream is = response.getEntity().getContent();
//				BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//				String line = "";
//				while ((line = br.readLine()) != null)
//					buf.append(line + "\r\n");
//
//				is.close();
//			}
//		}
//		catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return buf.toString();
//	}

}
