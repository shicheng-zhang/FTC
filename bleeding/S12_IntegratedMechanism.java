package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
@Config
public class S12_IntegratedMechanism {
    //State Machine Definition
    public enum State {
        IDLE,
        MOVING_TO_INTAKE,
        INTAKING,
        MOVING_TO_SCORE,
        SCORING,
        ERROR
    } private State currentState = State.IDLE;
    //PID Constants
    public static double SLIDE_P = 10;
    public static double SLIDE_I = 0.5;
    public static double SLIDE_D = 1.0;
    //Position Target
    public static int POS_GROUND = 0;
    public static int POS_INTAKE = 500;
    public static int POS_SCORE = 1500;
    //Hardware Init
    private DcMotorEx slideMotor;
    private Servo intakeServo;
    private DigitalChannel gamePieceSensor;
    private DigitalChannel bottomLimit;
    //PID States
    private int targetPosition = 0;
    private double integralSum = 0;
    private double lastError = 0;
    private ElapsedTime pidTimer = new ElapsedTime ();
    //Init Everything
    public void init (DcMotorEx slide, Servo intake, DigitalChannel sensor, DigitalChannel limit) {
        slideMotor = slide;
        intakeServo = intake;
        gamePieceSensor = sensor;
        bottomLimit = limit;
        //Configure Hardware Options
        slideMotor.setMode (DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode (DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
        gamePieceSensor.setMode (DigitalChannel.Mode.INPUT);
        bottomLimit.setMode (DigitalChannel.Mode.INPUT);
        intakeServo.setPosition(0.5); //Stowing Position
        currentState = State.IDLE;
    } //Check Sensor States
    private boolean hasGamePiece () {return gamePieceSensor.getState ();}
    private boolean atBottom () {return !bottomLimit.getState ();}
    private boolean atTargetPosition () {
        int error = Math.abs (targetPosition - slideMotor.getCurrentPosition ());
        return error < 50; //Within 50 ticks, robot is at target
    } //PID Control Status
    private void updatePID () {
        int currentPosition = slideMotor.getCurrentPosition ();
        double error = targetPosition - currentPosition;
        //P term
        double pTerm = SLIDE_P * error;
        //I term, anti-windup built in
        integralSum += error * pidTimer.seconds ();
        integralSum = Math.max (-1000, Math.min (1000, integralSum));
        double iTerm = SLIDE_I * integralSum;
        //D term
        double derivative = (error - lastError) / pidTimer.seconds ();
        double dTerm = SLIDE_D * derivative;
        //Compute and deliver power
        double power = pTerm + iTerm + dTerm;
        power = Math.max (-1.0, Math.min (1.0, power));
        slideMotor.setPower (power);
        //Update Drive State
        lastError = error;
        pidTimer.reset ();
    } //State Machine Specific Commands
    public void startIntakeSequence () {
        if (currentState == State.IDLE) {
            currentState = State.MOVING_TO_INTAKE;
            targetPosition = POS_INTAKE;
            intakeServo.setPosition (0.9); // Open intake
        }
    } public void startScoreSequence () {
        if ((currentState == State.IDLE) || (currentState == State.INTAKING)) {
            currentState = State.MOVING_TO_SCORE;
            targetPosition = POS_SCORE;
        }
    } public void emergencyStop () {
        slideMotor.setPower (0);
        intakeServo.setPosition (0.5);
        currentState = State.IDLE;
    } //Master Loop
    public void update (TelemetryPacket packet) {
        //Always PID to maintain position
        updatePID ();
        //State machine logic
        switch (currentState) {
            case MOVING_TO_INTAKE:
                if (atTargetPosition ()) {currentState = State.INTAKING;}
                break;
            case INTAKING:
                //Wait for sensor to detect game piece
                if (hasGamePiece ()) {
                    intakeServo.setPosition (0.1); // Close intake
                    currentState = State.IDLE;
                } break;
            case MOVING_TO_SCORE:
                if (atTargetPosition ()) {
                    currentState = State.SCORING;
                    intakeServo.setPosition (0.9); // Open to score
                } break;
            case SCORING:
                //Wait 1 second for game piece to fall out
                //Use a timer when optimal
                intakeServo.setPosition (0.5); //Stow Central Tray
                currentState = State.IDLE;
                break;
            default:
                break;
        } //Safety check
        if ((atBottom ()) && (targetPosition < 0)) {
            targetPosition = 0;
            packet.put ("Safety", "Bottom Extension Limit");
        } //Telemetry
        packet.put ("State", currentState.toString ());
        packet.put ("Target Pos", targetPosition);
        packet.put ("Current Pos", slideMotor.getCurrentPosition ());
        packet.put ("Has Piece", hasGamePiece ());
    } //Test Teleop Runtime
    @TeleOp (name = "Test: Integrated Mechanism", group = "S12: Full System")
    public static class TestIntegrated extends LinearOpMode {
        @Override
        public void runOpMode () {
            S12_IntegratedMechanism mech = new S12_IntegratedMechanism ();
            DcMotorEx slide = hardwareMap.get (DcMotorEx.class, "slide_motor");
            Servo intake = hardwareMap.get (Servo.class, "intake_servo");
            DigitalChannel sensor = hardwareMap.get (DigitalChannel.class, "game_piece_sensor");
            DigitalChannel limit = hardwareMap.get (DigitalChannel.class, "bottom_limit");
            mech.init (slide, intake, sensor, limit);
            telemetry = new MultipleTelemetry (telemetry, FtcDashboard.getInstance ().getTelemetry ());
            telemetry.addData ("Status", "RDY");
            telemetry.update ();
            waitForStart ();
            while (opModeIsActive ()) {
                //Controls
                if (gamepad1.a) {mech.startIntakeSequence ();} 
                else if (gamepad1.b) {mech.startScoreSequence ();} 
                else if (gamepad1.x) {mech.emergencyStop ();}
                //Update Runtime Mechanism
                TelemetryPacket packet = new TelemetryPacket ();
                mech.update (packet);
                telemetry.addData ("State", mech.currentState);
                telemetry.update ();
            }
        }
    }
}
