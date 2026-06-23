package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp (name = "Branch Reach", group = "Mechanisms")
public class Mech_BranchReach extends LinearOpMode {
    private DcMotor slideLeft;
    private DcMotor slideRight;
    private Servo wristClaw;
    //Encoder ticks for slide heights, gear ratio dependent 
    final int LEVEL_LOW = 0;
    final int LEVEL_MID = 500;
    final int LEVEL_HIGH = 1500; 
    final double WRIST_PICKUP = 0.8;
    final double WRIST_SCORE = 0.2;
    @Override
    public void runOpMode () {
        slideLeft = hardwareMap.get (DcMotor.class, "slide_left");
        slideRight = hardwareMap.get (DcMotor.class, "slide_right");
        wristClaw = hardwareMap.get (Servo.class, "wrist_claw");
        //One side reversed to rotate in the same direction
        slideLeft.setDirection (DcMotorSimple.Direction.REVERSE);
        slideRight.setDirection (DcMotorSimple.Direction.FORWARD);
        //Reset encoders and set zero
        slideLeft.setMode (DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideRight.setMode (DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Set to run to position, holding
        slideLeft.setMode (DcMotor.RunMode.RUN_TO_POSITION);
        slideRight.setMode (DcMotor.RunMode.RUN_TO_POSITION);
        waitForStart ();
        while (opModeIsActive ()) {
            //D-Pad, Slide Height
            if (gamepad2.dpad_up) {setSlideTarget (LEVEL_HIGH);}
            else if (gamepad2.dpad_down) {setSlideTarget (LEVEL_MID);}
            else if (gamepad2.dpad_left) {setSlideTarget (LEVEL_LOW);}
            //Right Bumper: Angle wrist to score on branch
            //Left Bumper: Angle wrist down to pick up pollen
            if (gamepad2.right_bumper) {wristClaw.setPosition (WRIST_SCORE);}
            else if (gamepad2.left_bumper) {wristClaw.setPosition (WRIST_PICKUP);}
            telemetry.addData ("Slide Target", slideLeft.getTargetPosition ());
            telemetry.addData ("Slide Current", slideLeft.getCurrentPosition ());
            telemetry.addData ("Slide Busy?", slideLeft.isBusy ());
            telemetry.update ();
        }
    } //Subfunction to send both motors to target position
    private void setSlideTarget (int target) {
        slideLeft.setTargetPosition (target);
        slideRight.setTargetPosition (target);  
        // Applying power is required to actually move in RUN_TO_POSITION mode
        slideLeft.setPower (0.8); 
        slideRight.setPower (0.8);
    }
}
