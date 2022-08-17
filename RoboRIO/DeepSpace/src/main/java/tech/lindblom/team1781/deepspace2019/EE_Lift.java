package tech.lindblom.team1781.deepspace2019;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.AnalogInput;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import tech.lindblom.team1781.deepspace2019.EE_LEDControl;

public class EE_Lift {

    SpeedControllerGroup rear_elevator_drive, front_elevator_drive;
    DifferentialDrive elevator_wheeldrive;
    Joystick controller;
    AHRS elevatorNavx;
    WPI_TalonSRX front_elevator, rear_elevator;
    WPI_VictorSPX elevator_leftwheel, elevator_rightwheel;
    DigitalInput elevatorUpFrontLimitSwitch, elevatorLowFrontLimitSwitch, elevatorUpRearLimitSwitch,
            elevatorLowRearLimitSwitch;
    AnalogInput front_potentiometers, rear_potentiometers;
    float navxReferenceAngle;
    float angleTolerance = 2.0f;
    double frontSpeed, rearSpeed, frontTiltFactor, rearTiltFactor, potFactor;
    boolean useExtendPowerConstantFront,useExtendPowerConstantBack;
    EE_LEDControl LED1;

    public EE_Lift(Joystick _controller, AHRS _ElevatorNavx) {
        front_elevator = new WPI_TalonSRX(EE_ConfigurationMap.FRONT_ELEVATOR);
        rear_elevator = new WPI_TalonSRX(EE_ConfigurationMap.REAR_ELEVATOR);
        elevator_leftwheel = new WPI_VictorSPX(EE_ConfigurationMap.ELEVATOR_LEFT_WHEEL);
        elevator_rightwheel = new WPI_VictorSPX(EE_ConfigurationMap.ELEVATOR_RIGHT_WHEEL);
        elevator_wheeldrive = new DifferentialDrive(elevator_rightwheel, elevator_leftwheel);
        controller = _controller;
        elevatorUpRearLimitSwitch = new DigitalInput(EE_ConfigurationMap.LIFT_REAR_UPPER_LIMIT_SWITCH);
        elevatorUpFrontLimitSwitch = new DigitalInput(EE_ConfigurationMap.LIFT_FRONT_UPPER_LIMIT_SWITCH);
        elevatorLowRearLimitSwitch = new DigitalInput(EE_ConfigurationMap.LIFT_REAR_LOWER_LIMIT_SWITCH);
        elevatorLowFrontLimitSwitch = new DigitalInput(EE_ConfigurationMap.LIFT_FRONT_LOWER_LIMIT_SWITCH);
        front_potentiometers = new AnalogInput(EE_ConfigurationMap.LIFT_FRONT_POT);
        rear_potentiometers = new AnalogInput(EE_ConfigurationMap.LIFT_REAR_POT);
        LED1 = new EE_LEDControl(6);
        elevatorNavx = _ElevatorNavx;

    }

    public void testLimitSwitch() {
        System.out.println("" + elevatorUpFrontLimitSwitch.get() + elevatorUpRearLimitSwitch.get()
                + elevatorLowFrontLimitSwitch.get() + elevatorLowRearLimitSwitch.get());

    }

    public void elevate(double front, double rear) {
        elevateFrontLegs(front);
        elevateRearLegs(rear);
    }

