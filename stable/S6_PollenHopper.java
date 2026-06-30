package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
//This class represents physical mechanisms
public class S6_PollenHopper {
    //Initialize motors and servos here
    //Turn the intake ON
    public Action startIntake () {
        return new Action () {
            @Override
            public boolean run (TelemetryPacket packet) {
                //intakeMotor.setPower (1.0);
                packet.put ("Hopper", "Intake Online");
                //Return false as we need "turn on" command only once.
                //The motor will keep spinning in the background.
                return false; 
            }
        };
    } //Turn intake OFF
    public Action stopIntake () {
        return new Action () {
            @Override
            public boolean run (TelemetryPacket packet) {
                //intakeMotor.setPower (0);
                packet.put ("Hopper", "Intake Stasis");
                return false;
            }
        };
    } //Wait for the hopper to fill up (Time determined in total)
    public Action waitForFill (double seconds) {
        return new Action () {
            private double startTime = -1;
            @Override
            public boolean run (TelemetryPacket packet) {
                if (startTime < 0) {startTime = System.currentTimeMillis ();}
                double elapsed = (System.currentTimeMillis () - startTime) / 1000.0;
                packet.put ("Fill Timer", elapsed);
                //Return true to keep running until the time is up.
                //While this returns true, the robot will not execute downstream sequential actions
                return elapsed < seconds; 
            }
        };
    } //Dump the hopper
    public Action dumpHopper () {
        return new Action () {
            private double startTime = -1;
            @Override
            public boolean run (TelemetryPacket packet) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis ();
                    //dumpServo.setPosition (0.9);
                    packet.put ("Hopper", "Dumping");
                } double elapsed = (System.currentTimeMillis () - startTime) / 1000.0;
                //Give the servo 1 second to dump pollen
                if (elapsed < 1.0) {return true;} //Keep waiting until times up 
                else {
                    //dumpServo.setPosition (0.1); to put back up
                    packet.put ("Hopper", "Stowed");
                    return false; //Done dumping all pollen objects
                }
            }
        };
    }
}
