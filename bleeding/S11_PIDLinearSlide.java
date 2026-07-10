package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
@Config  //PID constants visible in FTC Dashboard
@TeleOp (name = "Test: PID Linear Slide", group = "S11: PID Control")
public class S11_PIDLinearSlide extends LinearOpMode {
    //PID constants, tune in dashboard configurations
    public static double SLIDE_P = 10;  //Proportional values, error correction agressiveness
    public static double SLIDE_I = 0.5; //Integral, eliminates steady-state error
    public static double SLIDE_D = 1.0; //Derivative,  dampens general oscillation
    //Position targets in encoder ticks
    public static int POSITION_GROUND = 0;
    public static int POSITION_LOW = 500;
    public static int POSITION_HIGH = 1500;
    public static int POSITION_MAX = 2000;
    //Motors
    private DcMotorEx slideMotor;
    //PID status
    private double integralSum = 0;
    private double lastError = 0;
    private ElapsedTime timer = new ElapsedTime ();
    @Override
    public void runOpMode () {
        //Initialize motor for sliding 
        slideMotor = hardwareMap.get (DcMotorEx.class, "slide_motor");
        //Configure motor, position control
        slideMotor.setMode (DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode (DcMotor.RunMode.RUN_WITHOUT_ENCODER); //PID manually
        slideMotor.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setDirection (DcMotor.Direction.FORWARD);
        //Setup telemetry
        telemetry = new MultipleTelemetry (telemetry, FtcDashboard.getInstance ().getTelemetry ());
        telemetry.addData ("Status", "PID Slide Ready");
        telemetry.addData ("Instructions", "Use dpad up/down to set position");
        telemetry.update ();
        int targetPosition = 0;
        waitForStart ();
        while (opModeIsActive ()) {
            //Manual position control with dpad
            if (gamepad1.dpad_up) {targetPosition = POSITION_HIGH;} 
            else if (gamepad1.dpad_down) {targetPosition = POSITION_GROUND;} 
            else if (gamepad1.dpad_left) {targetPosition = POSITION_LOW;} 
            else if (gamepad1.dpad_right) {targetPosition = POSITION_MAX;}
            //PID Control Loops
            int currentPosition = slideMotor.getCurrentPosition ();
            double error = targetPosition - currentPosition;
            //Proportional increment terms
            double pTerm = SLIDE_P * error;
            //Integral term (accumulates error over time)
            integralSum += error * timer.seconds ();
            //Anti-windup, prevents integral from growing too large
            integralSum = Math.max (-1000, Math.min (1000, integralSum));
            double iTerm = SLIDE_I * integralSum;
            //Derivative of error values
            double derivative = (error - lastError) / timer.seconds ();
            double dTerm = SLIDE_D * derivative;
            //Calculate final power output
            double power = pTerm + iTerm + dTerm;
            //Clamp basic power to valid range
            power = Math.max (-1.0, Math.min (1.0, power));
            //Apply power to motor
            slideMotor.setPower (power);
            //Update PID states
            lastError = error;
            timer.reset ();
            //Telemetry
            telemetry.addData ("Target Position", targetPosition);
            telemetry.addData ("Current Position", currentPosition);
            telemetry.addData ("Error", error);
            telemetry.addData ("Motor Power", "%.2f", power);
            telemetry.addLine ();
            telemetry.addData ("P Term", "%.2f", pTerm);
            telemetry.addData ("I Term", "%.2f", iTerm);
            telemetry.addData ("D Term", "%.2f", dTerm);
            telemetry.update ();
        }
    }
}
