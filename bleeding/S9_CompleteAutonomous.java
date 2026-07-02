package org.firstinspires.ftc.teamcode;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import java.util.List;
@Autonomous(name = "Complete Autonomous", group = "S9: Full System", preselectTeleOp = "TeleOp: Default")
public class S9_CompleteAutonomous extends LinearOpMode {
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private S7_StateMachineHopper hopper;
    //Target positions for movements (in inches)
    private static final double INTAKE_X = 24;
    private static final double INTAKE_Y = 0;
    private static final double SCORING_X = 48;
    private static final double SCORING_Y = 24;
    @Override
    public void runOpMode () throws InterruptedException {
        //Init all
        Pose2d startPose = new Pose2d (0, 0, 0);
        MecanumDrive drive = new MecanumDrive (hardwareMap, startPose);
        hopper = new S7_StateMachineHopper ();
        // Initialize vision
        initVision ();
        telemetry.addData ("Status", "Operational");
        telemetry.addData ("Target Tag", "ID 5");
        telemetry.update ();
        waitForStart ();
        if (isStopRequested ()) {return;}
        //Detect AprilTag to determine scoring position
        int targetTagId = detectTargetTag ();
        telemetry.addData ("Detected Tag", targetTagId);
        telemetry.update ();
        //Drive to intake while starting intake mechanism
        Actions.runBlocking (
            new SequentialAction (
                //Drive to intake position AND start intake simultaneously
                new ParallelAction (
                    drive.actionBuilder (startPose)
                        .splineTo (new Vector2d (INTAKE_X, INTAKE_Y), Math.PI / 4)
                        .build (),
                    new Action () {
                        @Override
                        public boolean run (TelemetryPacket packet) {
                            hopper.startIntake ();
                            packet.put ("Phase", "Dual Intake-Drivetrain");
                            return false; //Run once, then proceed to next actions
                        }
                    }
                ), //Wait for hopper to fill completely (tracked by state machine)
                new Action () {
                    private double startTime = -1;
                    @Override
                    public boolean run (TelemetryPacket packet) {
                        if (startTime < 0) {startTime = System.currentTimeMillis ();}
                        double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                        packet.put ("Phase", "Waiting for completed fill");
                        packet.put ("Filling Time Taken", elapsed);
                        //Simulate filling for 1.5s
                        //This would check a sensor in the actual bot
                        if (elapsed > 1.5) {
                            hopper.stopIntake ();
                            return false;
                        } return true;
                    }
                }, //Drive to scoring position
                drive.actionBuilder (new Pose2d (INTAKE_X, INTAKE_Y, 0))
                    .splineTo (new Vector2d (SCORING_X, SCORING_Y), Math.PI / 2)
                    .build (),
                //Dump pollen
                new Action () {
                    private double startTime = -1;
                    @Override
                    public boolean run (TelemetryPacket packet) {
                        if (startTime < 0) {
                            startTime = System.currentTimeMillis ();
                            hopper.startDump ();
                            packet.put ("Phase", "Dumping Pollen");
                        } double elapsed = (System.currentTimeMillis () - startTime) / 1000.0;
                        if (elapsed > 1.0) {
                            hopper.finishDump ();
                            packet.put ("Phase", "Dumping Completed");
                            return false;
                        } return true;
                    }
                }
            )
        ); telemetry.addData ("Status", "Autonomous runtime completed");
        telemetry.update ();
        sleep (2000);
    } private int detectTargetTag () {
        //Look for AprilTags for 3 seconds
        double startTime = System.currentTimeMillis ();
        while ((opModeIsActive ()) && ((System.currentTimeMillis () - startTime) < 3000)) {
            List<AprilTagDetection> detections = aprilTag.getDetections ();
            for (AprilTagDetection detection : detections) {
                //Different tags might indicate different scoring positions
                if ((detection.id >= 1) && (detection.id <= 10)) {return detection.id;}
            } sleep (100);
        } //Default to tag 5 if no tags detected
        return 5;
    } private void initVision () {
        aprilTag = new AprilTagProcessor.Builder ()
            .setDrawAxes (true)
            .setDrawCubeProjection (true)
            .build ();
        VisionPortal.Builder builder = new VisionPortal.Builder ();
        if (hardwareMap.getAll (WebcamName.class).isEmpty ()) {builder.setCamera (BuiltinCameraDirection.BACK);} 
        else {builder.setCamera (hardwareMap.get (WebcamName.class, "Main Cam"));}
        builder.addProcessor (aprilTag);
        visionPortal = builder.build ();
    }
}
