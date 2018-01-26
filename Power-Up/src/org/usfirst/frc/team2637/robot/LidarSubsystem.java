package org.usfirst.frc.team2637.robot;

import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

public class LidarSubsystem extends SensorBase {

	I2C m_i2c;
	
	public LidarSubsystem()
	{
		m_i2c = new I2C(I2C.Port.kMXP, 0x62);
		initLidar();
	}
	
	public void initLidar()
	{
		
	}
	
	public int getDistance(boolean useBias)
	{
		byte[] buffer = new byte[2];
		
		byte[] isBusy = new byte[1];
		
		m_i2c.write(0x00, useBias ? 0x04 : 0x03);
		
		Timer.delay(0.04);
		
		/*
		
		do {
		
			m_i2c.read(0x01, 1, isBusy);
		
		}while(isSet(isBusy[0], 0));
		
		*/
		
		m_i2c.read(0x8f, 2, buffer);
		
		//return (int)Integer.toUnsignedLong(buffer[0] << 8) + Byte.toUnsignedInt(buffer[1]);
		
		//System.out.println(byteToString(buffer));
		
		return (buffer[0] & 0xff) | (buffer[1] & 0xff);
	}
	
	public String byteToString(byte[] buffer) {
		
		String binaryVal = "";
		
		for (byte b : buffer)
		{
			for (int i = 0; i < 8; i++)
			{
				binaryVal += (b >> 1);
			}
			
			binaryVal += " ";
		}
		
		return binaryVal;
	}
	
	public boolean isSet(Byte b, int pos)
	{
		return (b >> pos & 1) == 1;
	}
	
	public void isWiredCorrectly()
	{
		byte[] buffer = new byte[1];
				
		m_i2c.read(0x01, 1, buffer);
		
		System.out.println("Is this wired correctly: " + (isSet(buffer[0], 5) ? "Yes" : "No"));
		
		//SmartDashboard.putString("Is this wired correctly: ", isSet(buffer[0], 5) ? "Yes" : "No");
	}
	
	@Override
	public void initSendable(SendableBuilder builder) {
		//builder.setSmartDashboardType("Lidar Test");
	}
}
