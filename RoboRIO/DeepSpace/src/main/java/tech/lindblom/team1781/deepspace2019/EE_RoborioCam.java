/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.lindblom.team1781.deepspace2019;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.*;
import edu.wpi.first.wpilibj.Joystick;

public class EE_RoborioCam{
  UsbCamera camera, camera2;
  MjpegServer cameraServer;
  Joystick pilot;
  boolean state;
  
  public EE_RoborioCam(Joystick _joystick) {
    pilot = _joystick;
    state = true;
    camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320/2, 240/2, 20);
    camera2 = CameraServer.getInstance().startAutomaticCapture();
    camera2.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320/2, 240/2, 20);
    cameraServer = new MjpegServer("serve_USB Camera 0", 9000);

  }
  public void update(){
    if (pilot.getRawButton(EE_ConfigurationMap.CAMERA_TOGGLE)){
     state = !state;
    }

    if (state == true){
      cameraServer.setSource(camera);
    }else if (state == false){
      cameraServer.setSource(camera2);
    }


  }
}
