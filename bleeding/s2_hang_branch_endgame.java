package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
@TeleOp (name = "Canopy Hanging Parking", group = "Mechanisms")
public class s2_hang_branch_endgame extends LinearOpMode {
    private DcMotor winchMotor;
    private DigitalChannel topLimitSwitch;
    @Override
    public void runOpMode () {
        winchMotor = hardwareMap.get (DcMotor.class, "winch_motor");
        topLimitSwitch = hardwareMap.get (DigitalChannel.class, "top_limit");
        //Limit switches are usually closed when not pressed
        topLimitSwitch.setMode (DigitalChannel.Mode.INPUT);
        //When power is cut, the motor brakes, no slide down
        winchMotor.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
        winchMotor.setDirection (DcMotorSimple.Direction.FORWARD);
        telemetry.addData ("Status", "Operational-RDY");
        telemetry.update ();
        waitForStart ();
        while (opModeIsActive ()) {
            // --- WINCH CONTROLS ---
            //Y: Winch upwards to hang
            //X: Lower Winch
            //SAFETY CHECK: If the limit switch is pressed, cease pulling up
            if ((gamepad2.y) && (topLimitSwitch.getState ())) {winchMotor.setPower (1.0);} //Full power to lift 
            else if (gamepad2.x) {winchMotor.setPower (-0.5);} //Slower power to lower 
            else {winchMotor.setPower (0);} //Hold position
            telemetry.addData ("Limit Switch Pressed", !topLimitSwitch.getState ());
            telemetry.addData ("Winch Power", winchMotor.getPower ());
            telemetry.update ();
        }
    }
}
