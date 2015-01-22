package demo.leon.weather;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.ChangeRegistrationResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteFileResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.DiagnosticMessageResponse;
import com.ford.syncV4.proxy.rpc.EndAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.GetDTCsResponse;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.ListFilesResponse;
import com.ford.syncV4.proxy.rpc.OnAudioPassThru;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnHashChange;
import com.ford.syncV4.proxy.rpc.OnKeyboardInput;
import com.ford.syncV4.proxy.rpc.OnLanguageChange;
import com.ford.syncV4.proxy.rpc.OnLockScreenStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnSystemRequest;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.OnTouchEvent;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.PerformAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.PutFileResponse;
import com.ford.syncV4.proxy.rpc.ReadDIDResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.ScrollableMessageResponse;
import com.ford.syncV4.proxy.rpc.SetAppIconResponse;
import com.ford.syncV4.proxy.rpc.SetDisplayLayoutResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SliderResponse;
import com.ford.syncV4.proxy.rpc.SoftButton;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.SubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.SystemRequestResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SyncDisconnectedReason;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;

import java.util.Timer;
import java.util.Vector;

// Weather service

public class WeatherService extends Service implements IProxyListenerALM
{
	private SyncProxyALM mProxy = null;
	private Timer mTimer = null;
	private WeatherReporterTask mTask = null;
	private static ComponentName mComName = null;
	public static final int MSG_SPEAK = 0;
	public static final String TAG ="tag";
	public static final String LOG_TAG = "APPLINK";
	public static final String SERVICE_NANE = "com.example.myapplink.WeatherService";


	public static void startService(Context ctx)
	{
		if (mComName == null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			mComName = ctx.startService(intent);
		}
	}

	public static void stopService(Context ctx)
	{
		if (mComName != null)
		{
			Intent intent = new Intent(SERVICE_NANE);
			ctx.stopService(intent);
			mComName = null;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String appName = intent.getStringExtra("APP_NAME");
			String rpcName = intent.getStringExtra("RPC_NAME");
			String type = intent.getStringExtra("TYPE");
			boolean success = intent.getBooleanExtra("SUCCESS", false);
			String funcName = intent.getStringExtra("FUNCTION_NAME");
			String comment1 = intent.getStringExtra("COMMENT1");
			String comment2 = intent.getStringExtra("COMMENT2");
			String comment3 = intent.getStringExtra("COMMENT3");
			String comment4 = intent.getStringExtra("COMMENT4");
			String comment5 = intent.getStringExtra("COMMENT5");
			String comment6 = intent.getStringExtra("COMMENT6");
			String comment7 = intent.getStringExtra("COMMENT7");
			String comment8 = intent.getStringExtra("COMMENT8");
			String comment9 = intent.getStringExtra("COMMENT9");
			String comment10 = intent.getStringExtra("COMMENT10");
			String data = intent.getStringExtra("DATA");

			String log =
					"*************************************************\n" +
					"APP_NAME=" + appName + "\n" +
					"RPC_NAME=" + rpcName +"\n" +
					"TYPE=" + type +"\n" +
					"SUCCESS=" + success +"\n" +
					"FUNCTION_NAME=" + funcName +"\n" +
					"COMMENT1=" + comment1 +"\n" +
					"COMMENT2=" + comment2 +"\n" +
					"COMMENT3=" + comment3 +"\n" +
					"COMMENT4=" + comment4 +"\n" +
					"COMMENT5=" + comment5 +"\n" +
					"COMMENT6=" + comment6 +"\n" +
					"COMMENT7=" + comment7 +"\n" +
					"COMMENT8=" + comment8 +"\n" +
					"COMMENT9=" + comment9 +"\n" +
					"COMMENT10=" + comment10 +"\n" +
					"DATA=" + data + "\n" +
					"*************************************************\n";

			Log.d(LOG_TAG, log);
		}
	};


	private Handler mMessageHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Utils.WeatherInfo wi = msg.getData().getParcelable(TAG);

