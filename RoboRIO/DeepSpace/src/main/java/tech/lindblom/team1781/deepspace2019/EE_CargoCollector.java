package tech.lindblom.team1781.deepspace2019;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;

public class EE_CargoCollector {
	// Variables
	WPI_TalonSRX cargoCollector;
	DigitalInput button;
	Joystick controller;

	// Constructor
	public EE_CargoCollector(Joystick _controller) {
		cargoCollector = new WPI_TalonSRX(EE_ConfigurationMap.CARGOCOLLECTOR_MOTOR);
		button = new DigitalInput(EE_ConfigurationMap.CARGOCOLLECTOR_LOAD_SENSOR_CHANNEL);
		controller = _controller;

	}

	// Functions
	public void collect() {

		if (button.get()) {
			cargoCollector.set(-1f);
		} else
			cargoCollector.set(0);

	}

	public void dispense() {

		cargoCollector.set(1f);
	}

	public void stop() {
		cargoCollector.set(0);
	}

	public void update() {
		if (controller.getRawButton(EE_ConfigurationMap.COLLECTOR_COLLECT)) {
			collect();
		} else if (controller.getRawButton(EE_ConfigurationMap.COLLECTOR_DISPENSE))
			dispense();
		else
			stop();
	}
}
