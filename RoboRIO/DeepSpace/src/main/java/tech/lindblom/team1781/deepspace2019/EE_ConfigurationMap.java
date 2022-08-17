package tech.lindblom.team1781.deepspace2019;

public class EE_ConfigurationMap {

//CAN IDs Motors
public final static int DRIVE_LEFT_1 = 1;
public final static int DRIVE_LEFT_2 = 2;
public final static int DRIVE_LEFT_3 = 3;

public final static int DRIVE_RIGHT_1 = 7;
public final static int DRIVE_RIGHT_2 = 8;
public final static int DRIVE_RIGHT_3 = 9;

public final static int FRONT_ELEVATOR = 5;
public final static int REAR_ELEVATOR = 4;
public final static int ELEVATOR_LEFT_WHEEL = 11;
public final static int ELEVATOR_RIGHT_WHEEL = 12;

//Cargo Collector
public final static int CARGOCOLLECTOR_MOTOR = 6;

//Lift Elevator
public final static float ELEVATOR_RETRACTION_POWER_CONSTANT = -0.15f;
public final static float ELEVATOR_EXTENDED_POWER_CONSTANT = 0.15f;
public final static float FRONT_ELEVATOR_SPEED = 1f;
public final static float REAR_ELEVATOR_SPEED = 1f;

//Pilot Joystick Mapping
public final static int HATCH_AUTO_COLLECT = 4;
public final static int HATCH_AUTO_DISPENSE = 6;
public final static int HATCH_TILT_FORWARD = 7;
public final static int HATCH_TILT_BACKWARD = 8;
public final static int HATCH_MECH_RETRACT = 3;
public final static int HATCH_MECH_DISPENSE = 5;
public final static int HATCH_COLLECT_CLOSE_FINGERS = 1;
public final static int SLOW_DRIVING_OFF_LEVEL = 12;
public final static int CAMERA_TOGGLE = 9;

//Co-Pilot Joystick Mapping
public final static int COLLECTOR_COLLECT = 3;
public final static int COLLECTOR_DISPENSE = 5;
public final static int ELEVATE_BOTH = 7;
public final static int ELEVATOR_DRIVE = 1;
public final static int RETRACT_FRONT_LEGS = 8;
public final static int RETRACT_REAR_LEGS = 10;
public final static int LOWER_ROBOT = 12;
public final static int ADJUST_REAR_LEGS = 9;

// DIOs
public final static int CARGOCOLLECTOR_LOAD_SENSOR_CHANNEL = 9;
public final static int LIFT_FRONT_LOWER_LIMIT_SWITCH = 7;
public final static int LIFT_FRONT_UPPER_LIMIT_SWITCH = 8;
public final static int LIFT_REAR_LOWER_LIMIT_SWITCH = 5;
public final static int LIFT_REAR_UPPER_LIMIT_SWITCH = 6;

// Solenoids
public final static int HATCHCOLLECTOR_SOLENOID_A = 4;
public final static int HATCHCOLLECTOR_SOLENOID_B= 5;
public final static int HATCHMECHANISM_MOVE_SOLENOID_A = 2;
public final static int HATCHMECHANISM_MOVE_SOLENOID_B = 3;
public final static int HATCHCOLLECTOR_TILT_SOLENOID_A = 0;
public final static int HATCHCOLLECTOR_TILT_SOLENOID_B = 1;

// Analog
public final static int LIFT_FRONT_POT = 0;
public final static int LIFT_REAR_POT = 1;

// Current
public final static int DRIVE_MOTOR_MAX_CURRENT = 35;

//Timers
public final static double BEGIN_TIME_OF_FIRST_STATE = 0.0;
public final static double END_TIME_OF_FIRST_STATE = 0.16;
public final static double END_TIME_OF_SECOND_STATE = 0.32;
public final static double END_TIME_OF_THIRD_STATE = 0.5;
public final static double END_TIME_OF_FOURTH_STATE = 0.66;
public final static double END_TIME_OF_FIFTH_STATE = 0.82;
public final static double END_TIME_OF_SIXTH_STATE = 1.0;
}
