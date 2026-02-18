package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name = "01 (Blocks to Java)")
public class teleoFarmTeam extends LinearOpMode {

  private CRServo feed;
  private DcMotor flywheel;
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor frontRight;
  private CRServo frontLeft;

  double frontLeftPower;
  int shootVelocity;
  double backLeftPower;
  double frontRightPower;
  double backRightPower;

  /**
   * This OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
   * This code will work with either a Mecanum-Drive or an X-Drive train.
   * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
   *
   * Also note that it is critical to set the correct rotation direction for each motor. See details below.
   *
   * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
   * Each motion axis is controlled by one Joystick axis.
   *
   * 1) Axial -- Driving forward and backward -- Left-joystick Forward/Backward
   * 2) Lateral -- Strafing right and left -- Left-joystick Right and Left
   * 3) Yaw -- Rotating Clockwise and counter clockwise -- Right-joystick Right and Left
   *
   * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
   * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
   * the direction of all 4 motors (see code below).
   */
  @Override
  public void runOpMode () {
    ElapsedTime runtime;

    feed = hardwareMap.get (CRServo.class, "feed");
    flywheel = hardwareMap.get (DcMotor.class, "flywheel");
    backLeft = hardwareMap.get (DcMotor.class, "backLeft");
    backRight = hardwareMap.get (DcMotor.class, "backRight");
    frontRight = hardwareMap.get (DcMotor.class, "frontRight");
    frontLeft = hardwareMap.get (CRServo.class, "frontLeft");

    runtime = new ElapsedTime ();
    shootVelocity = 1200;
    // ########################################################################################
    // !!! IMPORTANT Drive Information. Test your motor directions. !!!!!
    // ########################################################################################
    //
    // Most robots need the motors on one side to be reversed to drive forward.
    // The motor reversals shown here are for a "direct drive" robot
    // (the wheels turn the same direction as the motor shaft).
    //
    // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
    // that your motors are turning in the correct direction. So, start out with the reversals here, BUT
    // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
    //
    // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward.
    // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
    // <--- Click blue icon to see important note re. testing motor directions.
    
	//Set Flywheel direction and Velocity Set
	flywheel.setMode (DcMotor.RunMode.RUN_USING_ENCODER);
    flywheel.setDirection (DcMotor.Direction.REVERSE);
    
	//Orientation of Motor Spin
	backLeft.setDirection (DcMotor.Direction.REVERSE);
    backRight.setDirection (DcMotor.Direction.FORWARD);  
	frontLeft.setDirection (CRServo.Direction.FORWARD);
	frontRight.setDirection (CRServo.Direction.FORWARD);
    
	//Zero Power Behaviour for Drive Motors
	frontLeft.setZeroPowerBehaviour (DcMotor.ZeroPowerBehavious.BRAKE);
	frontRight.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
    backLeft.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
    backRight.setZeroPowerBehavior (DcMotor.ZeroPowerBehavior.BRAKE);
    
	//Set Direction of Feed Motors
	feed.setDirection (CRServo.Direction.FORWARD);
    
	// Wait for init (driver presses START)
    telemetry.addData ("Status", "Initialized");
    telemetry.update ();
    waitForStart ();
    runtime.reset ();
    
	// Run until sigterm (driver presses STOP)
    while (opModeIsActive ()) {
      feed.setPower (gamepad1.left_trigger);
      if (gamepad1.right_trigger > 0.1) {
        launch ();
      } else {
        flywheel.setPower (0.15);
      } drive ();
      // Show elapsed game time and wheel power consumption
      telemetry.addData ("Status", "Run Time: " + runtime);
      telemetry.addData ("Front left/Right", JavaUtil.formatNumber (frontLeftPower, 4, 2) + ", " + JavaUtil.formatNumber (frontRightPower, 4, 2));
      telemetry.addData ("Back  left/Right", JavaUtil.formatNumber (backLeftPower, 4, 2) + ", " + JavaUtil.formatNumber (backRightPower, 4, 2));
      telemetry.update ();
    }
  }

  /**
   * Describe this function...
   */
  private void drive () {
    float axial; //Front Back
    float lateral; //Left Right Strafe
    float yaw; //Left Right Rotation
    double max; //Restriction to wheel power consumption

    frontLeft.setPower (frontLeftPower);
    frontRight.setPower (frontRightPower);
    backLeft.setPower (backLeftPower);
    backRight.setPower (backRightPower);
    // POV Mode uses left joystick for forward and strafing, and right joystick for rotation.
    // Note: pushing stick forward gives negative value
    axial = -gamepad1.left_stick_y;
    lateral = gamepad1.left_stick_x;
    yaw = gamepad1.right_stick_x;
    // Combine the joystick input for desired power draw
    frontLeftPower = axial + lateral + yaw;
    frontRightPower = (axial - lateral) - yaw;
    backLeftPower = (axial - lateral) + yaw;
    backRightPower = (axial + lateral) - yaw;
    // Buffer check if wheel power exceeds 100
    max = JavaUtil.maxOfList (JavaUtil.createListWith (Math.abs (frontLeftPower), Math.abs (frontRightPower), Math.abs (backLeftPower), Math.abs (backRightPower))); //Power Overflow: max > 1
    if (max > 1) {
      frontLeftPower = frontLeftPower / max;
      frontRightPower = frontRightPower / max;
      backLeftPower = backLeftPower / max;
      backRightPower = backRightPower / max;
    }
  }

  //Launch Motor Settings
  private void launch () {
    flywheel.setPower (-1);
    feed.setPower (-1);
    sleep (850);
    feed.setPower (0);
    while (gamepad1.right_trigger > 0.1) {
      ((DcMotorEx) flywheel).setVelocity (shootVelocity);
      drive ();
      if (((DcMotorEx) flywheel).getVelocity () < shootVelocity - 50) {
        ((DcMotorEx) flywheel).setVelocity (shootVelocity);
		//Too Low Launch SPD
        feed.setPower (0);
      } else {
        feed.setPower (1);
      } telemetry.update ();
    }
  }

  /**
   * This function is used to test your motor directions.
   *
   * Each button should make the corresponding motor run FORWARD.
   *
   *   1) First set all the motors to take to correct positions preset
   *      by adjusting robot config
   *
   *   2) Then make sure they run in the correct direction by modifying the
   *      the setDirection () definitions above.
   */
  private void testMotorDirections() {
    frontLeftPower = gamepad1.x ? 1 : 0;
    backLeftPower = gamepad1.a ? 1 : 0;
    frontRightPower = gamepad1.y ? 1 : 0;
    backRightPower = gamepad1.b ? 1 : 0;
  }
}
