package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "_01 (Blocks to Java)")
public class _01 extends LinearOpMode {

  private DcMotor d;
  private DcMotor ri;
  private DcMotor v;
  private DcMotor e;
  private CRServo feed;
  private CRServo launcher;

  /**
   * This function is executed when this Op Mode is selected.
   */
  @Override
  public void runOpMode() {
    d = hardwareMap.get(DcMotor.class, "d");
    ri = hardwareMap.get(DcMotor.class, "ri");
    v = hardwareMap.get(DcMotor.class, "v");
    e = hardwareMap.get(DcMotor.class, "e");
    feed = hardwareMap.get(CRServo.class, "feed");
    launcher = hardwareMap.get(CRServo.class, "launcher");

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
    d.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    d.setDirection(DcMotor.Direction.FORWARD);
    ri.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    ri.setDirection(DcMotor.Direction.FORWARD);
    v.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    v.setDirection(DcMotor.Direction.FORWARD);
    e.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    e.setDirection(DcMotor.Direction.FORWARD);
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
    ri.setPower(leftFrontPower);
    d.setPower(rightFrontPower);
    e.setPower(leftBackPower);
    v.setPower(rightBackPower);
    strafer();
  }

  /**
   * Describe this function...
   */
  private void strafer() {
    if (gamepad1.dpad_right) {
      ri.setPower(1);
      d.setPower(-1);
      e.setPower(1);
      v.setPower(-1);
    } else if (gamepad1.dpad_left) {
      ri.setPower(-1);
      d.setPower(1);
      e.setPower(-1);
      v.setPower(1);
    } else {
      ri.setPower(0);
      d.setPower(0);
      e.setPower(0);
      v.setPower(0);
    }
  }
}

