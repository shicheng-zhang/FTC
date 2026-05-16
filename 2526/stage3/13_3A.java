package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * This file is 13_3A, or in naming before 15/04/26 as 01N, a buffering file as a edit of the original 01.Java, now called 01_1.java.
 * This is not the actual file for running teleoperations
 * For that refer to the new 13_3.java file
 *
 * */

@TeleOp(name = "_012 (Blocks to Java)")
public class _012 extends LinearOpMode {

  private DcMotor backRight;
  private DcMotor frontRight;
  private DcMotor frontLeft;
  private DcMotor backLeft;
  private CRServo feed;
  private CRServo launcher;

  /**
   * This function is executed when this Op Mode is selected.
   */
  @Override
  public void runOpMode() {
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    feed = hardwareMap.get(CRServo.class, "feed");
    launcher = hardwareMap.get(CRServo.class, "launcherAsCRServo");

    // Put initialization blocks here.
    MOTOR_SETTINGS();
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      while (opModeIsActive()) {
        execution();
        firing_mech();
      }
    }
  }

  /**
   * Describe this function...
   */
  private void MOTOR_SETTINGS() {
    backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backRight.setDirection(DcMotor.Direction.FORWARD);
    frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontRight.setDirection(DcMotor.Direction.FORWARD);
    frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontLeft.setDirection(DcMotor.Direction.FORWARD);
    backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backLeft.setDirection(DcMotor.Direction.FORWARD);
  }

  /**
   * Describe this function...
   */
  private void firing_mech() {
    float feed_spd;
    double launch_spd;

    if (gamepad1.left_bumper) {
      feed_spd = -1;
    } else {
      if (Math.abs(gamepad1.left_trigger) > 0.5) {
        feed_spd = gamepad1.left_trigger;
      } else {
        feed_spd = 0;
      }
    }
    if (gamepad1.right_trigger > 0.5) {
      launch_spd = -1;
    } else if (gamepad1.right_bumper) {
      launch_spd = -0.75;
    } else if (gamepad1.x) {
      launch_spd = -0.6;
    } else {
      launch_spd = -0.15;
    }
    feed.setPower(feed_spd);
    launcher.setPower(launch_spd);
  }

  /**
   * Describe this function...
   */
  private void execution() {
    float forwardBack;
    float strafe;
    float leftFrontPower;
    float rightFrontPower;
    float leftBackPower;
    float rightBackPower;

    // Determining movement based on gamepad inputs
    forwardBack = -gamepad1.right_stick_x;
    strafe = -gamepad1.left_stick_y;
    leftFrontPower = forwardBack - strafe;
    rightFrontPower = forwardBack + strafe;
    leftBackPower = forwardBack + strafe;
    rightBackPower = forwardBack - strafe;
    // Setting Motor Power
    backLeft.setPower(leftFrontPower);
    backRight.setPower(rightFrontPower);
    backLeft.setPower(leftBackPower);
    frontLeft.setPower(rightBackPower);
    strafer();
  }

  /**
   * Describe this function...
   */
  private void strafer() {
    if (gamepad1.dpad_right) {
      frontRight.setPower(1);
      backRight.setPower(-1);
      backLeft.setPower(1);
      frontLeft.setPower(-1);
    } else if (gamepad1.dpad_left) {
      frontRight.setPower(-1);
      backRight.setPower(1);
      backLeft.setPower(-1);
      frontLeft.setPower(1);
    } else {
      frontRight.setPower(0);
      backRight.setPower(0);
      backLeft.setPower(0);
      frontLeft.setPower(0);
    }
  }
}


