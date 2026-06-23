package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@TeleOp (name = "Omni Drive", group = "TeleOp")
public class OmniDriveTeleOp extends LinearOpMode {
    //Declare Motors to Driver Hub
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    @Override
    public void runOpMode () {
        //Initialize hardware stack
        leftMotor = hardwareMap.get (DcMotor.class, "left_motor");
        rightMotor = hardwareMap.get (DcMotor.class, "right_motor");
        //Reverse one side so the robot drives linearly with both sticks in same direction
        leftMotor.setDirection (DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection (DcMotorSimple.Direction.FORWARD);
        telemetry.addData ("Status", "Initialized");
        telemetry.update ();
        waitForStart ();
        while (opModeIsActive ()) {
            //Omni Drive 2 mtr setup: 
            //Drives exactly like tank, lateral allowed however.
            double leftPower = -gamepad1.left_stick_y;
            double rightPower = -gamepad1.right_stick_y;
            //Send power to motors
            leftMotor.setPower (leftPower);
            rightMotor.setPower (rightPower);
            //Telemetry, drivetrain debugging
            telemetry.addData ("Left Power", leftPower);
            telemetry.addData ("Right Power", rightPower);
            telemetry.update ();
        }
    }
}