			switch(msg.what)
			{
			case MSG_SPEAK:
				try
				{
					String ttsText = "";
					String alertText1 = "";
					String alertText2 = "";
					if (wi == null)
					{
						ttsText = "定位失败，稍后重试";
					}
					else
					{
						//mProxy.speak(data, 0);
						ttsText = String.format("当前所在城市：%s，目前气温：%d摄氏度，%s，湿度：%d%%，气压：%dhPa", wi.city,
												wi.curTemp, wi.description, wi.humidity, wi.pressure);
						alertText1 = wi.city;
						alertText2 = wi.curTemp + " degrees Celsius";
					}

					mProxy.alert(ttsText, alertText1, alertText2, true, 10 * 1000, 0);
				}
				catch (SyncException e)
				{
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void startProxy()
	{
		if (mProxy == null)
		{
			try
			{
				//mProxy = new SyncProxyALM(this, "Real Weather", true, "012345678");
				mProxy = new SyncProxyALM(this, "福特测试", true, "584421907");
				//mProxy = new SyncProxyALM(this, "License Plate Finder", true, "47");
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void disposeProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.dispose();
				mProxy = null;
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void resetProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.resetProxy();
			}
			catch (SyncException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		mTimer = new Timer();
		startProxy();


//		mTask = new WeatherReporterTask(mMessageHandler, this);
//		mTimer.schedule(mTask, 0, 30 * 1000);

//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.ford.syncV4.broadcast");
//		registerReceiver(mReceiver, filter);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		disposeProxy();

//		unregisterReceiver(mReceiver);
		super.onDestroy();
	}


	@Override
	public void onAddCommandResponse(AddCommandResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onAlertResponse(AlertResponse arg0)
	{
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Result=" + arg0.getResultCode().toString());
		Log.d(LOG_TAG, "Info=" + arg0.getInfo());
	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String arg0, Exception arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGenericResponse(GenericResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onListFilesResponse(ListFilesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnButtonPress(OnButtonPress arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnCommand(OnCommand arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		// TODO Auto-generated method stub
		switch (status.getSystemContext())
		{
			case SYSCTXT_MAIN:
				break;
			case SYSCTXT_VRSESSION:
				break;
			case SYSCTXT_MENU:
				break;
			default:
				return;
		}

		switch (status.getAudioStreamingState())
		{
			case AUDIBLE:
				// play audio if applicable
				break;
			case NOT_AUDIBLE:
				// pause/stop/mute audio if applicable
				break;
			default:
				return;
		}

		switch (status.getHmiLevel())
		{
			case HMI_FULL:
				int id = 0;
				if (status.getFirstRun())
				{
					LockScreenActivity.show(this, true);

					// setup app on SYNC
					// send welcome message if applicable
					try
					{

						//mProxy.show("this is the first", "show command", TextAlignment.CENTERED, id++);

						SoftButton sb1 = new SoftButton();
						sb1.setSoftButtonID(0);
						sb1.setText("3Hrs");
						sb1.setType(SoftButtonType.SBT_TEXT);
						sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

						SoftButton sb2 = new SoftButton();
						sb2.setSoftButtonID(0);
						sb2.setText("Days");
						sb2.setType(SoftButtonType.SBT_TEXT);
						sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

						Vector<SoftButton> softBtns = new Vector<SoftButton>();
						softBtns.add(sb1);
						softBtns.add(sb2);

						mProxy.show("Initializing", null, null, null, null, null,
									null, null, softBtns, null, null, id);

						mTask = new WeatherReporterTask(mMessageHandler, this);
						mTimer.schedule(mTask, 0, 30 * 1000);


						// send addcommands
						//					mProxy.subscribeButton(ButtonName.OK, id++);
						//					mProxy.subscribeButton(ButtonName.SEEKLEFT, id++);
						//					mProxy.subscribeButton(ButtonName.SEEKRIGHT, id++);
						//					mProxy.subscribeButton(ButtonName.TUNEUP, id++);
						//					mProxy.subscribeButton(ButtonName.TUNEDOWN, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_1, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_2, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_3, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_4, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_5, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_6, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_7, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_8, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_9, id++);
						//					mProxy.subscribeButton(ButtonName.PRESET_0, id++);
					}
					catch (SyncException e)
					{
						e.printStackTrace();
					}


				}
				else
				{
					try
					{
						mProxy.show("Real Weather", "Alive", TextAlignment.CENTERED, id++);
					}
					catch (SyncException e)
					{
						e.printStackTrace();
					}
				}
				break;
			case HMI_LIMITED:
				break;
			case HMI_BACKGROUND:
				break;
			case HMI_NONE:
				LockScreenActivity.show(this, false);
				break;
			default:
				return;
		}
	}

	@Override
	public void onOnHashChange(OnHashChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnVehicleData(OnVehicleData arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProxyClosed(String arg0, Exception arg1, SyncDisconnectedReason arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPutFileResponse(PutFileResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowResponse(ShowResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSliderResponse(SliderResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakResponse(SpeakResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
