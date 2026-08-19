package org.firstinspires.ftc.teamcode.Zebolts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp
public class newtry extends OpMode {

    private static final Logger log = LoggerFactory.getLogger(secondtry.class);

    // Drive Motors
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;

    // Arm Motors
    public DcMotor motorArm;
    public DcMotor arm2;

    // Intake Motor
    public DcMotor intake;

    private double MAX_SPEED = 0.6;

    public double ySpeed;
    public double xSpeed;
    public double turnSpeed;

    public double offsetArm;
    public double currentArmPos;
    public double errorArm;

    public double KP = 0.016;
    public double armTarget;
    public double armPower;

    public double MIN_ARM_POWER = 0.05;
    public double ARM_TOLERANCE = 5;

    // New Manual Speeds
    public double MANUAL_CHANGE_REGULAR = 5; //
    public double MANUAL_CHANGE_SLOW = 1.5;  // Slow s

    public boolean isMovingToDaPreset = false;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        motorArm = hardwareMap.get(DcMotor.class, "ARM");
        arm2 = hardwareMap.get(DcMotor.class, "ARM2");
        intake = hardwareMap.get(DcMotor.class, "INTAKE");

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Reverse arm2 so it fights with motorArm in the correct direction
        arm2.setDirection(DcMotorSimple.Direction.REVERSE);

        motorArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        offsetArm = motorArm.getCurrentPosition();
        armTarget = 0;
    }

    @Override
    public void loop() {
        // --- DRIVETRAIN (Gamepad 1) ---
        if (gamepad1.a) {
            MAX_SPEED = 1.0;
        } else if (gamepad1.y) {
            MAX_SPEED = 0.3;
        } else if (gamepad1.x) {
            MAX_SPEED = 0.6;
        }

        ySpeed = Math.signum(-gamepad1.left_stick_y)
                * Math.pow(gamepad1.left_stick_y, 2)
                * MAX_SPEED;

        xSpeed = Math.signum(gamepad1.left_stick_x)
                * Math.pow(gamepad1.left_stick_x, 2)
                * MAX_SPEED;

        turnSpeed = Math.signum(gamepad1.right_stick_x)
                * Math.pow(gamepad1.right_stick_x, 2)
                * MAX_SPEED;

        frontLeft.setPower(ySpeed + xSpeed + turnSpeed);
        frontRight.setPower(ySpeed - xSpeed - turnSpeed);
        backLeft.setPower(ySpeed - xSpeed + turnSpeed);
        backRight.setPower(ySpeed + xSpeed - turnSpeed);

        //
        if (Math.abs(gamepad1.right_stick_y) > 0.1) {
            armTarget -= gamepad1.right_stick_y * MANUAL_CHANGE_REGULAR;
            isMovingToDaPreset = false;
        }
        // Slow speed on D-pad up/down
        else if (gamepad1.dpad_up) {
            armTarget += MANUAL_CHANGE_SLOW;
            isMovingToDaPreset = false;
        } else if (gamepad1.dpad_down) {
            armTarget -= MANUAL_CHANGE_SLOW;
            isMovingToDaPreset = false;
        }

        //
        if (gamepad2.x) {
            armTarget = 480;
            isMovingToDaPreset = true;
        }

        // -
        currentArmPos = motorArm.getCurrentPosition() - offsetArm;
        errorArm = armTarget - currentArmPos;

        if (Math.abs(errorArm) < ARM_TOLERANCE) {
            isMovingToDaPreset = false;
        }

        armPower = errorArm * KP;

        if (isMovingToDaPreset) {
            armPower *= 0.5;
        }

        armPower = Math.max(Math.abs(armPower), MIN_ARM_POWER)
                * Math.signum(armPower);

        armPower = Math.max(-1, Math.min(1, armPower));

        if (Math.abs(errorArm) < ARM_TOLERANCE) {
            motorArm.setPower(0);
            arm2.setPower(0);
        } else {
            motorArm.setPower(armPower);
            arm2.setPower(armPower);
        }

        // --- IN
        // Left tr
        if (gamepad2.left_trigger > 0.1) {
            intake.setPower(1.0); // Intake
        } else if (gamepad2.right_trigger > 0.1) {
            intake.setPower(-1.0); // Outtake
        } else {
            intake.setPower(0.0); // St
        }

        // -
        telemetry.addData("Arm Target", armTarget);
        telemetry.addData("Arm Position", currentArmPos);
        telemetry.addData("Arm Error", errorArm);
        telemetry.addData("Max Speed", MAX_SPEED);
        telemetry.addData("Intake Power", intake.getPower());
        telemetry.update();
    }
}