    public void elevateWithThrottle() {
        System.out.println(controller.getThrottle() + " " + useExtendPowerConstantFront);
        if (controller.getRawButton(EE_ConfigurationMap.ELEVATE_BOTH)) {
            if (elevatorUpFrontLimitSwitch.get()) {
                front_elevator.set(1 * controller.getThrottle());
            } else {
                front_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            }

            if (elevatorUpRearLimitSwitch.get()) {
                rear_elevator.set(1 * controller.getThrottle());
            } else {
                rear_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            }
            useExtendPowerConstantFront = true;
            System.out.println("Elevating Legs");
        } else if (controller.getRawButton(EE_ConfigurationMap.RETRACT_FRONT_LEGS)) {
            elevateRearLegs(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            retractFrontLegs();
            elevator_Wheels_Forward();
            System.out.println("Retract Front Legs");
        } else if (controller.getRawButton(EE_ConfigurationMap.RETRACT_REAR_LEGS)) {
            useExtendPowerConstantFront = false;
            retractRearLegs();
            System.out.println("Retract Rear Legs");
        } else if (controller.getRawButton(EE_ConfigurationMap.LOWER_ROBOT)) {
            useExtendPowerConstantFront = false;
            lowerRobot();
        } else if (useExtendPowerConstantFront) {
            front_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            rear_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            System.out.println("Extend Power Constant");
        } else
            no_movement();

    }

    public void elevator_Wheels_Forward() {
        if (this.controller.getRawButton(1))
            drive();
        else {

            elevator_rightwheel.set(0);
            elevator_leftwheel.set(0);
        }

    }

    public void elevateFrontLegs(double front) {
        if (elevatorUpFrontLimitSwitch.get()) {
            front_elevator.set(front);
        } else {

            front_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);

        }
    }

