package org.usfirst.frc.team2637.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/*
 * Designed to have a coasting CIM motor that can be actively toggled.
 * Will set the hottest motor-controller to coasting state
 */


/**
 * Allows multiple {@link WPI_TalonSRX} objects to be linked together.
 */
public class CatzTalonGroup extends SendableBase implements SpeedController {
  private boolean m_isInverted = false;
  private final WPI_TalonSRX[] m_speedControllers;
  private static int instances = 0;

  private WPI_TalonSRX m_hotMot;
  private boolean m_useHotMot;
  
  /**
   * Create a new SpeedControllerGroup with the provided SpeedControllers.
   * 
   * Will set one of the provided SpeedControllers to coast until actively toggled.
   *
   * @param speedControllers The SpeedControllers to add
   */
  public CatzTalonGroup(WPI_TalonSRX speedController,
                    WPI_TalonSRX... speedControllers) {
	  m_speedControllers = new WPI_TalonSRX[speedControllers.length + 1];
	  m_speedControllers[0] = speedController;
	  addChild(speedController);
	  for (int i = 0; i < speedControllers.length; i++) {
		  m_speedControllers[i + 1] = speedControllers[i];
		  addChild(speedControllers[i]);
	  }
	  
	  m_hotMot = null;
	  this.dropItIfItsHot();
    
	  m_useHotMot = false;
    
	  instances++;
	  setName("SpeedControllerGroup", instances);
  	}
  
  	public void useBoost()
  	{
  		m_useHotMot = true;
  	}
  
  	public void disableBoost()
  	{
  		m_hotMot.setNeutralMode(NeutralMode.Coast);
  		m_useHotMot = false;
  	}
  
  	public void dropItIfItsHot()
  	{ 
  		if (m_speedControllers.length > 1)
  		{
	  		m_hotMot = m_speedControllers[0];
	  		
	  		for (WPI_TalonSRX talon : m_speedControllers)
	  		{
	  			m_hotMot = getHotMot(talon, m_hotMot);
	  		}
	  		
	  		m_hotMot.setNeutralMode(NeutralMode.Coast);
  		}
  		else
  		{
  			return;
  		}
  	}
  	
  	private WPI_TalonSRX getHotMot(WPI_TalonSRX t1, WPI_TalonSRX t2)
  	{
  		return t1.getTemperature() >= (t2.getTemperature() + 5.0F) ? t1 : t2;
  	}

  	@Override
  	public void set(double speed) 
  	{
  		if (m_speedControllers.length > 1)
  		{
	  		for (WPI_TalonSRX speedController : m_speedControllers) 
	  		{	
	  			if (speedController.getDeviceID() != m_hotMot.getDeviceID())
	  			{
	  				speedController.set(m_isInverted ? -speed : speed);
	  			}
	  			else
	  			{
	  				if (m_useHotMot)
	  				{
	  					speedController.set(m_isInverted ? -speed : speed);
	  				}
	  			}
	  		}
  		}
  		else
  		{
  			m_speedControllers[0].set(m_isInverted ? -speed : speed);
  		}
  	}

  @Override
  public double get() {
    if (m_speedControllers.length > 0) {
      return m_speedControllers[0].get();
    }
    return 0.0;
  }

  @Override
  public void setInverted(boolean isInverted) {
    m_isInverted = isInverted;
  }

  @Override
  public boolean getInverted() {
    return m_isInverted;
  }

  @Override
  public void disable() {
    for (WPI_TalonSRX speedController : m_speedControllers) {
      speedController.disable();
    }
  }

  @Override
  public void stopMotor() {
    for (WPI_TalonSRX speedController : m_speedControllers) {
      speedController.stopMotor();
    }
  }

  @Override
  public void pidWrite(double output) {
    for (WPI_TalonSRX speedController : m_speedControllers) {
      speedController.pidWrite(output);
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("Speed Controller");
    builder.setSafeState(this::stopMotor);
    builder.addDoubleProperty("Value", this::get, this::set);
  }
}