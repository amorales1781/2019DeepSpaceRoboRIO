package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import tech.lindblom.team1781.deepspace2019.EE_CargoCollector;
import tech.lindblom.team1781.deepspace2019.EE_ConfigurationMap;
import tech.lindblom.team1781.deepspace2019.EE_DriveSystem;
import tech.lindblom.team1781.deepspace2019.EE_HatchCollector;
import tech.lindblom.team1781.deepspace2019.EE_CameraFixture;
import tech.lindblom.team1781.deepspace2019.EE_Lift;
import tech.lindblom.team1781.deepspace2019.EE_RoborioCam;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
@SuppressWarnings({ "unused", "rawtypes" })

public class Robot extends TimedRobot {

	Joystick driveStick, co_driveStick;

	// navx
	AHRS navx;

	// Class Testing
	EE_CargoCollector cargoCollect;
	EE_HatchCollector hatchCollect;
	EE_DriveSystem driveSystem;
	EE_Lift lift;

	// Camera Movement
	// EE_CameraFixture camera;
	// Roborio Camera
	EE_RoborioCam usbcam;

	@SuppressWarnings("unchecked")
	@Override
	public void robotInit() {

		// Joystick assigned to USB 0 & 1 for co-pilot
		driveStick = new Joystick(0);
		co_driveStick = new Joystick(1);

		// navx
		try {
			navx = new AHRS(SerialPort.Port.kMXP);
		} catch (RuntimeException ex) {
			System.out.println("Error instantiating navX-USB: " + ex.getMessage());
		}

		// Elevator
		lift = new EE_Lift(co_driveStick, navx);

		// Cargo Collector Assignment
		cargoCollect = new EE_CargoCollector(co_driveStick);

		// Hatch Collector Assignment
		hatchCollect = new EE_HatchCollector(driveStick, co_driveStick);

		// Drive System Assignment
		driveSystem = new EE_DriveSystem(navx, driveStick);

		// Camera Movement
		// camera = new EE_CameraFixture(0, 1, driveStick);
		// Usb Camera
		usbcam = new EE_RoborioCam(driveStick);
	}

	public void teleopInit() {
		// reset navx
		navx.reset();

		// Setting axis_choice to the integer that you pick (either X or Z)
		driveSystem.setAxisChoice();

		// Compressor
		hatchCollect.compressor();
		hatchCollect.starting_State();

		// Cam Tilt Reset
		// camera.camInit();
		usbcam.update();
	}

	/*
	 * Function might need to be tweaked after testing maybe using a boolean instead
	 * of the cases
	 */

	public void autonomousInit() {
		// Setting axis_choice to the integer that you pick (either X or Z)
		driveSystem.setAxisChoice();

		// Compressor
		hatchCollect.compressor();
		hatchCollect.starting_State();

		// Cam Tilt Reset
		// camera.camInit();
		usbcam.update();
	}

	@Override
	public void autonomousPeriodic() {
		lift.update();
		cargoCollect.update();
		// isn't needed
		// camera.update();
		usbcam.update();
		hatchCollect.update_control();

		// Driving off level slowly
		if (!driveSystem.drive_Off_Level())
			driveSystem.update();

	}

	@Override
	public void teleopPeriodic() {
		// used for climbing LEDControll
		lift.LEDActivation();

		lift.update();
		cargoCollect.update();
		// only to be used if we are using the servos
		// camera.update();
		usbcam.update();
		hatchCollect.update_control();
		// lift.testLimitSwitch();

		if (driveStick.getRawButton(7)) {
			navx.reset();
		}

		driveSystem.update();

	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		driveSystem.update();
		lift.elevateWithThrottle();
	}

}
