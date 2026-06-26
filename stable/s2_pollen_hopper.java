package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp (name = "Pollen Hopper", group = "Mechanisms")
public class s2_pollen_hopper extends LinearOpMode {
    private DcMotor intakeRoller;
    private Servo dumpBedTilt;
    // Servo positions, tune before deployment
    final double BED_STOWED = 0.1; 
    final double BED_DUMPING = 0.9; 
    @Override
    public void runOpMode () {
        intakeRoller = hardwareMap.get (DcMotor.class, "intake_roller");
        dumpBedTilt = hardwareMap.get (Servo.class, "dump_bed_tilt");
        intakeRoller.setDirection (DcMotorSimple.Direction.FORWARD);
        dumpBedTilt.setPosition (BED_STOWED);
        telemetry.addData ("Status", "Initialized");
        telemetry.update ();
        waitForStart ();
        while (opModeIsActive ()) {
            //Intake
            //Right Trigger: Intake pollen to belly
            //Left Trigger: Reverse intake, clear jams
            if (gamepad2.right_trigger > 0.1) {intakeRoller.setPower (gamepad2.right_trigger);} 
            else if (gamepad2.left_trigger > 0.1) {intakeRoller.setPower (-gamepad2.left_trigger);} 
            else {intakeRoller.setPower (0);}
            //Spitout
            //A: Tilt bed to dump pollen into Hive
            //B: Pull bed back to stowed position
            if (gamepad2.a) {dumpBedTilt.setPosition (BED_DUMPING);} 
            else if (gamepad2.b) {dumpBedTilt.setPosition (BED_STOWED);}
            telemetry.addData ("Intake Power", intakeRoller.getPower ());
            telemetry.addData ("Bed Position", dumpBedTilt.getPosition ());
            telemetry.update ();
        }
    }
}
