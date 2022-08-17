package tech.lindblom.team1781.deepspace2019;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EE_HatchCollector {
	// Variable
	Joystick pilot, copilot;
	Compressor compressor;
	DoubleSolenoid hatchCollector, moveHatchCollector, hatchTilt;
	Timer timer_Collect, timer_Dispense;
	int state_Collect, state_Dispense, control_choice;
	boolean Manual_State;
	SendableChooser Hatch_Auto_Manual;

	// Constructor
	public EE_HatchCollector(Joystick _pilot, Joystick _copilot) {
		// 4 and 5
		hatchCollector = new DoubleSolenoid(50, EE_ConfigurationMap.HATCHCOLLECTOR_SOLENOID_A,
				EE_ConfigurationMap.HATCHCOLLECTOR_SOLENOID_B);
		// 2 and 3
		moveHatchCollector = new DoubleSolenoid(50, EE_ConfigurationMap.HATCHMECHANISM_MOVE_SOLENOID_A,
				EE_ConfigurationMap.HATCHMECHANISM_MOVE_SOLENOID_B);
		// 0 and 1
		hatchTilt = new DoubleSolenoid(50, EE_ConfigurationMap.HATCHCOLLECTOR_TILT_SOLENOID_A,
				EE_ConfigurationMap.HATCHCOLLECTOR_TILT_SOLENOID_B);

		// Compressor
		compressor = new Compressor(50);
		copilot = _copilot;
		pilot = _pilot;

		// Timer
		timer_Collect = new Timer();
		timer_Dispense = new Timer();

		// Starting Position
		starting_State();

		// SmartDash
		Hatch_Auto_Manual = new SendableChooser();

		// Manual Control is Default
		Hatch_Auto_Manual.addDefault("Manual Control", new Integer(0));
		Hatch_Auto_Manual.addObject("Automatic Control", new Integer(1));
		SmartDashboard.putData("Control Type: ", Hatch_Auto_Manual);
	}

	// Functions
	public void compressor() {
		compressor.start();
	}

	public int setControlChoice() {
		return control_choice = ((Integer) (Hatch_Auto_Manual.getSelected())).intValue();
	}

	public void update_control() {
		System.out.println("Manual State: " + Manual_State);
		/*
		 * 
		 * if (setControlChoice() == 0) { // Manual Functions manual_Hatch();
		 * manual_Retract_Deploy_Mechanism(); manual_Tilt(); } else if
		 * (setControlChoice() == 1) { // Auto Functions auto_Dispensing();
		 * auto_Collecting(); }
		 */
		// Setting to Manual Control
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_MECH_RETRACT)
				|| pilot.getRawButton(EE_ConfigurationMap.HATCH_MECH_DISPENSE)
				|| pilot.getRawButton(EE_ConfigurationMap.HATCH_TILT_BACKWARD)
				|| pilot.getRawButton(EE_ConfigurationMap.HATCH_TILT_FORWARD)
				|| pilot.getRawButton(EE_ConfigurationMap.HATCH_COLLECT_CLOSE_FINGERS)
				|| copilot.getRawButton(EE_ConfigurationMap.ELEVATE_BOTH)) {
			// When Manual_State = true ---> Manual is Active
			Manual_State = true;
		}

		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_AUTO_COLLECT)
				|| pilot.getRawButton(EE_ConfigurationMap.HATCH_AUTO_DISPENSE)) {
			// When Manual_State = false ---> Auto is Active
			Manual_State = false;
		}

		if (Manual_State == true) {
			// Manual Functions
			manual_Hatch();
			manual_Retract_Deploy_Mechanism();
			manual_Tilt();
		} else {
			// Auto Functions
			auto_Dispensing();
			auto_Collecting();
		}
	}

	public void auto_Dispensing() {
		// Button 6
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_AUTO_DISPENSE)) {
			// Timer
			timer_Dispense.reset();
			timer_Dispense.start();

			// Reset state for other
			state_Collect = 0;
		}

		// What happens in each state
		if (state_Dispense == 1) {
			reset_Dispensing();
		} else if (state_Dispense == 2) // Tilt Forward
		{
			tilt_Foward();
		} else if (state_Dispense == 3) // Deploy Mechanism
		{
			deploy_HatchCollector();
		} else if (state_Dispense == 4) // Close Fingers
		{
			close_Hatch();
		} else if (state_Dispense == 5) // Retract Mechanism
		{
			retract_HatchCollector();
		} else if (state_Dispense == 6) // Tilt back
		{
			tilt_Back();
		}

		// State Changers (Dispensing Hatch)
		if (timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_FIRST_STATE
				&& timer_Dispense.get() > EE_ConfigurationMap.BEGIN_TIME_OF_FIRST_STATE) {
			state_Dispense = 1;
		} else if (timer_Dispense.get() > EE_ConfigurationMap.END_TIME_OF_FIRST_STATE
				&& timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_SECOND_STATE) {
			state_Dispense = 2;
		} else if (timer_Dispense.get() > EE_ConfigurationMap.END_TIME_OF_SECOND_STATE
				&& timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_THIRD_STATE) {
			state_Dispense = 3;
		} else if (timer_Dispense.get() > EE_ConfigurationMap.END_TIME_OF_THIRD_STATE
				&& timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_FOURTH_STATE) {
			state_Dispense = 4;
		} else if (timer_Dispense.get() > EE_ConfigurationMap.END_TIME_OF_FOURTH_STATE
				&& timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_FIFTH_STATE) {
			state_Dispense = 5;
		} else if (timer_Dispense.get() > EE_ConfigurationMap.END_TIME_OF_FIFTH_STATE
				&& timer_Dispense.get() < EE_ConfigurationMap.END_TIME_OF_SIXTH_STATE) {
			state_Dispense = 6;
		}
	}

	public void auto_Collecting() {
		// Button 4
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_AUTO_COLLECT)) {
			// Timer
			timer_Collect.reset();
			timer_Collect.start();

			// Reset State for other
			state_Dispense = 0;
		}

		// What happens in each state
		if (state_Collect == 1) {
			reset_Collecting();
		} else if (state_Collect == 2) // Tilt Forward
		{
			tilt_Foward();
		} else if (state_Collect == 3) // Deploy Mechanism
		{
			deploy_HatchCollector();
		} else if (state_Collect == 4) // Open Fingers
		{
			open_Hatch();
		} else if (state_Collect == 5) // Retract Mechanism
		{
			retract_HatchCollector();
		} else if (state_Collect == 6) // Tilt back
		{
			tilt_Back();
		}

		// State Changers (Collecting Hatch)
		if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_FIRST_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.BEGIN_TIME_OF_FIRST_STATE) {
			state_Collect = 1;
		} else if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_SECOND_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.END_TIME_OF_FIRST_STATE) {
			state_Collect = 2;
		} else if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_THIRD_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.END_TIME_OF_SECOND_STATE) {
			state_Collect = 3;
		} else if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_FOURTH_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.END_TIME_OF_THIRD_STATE) {
			state_Collect = 4;
		} else if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_FIFTH_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.END_TIME_OF_FOURTH_STATE) {
			state_Collect = 5;
		} else if (timer_Collect.get() < EE_ConfigurationMap.END_TIME_OF_SIXTH_STATE
				&& timer_Collect.get() > EE_ConfigurationMap.END_TIME_OF_FIFTH_STATE) {
			state_Collect = 6;
		}
	}

	public void open_Hatch() {
		hatchCollector.set(DoubleSolenoid.Value.kReverse);
	}

	public void close_Hatch() {
		hatchCollector.set(DoubleSolenoid.Value.kForward);
	}

	public void tilt_Foward() {
		hatchTilt.set(DoubleSolenoid.Value.kReverse);
	}

	public void tilt_Back() {
		hatchTilt.set(DoubleSolenoid.Value.kForward);
	}

	public void deploy_HatchCollector() {
		moveHatchCollector.set(DoubleSolenoid.Value.kReverse);
	}

	public void retract_HatchCollector() {
		moveHatchCollector.set(DoubleSolenoid.Value.kForward);
	}

	public void reset_Collecting() {
		// Retract Hatch Collector
		moveHatchCollector.set(DoubleSolenoid.Value.kReverse);

		// Tilt Back
		hatchTilt.set(DoubleSolenoid.Value.kForward);

		// Close Fingers
		hatchCollector.set(DoubleSolenoid.Value.kForward);
	}

	public void reset_Dispensing() {
		// Retract Hatch Collector
		moveHatchCollector.set(DoubleSolenoid.Value.kReverse);

		// Tilt Back
		hatchTilt.set(DoubleSolenoid.Value.kForward);

		// Fingers Open
		hatchCollector.set(DoubleSolenoid.Value.kReverse);
	}

	public void starting_State() {
		// Tilt Back
		hatchTilt.set(DoubleSolenoid.Value.kForward);

		// Fingers Open
		hatchCollector.set(DoubleSolenoid.Value.kReverse);

		// Retract Mechanism
		moveHatchCollector.set(DoubleSolenoid.Value.kReverse);
	}

	// Manual Control on Fingers opening and closing on the Hatch Collector
	public void manual_Hatch() {
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_COLLECT_CLOSE_FINGERS)) {
			close_Hatch();

		} else {
			open_Hatch();
		}
	}

	// Manual Control on Tilting the Hatch Collector
	public void manual_Tilt() {
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_TILT_FORWARD)
				|| copilot.getRawButton(EE_ConfigurationMap.ELEVATE_BOTH)) {
			tilt_Foward();
		} else if (pilot.getRawButton(EE_ConfigurationMap.HATCH_TILT_BACKWARD)) {
			tilt_Back();
		}
	}

	// Manual Control on Mechanism to push in and out Hatch Collector
	public void manual_Retract_Deploy_Mechanism() {
		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_MECH_DISPENSE)
				|| copilot.getRawButton(EE_ConfigurationMap.RETRACT_FRONT_LEGS)) {
			deploy_HatchCollector();
		}

		if (pilot.getRawButton(EE_ConfigurationMap.HATCH_MECH_RETRACT)) {
			retract_HatchCollector();
		}
	}
}