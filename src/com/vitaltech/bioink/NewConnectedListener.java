package com.vitaltech.bioink ;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import zephyr.android.BioHarnessBT.*;

public class NewConnectedListener extends ConnectListenerImpl
{
	private static final String TAG = NewConnectedListener.class.getSimpleName();
	
	private Handler _OldHandler;
	private Handler _aNewHandler; 
	final int GP_MSG_ID = 0x20;
	final int BREATHING_MSG_ID = 0x21;
	final int ECG_MSG_ID = 0x22;
	final int RtoR_MSG_ID = 0x24;
	final int ACCEL_100mg_MSG_ID = 0x2A;
	final int SUMMARY_MSG_ID = 0x2B;
	
	
	private int GP_HANDLER_ID = 0x20;
	
	private final int HEART_RATE = 0x100;
	private final int RESPIRATION_RATE = 0x101;
	private final int SKIN_TEMPERATURE = 0x102;
	private final int POSTURE = 0x103;
	private final int PEAK_ACCLERATION = 0x104;
	private final int RtoR_Interval = 0x105;
	
	/*Creating the different Objects for different types of Packets*/
	private GeneralPacketInfo GPInfo = new GeneralPacketInfo();
	private ECGPacketInfo ECGInfoPacket = new ECGPacketInfo();
	private BreathingPacketInfo BreathingInfoPacket = new  BreathingPacketInfo();
	private RtoRPacketInfo RtoRInfoPacket = new RtoRPacketInfo();
	private AccelerometerPacketInfo AccInfoPacket = new AccelerometerPacketInfo();
	private SummaryPacketInfo SummaryInfoPacket = new SummaryPacketInfo();
	
	private PacketTypeRequest RqPacketType = new PacketTypeRequest();
	private String UID = "InvalidName";
	
	public NewConnectedListener(Handler handler,Handler _NewHandler) {
		super(handler, null);
		_OldHandler= handler;
		_aNewHandler = _NewHandler;

		// TODO Auto-generated constructor stub

	}
	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		Log.v(TAG, String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));
		UID = eventArgs.getSource().getDevice().getAddress();
		/*Use this object to enable or disable the different Packet types*/
		RqPacketType.GP_ENABLE = true;
		RqPacketType.BREATHING_ENABLE = true;
		RqPacketType.LOGGING_ENABLE = true;
		RqPacketType.RtoR_ENABLE = true;
		
		
		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), RqPacketType);
		//ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();
				byte CRCFailStatus;
				byte RcvdBytes;
				
				
				
				CRCFailStatus = msg.getCRCStatus();
				RcvdBytes = msg.getNumRvcdBytes() ;
				int MsgID = msg.getMsgID();
				byte [] DataArray = msg.getBytes();	
				Bundle b1 = new Bundle();
				b1.putString("UID", UID);
				switch (MsgID)
				{

				case GP_MSG_ID:
					//***************Displaying the Heart Rate********************************
					int HRate =  GPInfo.GetHeartRate(DataArray);
					Message text1 = _aNewHandler.obtainMessage(HEART_RATE);
					b1.putFloat("HeartRate", Float.valueOf(HRate));					
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					Log.v(TAG, "Heart Rate is "+ HRate);

					//***************Displaying the Respiration Rate********************************
					float RespRate = (float)GPInfo.GetRespirationRate(DataArray);
					text1 = _aNewHandler.obtainMessage(RESPIRATION_RATE);
					b1.putFloat("RespirationRate", Float.valueOf(RespRate));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					Log.v(TAG, "Respiration Rate is "+ RespRate);

					//***************Displaying the Posture******************************************					

					int PostureInt = GPInfo.GetPosture(DataArray);
					text1 = _aNewHandler.obtainMessage(POSTURE);
					b1.putFloat("Posture", Float.valueOf(PostureInt));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					Log.v(TAG, "Posture is "+ PostureInt);	
					//***************Displaying the Peak Acceleration******************************************

					float PeakAccDbl = (float)GPInfo.GetPeakAcceleration(DataArray);
					text1 = _aNewHandler.obtainMessage(PEAK_ACCLERATION);
					b1.putFloat("PeakAcceleration", Float.valueOf(PeakAccDbl));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					Log.v(TAG, "Peak Acceleration is "+ PeakAccDbl);	
				
					byte ROGStatus = GPInfo.GetROGStatus(DataArray);
					Log.v(TAG, "ROG Status is "+ ROGStatus);
				
					break;
					
					case BREATHING_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					Log.v(TAG, "Breathing Packet Sequence Number is "+BreathingInfoPacket.GetSeqNum(DataArray));
					break;
					case ECG_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					Log.v(TAG, "ECG Packet Sequence Number is "+ECGInfoPacket.GetSeqNum(DataArray));
					break;
					case RtoR_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					int[] RtoRSamples = RtoRInfoPacket.GetRtoRSamples(DataArray);
					//text1 = _aNewHandler.obtainMessage(RtoR_Interval);
					//b1.putString("RtoR", String.valueOf(700));
					//text1.setData(b1);
					for(int i = 0; i < 18; i ++){
						Log.v(TAG, "R to R interval " + i + " " + RtoRSamples[i]);
						text1 = _aNewHandler.obtainMessage(RtoR_Interval);
						b1.putFloat("RtoR", Float.valueOf(RtoRSamples[i]));
						text1.setData(b1);
						_aNewHandler.sendMessage(text1);
					}
					Log.v(TAG, "R to R Packet Sequence Number is "+RtoRInfoPacket.GetSeqNum(DataArray));
					break;
					case ACCEL_100mg_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					Log.v(TAG, "Accelerometry Packet Sequence Number is "+AccInfoPacket.GetSeqNum(DataArray));
					break;
					case SUMMARY_MSG_ID:
					/*Do what you want. Printing Sequence Number for now*/
					Log.v(TAG, "Summary Packet Sequence Number is "+SummaryInfoPacket.GetSeqNum(DataArray));
					break;					
				}
			}
		});
	}
	
}