    public void elevateRearLegs(double rear) {
        if (elevatorUpRearLimitSwitch.get()) {
            rear_elevator.set(rear);
        } else {
            rear_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);

        }
    }

    public void checkReferenceAngle() {

        if (!elevatorLowFrontLimitSwitch.get() && !elevatorLowRearLimitSwitch.get()
                && !controller.getRawButton(EE_ConfigurationMap.ELEVATE_BOTH)) {
            navxReferenceAngle = elevatorNavx.getRoll();
        }

    }

    public void elevateWithSelfLeveling() {

        // NAV X Tilting
        // tilting back
        if (((elevatorNavx.getRoll() - navxReferenceAngle) >= angleTolerance) && navxReferenceAngle != 0) {
            System.out.println("ELEVATE: Speeding Up Rear");
            frontTiltFactor = 0.9;
            rearTiltFactor = 0.5;
        } else if ((elevatorNavx.getRoll() - navxReferenceAngle) <= -angleTolerance && navxReferenceAngle != 0) { // tilting
                                                                                                                  // //
                                                                                                                  // forward
            System.out.println("ELEVATE: Speeding Up Front");

            frontTiltFactor = 0.5;
            rearTiltFactor = 0.9;
        } else { // even
            System.out.println("ELEVATE: LEVEL");
            frontTiltFactor = 1;
            rearTiltFactor = 1;
        }

        // Potentiometer Section
        potFactor = 1;

        // Fail safe (timer based)

        frontSpeed = EE_ConfigurationMap.FRONT_ELEVATOR_SPEED * frontTiltFactor * potFactor;
        rearSpeed = EE_ConfigurationMap.REAR_ELEVATOR_SPEED * rearTiltFactor * potFactor;

        elevate(frontSpeed, rearSpeed);
    }

    public void drive() {
        if (controller.getRawButton(EE_ConfigurationMap.ELEVATOR_DRIVE)) {
            elevator_wheeldrive.arcadeDrive(-controller.getY(), 0);
        } else
            elevator_wheeldrive.arcadeDrive(0, 0);
    }

    public void lowerRobot() {
        retractFrontLegs();
        retractRearLegs();
    }

    public void retractFrontLegs() {
        if (elevatorLowFrontLimitSwitch.get()) {
            front_elevator.set(-0.75);
        } else {
            front_elevator.set(EE_ConfigurationMap.ELEVATOR_RETRACTION_POWER_CONSTANT);
        }
    }

    public void retractRearLegs() {
        if (elevatorLowRearLimitSwitch.get()) {
            rear_elevator.set(-0.75);
        } else {
            rear_elevator.set(EE_ConfigurationMap.ELEVATOR_RETRACTION_POWER_CONSTANT);
        }
    }

    public void no_movement() {
        front_elevator.set(EE_ConfigurationMap.ELEVATOR_RETRACTION_POWER_CONSTANT);
        rear_elevator.set(EE_ConfigurationMap.ELEVATOR_RETRACTION_POWER_CONSTANT);

        elevator_wheeldrive.arcadeDrive(0, 0);
    }

    public void adjustRearElevator() {
      useExtendPowerConstantBack = !useExtendPowerConstantBack;
    }

    public void update() {

        System.out.println(debugString());

        // As you extend value increases
        checkReferenceAngle();
        System.out.println("CURRENT ANGLE: " + elevatorNavx.getRoll() + "\tREF ANGLE: " + navxReferenceAngle
                + "\tDELTA: " + (elevatorNavx.getRoll() - navxReferenceAngle));

        if (controller.getRawButton(EE_ConfigurationMap.ELEVATE_BOTH)) {
            elevateWithSelfLeveling();
            elevator_Wheels_Forward();
            useExtendPowerConstantFront = true;
            useExtendPowerConstantBack = true;
        } else if (controller.getRawButton(EE_ConfigurationMap.RETRACT_FRONT_LEGS)) {
            elevateRearLegs(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            retractFrontLegs();
            elevator_Wheels_Forward();
            System.out.println("Retract Front Legs");
        } else if (controller.getRawButtonPressed(EE_ConfigurationMap.ADJUST_REAR_LEGS)) {
            adjustRearElevator();
        } else if (controller.getRawButton(EE_ConfigurationMap.RETRACT_REAR_LEGS)) {
            useExtendPowerConstantFront = false;
            useExtendPowerConstantBack = false;
            retractRearLegs();
            System.out.println("Retract Rear Legs");
        } else if (controller.getRawButton(EE_ConfigurationMap.LOWER_ROBOT)) {
            useExtendPowerConstantFront = false;
            useExtendPowerConstantBack = false;
            lowerRobot();
        }else if (useExtendPowerConstantFront && useExtendPowerConstantBack) {
            front_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            rear_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
        } else if (useExtendPowerConstantFront && !useExtendPowerConstantBack) {
            front_elevator.set(EE_ConfigurationMap.ELEVATOR_EXTENDED_POWER_CONSTANT);
            rear_elevator.set(EE_ConfigurationMap.ELEVATOR_RETRACTION_POWER_CONSTANT);
        } else {
            no_movement();
        }
        drive();

    }

    public String debugString() {
        return front_elevator.getOutputCurrent() + "," + rear_elevator.getOutputCurrent() + ","
                + front_potentiometers.getAverageVoltage() + "," + rear_potentiometers.getAverageVoltage() + ","
                + elevatorLowFrontLimitSwitch.get() + "," + elevatorLowRearLimitSwitch.get() + ","
                + elevatorUpFrontLimitSwitch.get() + "," + elevatorUpRearLimitSwitch.get();
    }

    public void getLimitSwitches() {
        System.out.println("Front Up Limit Switch = " + elevatorUpFrontLimitSwitch.get());
        System.out.println("Rear Up Limit Switch = " + elevatorUpRearLimitSwitch.get());
        System.out.println("Front Low Limit Switch = " + elevatorLowRearLimitSwitch.get());
        System.out.println("Rear Low Limit Switch = " + elevatorLowRearLimitSwitch.get());
    }

    public void LEDActivation() {
        if (!elevatorLowFrontLimitSwitch.get() && !elevatorLowRearLimitSwitch.get() && elevatorUpFrontLimitSwitch.get()
                && elevatorUpRearLimitSwitch.get()) {
            LED1.setGreen();
        } else if (elevatorLowFrontLimitSwitch.get() && elevatorLowRearLimitSwitch.get()
                && !elevatorUpFrontLimitSwitch.get() && !elevatorUpRearLimitSwitch.get()) {
            LED1.setRed();
        } else if (!elevatorLowFrontLimitSwitch.get() && elevatorLowRearLimitSwitch.get()
                && elevatorUpFrontLimitSwitch.get() && !elevatorUpRearLimitSwitch.get()) {
            LED1.setYellow();
        } else {
            LED1.setBlue();
        }
    }

}
