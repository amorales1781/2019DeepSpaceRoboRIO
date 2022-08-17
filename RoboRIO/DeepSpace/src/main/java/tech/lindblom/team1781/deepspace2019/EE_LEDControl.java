/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package tech.lindblom.team1781.deepspace2019;

import edu.wpi.first.wpilibj.Spark;

public class EE_LEDControl {
    Spark LED;

    // When making a new LEDController, int input is the channel number the leds are
    // attached to
    EE_LEDControl(int input) {
        LED = new Spark(input);
    }

    // these are specific decimals used to change to specific colors
    void setRed() {
        LED.set(.61);
    }

    void setOrange() {
        LED.set(.65);
    }

    void setYellow() {
        LED.set(.69);
    }

    void setGreen() {
        LED.set(.77);
    }

    void setBlue() {
        LED.set(.87);
    }

    void setViolet() {
        LED.set(.91);
    }

    void setWhite() {
        LED.set(.93);
    }

    void setGrey() {
        LED.set(.95);
    }

    void setBlack() {
        LED.set(.99);
    }

}
