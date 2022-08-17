package tech.lindblom.team1781.deepspace2019;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class EE_DriveSystem {
    int axis_choice;
    double motor;
	double motor_constant = 8;
	double rotation = 0;
    double rotation_constant = 8;
    Joystick controlStick;
    PIDController straightControl;
    SendableChooser Axis_Chooser;
    SpeedControllerGroup m_left, m_right;
    DifferentialDrive myDrive;
    WPI_TalonSRX left1, left2, left3, right1, right2, right3;
    LMSA_PID RotatePID;
    AHRS straightNavx;
    Encoder encoL, encoR;
    PowerDistributionPanel pdp;

    public EE_DriveSystem(AHRS _navx, Joystick _control){
        //Joystick
        controlStick = _control;
        
        //navx
        straightNavx = _navx;
        
        pdp =  new PowerDistributionPanel();

        //PID
        RotatePID = new LMSA_PID();

        // Left side of the Drive System being assigned
		left1 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_LEFT_1);
		left2 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_LEFT_2);
        left3 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_LEFT_3);
        
        left1.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);
        left2.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);
        left3.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);

        left1.configPeakCurrentLimit(0);
        left2.configPeakCurrentLimit(0);
        left3.configPeakCurrentLimit(0);

        left1.enableCurrentLimit(true);
        left2.enableCurrentLimit(true);
        left3.enableCurrentLimit(true);

		m_left = new SpeedControllerGroup(left1, left2, left3);

		// Right side of the Drive System being assigned
		right1 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_RIGHT_1);
		right2 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_RIGHT_2);
		right3 = new WPI_TalonSRX(EE_ConfigurationMap.DRIVE_RIGHT_3);
		right1.setInverted(true);
		right2.setInverted(true);
        right3.setInverted(true);

        right1.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);
        right2.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);
        right3.configContinuousCurrentLimit(EE_ConfigurationMap.DRIVE_MOTOR_MAX_CURRENT);

        right1.configPeakCurrentLimit(0);
        right2.configPeakCurrentLimit(0);
        right3.configPeakCurrentLimit(0);

        right1.enableCurrentLimit(true);
        right2.enableCurrentLimit(true);
        right3.enableCurrentLimit(true);

        m_right = new SpeedControllerGroup(right1, right2, right3);
        
        // Drive object
        myDrive = new DifferentialDrive(m_left, m_right);
        
        	// Assignment to classes for choosing between X and Z Axis
		Axis_Chooser = new SendableChooser();

		// Z is the default
		Axis_Chooser.addDefault("Axis Z", new Integer(0));
		Axis_Chooser.addObject("Axis X", new Integer(1));
        SmartDashboard.putData("Axis Type: ", Axis_Chooser);

		// Encoders
		encoL = new Encoder(0, 1, false, Encoder.EncodingType.k2X);
		encoR = new Encoder(2, 3, false, Encoder.EncodingType.k2X);

        reset();
	
    }

    public void reset()
    {
		// Encoder Reset
		encoL.reset();
		encoR.reset();

    }

    public double setAxisChoice()
    {
       return axis_choice = ((Integer) (Axis_Chooser.getSelected())).intValue();
    }

    public double axis() {
		if (setAxisChoice() == 0) {
			return (controlStick.getZ() - rotation) / rotation_constant;
		}

		if (setAxisChoice() == 1) {
			return (controlStick.getX() - rotation) / rotation_constant;
		}
		return axis_choice;
    }

    public void update(){
    // Rotate Ramping setup for driving on axis
		rotation += axis();

		// Motor Ramping setup for driving Forward and Backwords
        motor += ((controlStick.getY() - motor) / motor_constant);
    
        myDrive.arcadeDrive(rotation * -.7, motor);
        SmartDashboard.putNumber("pdp", pdp.getCurrent(0));
    }

    public boolean drive_Off_Level()
    {
        if (controlStick.getRawButton(EE_ConfigurationMap.SLOW_DRIVING_OFF_LEVEL))
        {
            m_left.set(-0.4);
            m_right.set(-0.4);
            return true;
        }
        return false;
    }

    public void invertDriveSystem()
    {
        myDrive.arcadeDrive(rotation * -.7, motor * -1);
    }

    public void usePIDOutput()
    {
        if(controlStick.getRawButton(12))
        {
        	straightControl.setSetpoint(0);
        	straightControl.enable();
            rotation = RotatePID.get_output();
        }else {
        	straightControl.disable();
        	rotation += axis();
		}
    }
}
