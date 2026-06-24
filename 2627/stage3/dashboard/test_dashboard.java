package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
// Import the Dashboard classes
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
@TeleOp(name = "Dashboard Testing", group = "Tests")
public class DashboardTest extends LinearOpMode {
    @Override
    public void runOpMode () {
        //Dashboard Init
        FtcDashboard dashboard = FtcDashboard.getInstance ();
        //Merge Dashboard telemetry with Driver Hub telemetry.
        //This is crucial. Data from all connected devices can be observed simultaneously 
        telemetry = new MultipleTelemetry (telemetry, dashboard.getTelemetry ());
        //Initialize dummy servo for testing
        Servo testClaw = hardwareMap.get (Servo.class, "test_claw");
        telemetry.addData ("Status", "Init Finished. Connect Dashboard to tune.");
        telemetry.update ();
        waitForStart ();
        while (opModeIsActive ()) {
            //Use the variables from RobotConstants class
            //Because they are static, no object is required to use this
            if (gamepad1.a) {testClaw.setPosition (RobotConstants.CLAW_OPEN_POS);} 
            else if (gamepad1.b) {testClaw.setPosition (RobotConstants.CLAW_CLOSED_POS);}
            //Send telemetry to dashboard
            telemetry.addData ("Claw Target Open", RobotConstants.CLAW_OPEN_POS);
            telemetry.addData ("Claw Target Closed", RobotConstants.CLAW_CLOSED_POS);
            telemetry.addData ("Actual Servo Pos", testClaw.getPosition ());
            telemetry.update ();
        }
    }
}
