/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.lindblom.team1781.deepspace2019;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;


public class EE_CameraFixture{

  // Center Axis
  Servo cameraServo1;
  // Tilting
  Servo cameraServo2;
  double tilt, rotate;
  Joystick joystick;
  
  public EE_CameraFixture(int axisport, int tiltport, Joystick _joystick) {
    cameraServo1 = new Servo(axisport);
    cameraServo2 = new Servo(tiltport);
    joystick = _joystick;
  }

  // should be called at teleopInit() so that it resets the value for the tilt
  public void camInit() {
    tilt = .6;
    rotate = 1;
  }

  // this rotates the camera when the joystick button (1) is pressed
  public void rotate() {
    if (rotate > 0){
      if (joystick.getPOV() == 90){
        rotate = rotate - .01;
      }
    }

    if (rotate < 1){
      if (joystick.getPOV() == 270){
        rotate = rotate + .01;
      }
    }
    // if (joystick.getRawButton(1) || joystick.getY() < 0){
    //   cameraServo1.set(0);
    // } else {
    //   cameraServo1.set(1);
    // }
  }

  // POV changes the tilt of joystick
  public void tilt(){
    //camera tilts to look up
    if (tilt > 0){
      if (joystick.getPOV() == 0){
        tilt = tilt - .01;
      }
    }

    //camera tilts to look down
    if (tilt < 1){
      if (joystick.getPOV() == 180){
        tilt = tilt + .01;
      }
    }
    System.out.println(tilt);
    System.out.println(rotate);
    cameraServo2.set(tilt);
    cameraServo1.set(rotate);


  }

  public void update(){
    tilt();
		rotate();
  }
}
