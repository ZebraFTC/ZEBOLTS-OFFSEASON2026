package org.firstinspires.ftc.teamcode.Zebolts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp
public class secondtry extends OpMode {

    public static final double LEFT_CLOSED = 0.21;
    public static final double LEFT_OPEN = 0.0;
    public static final double RIGHT_OPEN = 0.21;
    public static final double RIGHT_CLOSED = 0.0;
    private static final Logger log = LoggerFactory.getLogger(secondtry.class);
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor motorArm;

    public Servo clawLeft;
    public Servo clawRight;

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
    public double MANUAL_CHANGE = 3;

    public boolean isMovingToDaPreset = false;

    public boolean leftTriggerWasDown = false;
    public boolean rightTriggerWasDown = false;

    // State variables to reliably track open/closed status without using getPosition()
    public boolean isLeftOpen = false;
    public boolean isRightOpen = false;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        motorArm = hardwareMap.get(DcMotor.class, "ARM");

        clawLeft = hardwareMap.get(Servo.class, "CL");
        clawRight = hardwareMap.get(Servo.class, "CR");

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        motorArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        offsetArm = motorArm.getCurrentPosition();
        armTarget = 0;
    }

    @Override
    public void loop() {
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


        if (Math.abs(gamepad2.right_stick_y) > 0.1) {
            armTarget -= gamepad2.right_stick_y * MANUAL_CHANGE;
            isMovingToDaPreset = false;
        } else if (gamepad2.dpad_up) {
            armTarget += MANUAL_CHANGE;
            isMovingToDaPreset = false;
        } else if (gamepad2.dpad_down) {
            armTarget -= MANUAL_CHANGE;
            isMovingToDaPreset = false;
        }


        if (gamepad2.a) {
            armTarget = 20;
            isMovingToDaPreset = true;
        }
        if (gamepad2.x) {
            armTarget = 480;
            isMovingToDaPreset = true;
        }

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
        } else {
            motorArm.setPower(armPower);
        }

        // Left Claw Toggle (Left Trigger)
        if (gamepad2.left_trigger > 0.5 && !leftTriggerWasDown) {
            isLeftOpen = !isLeftOpen;
            if (isLeftOpen) {
                clawLeft.setPosition(LEFT_OPEN);
            } else {
                clawLeft.setPosition(LEFT_CLOSED);
            }
        }

        // Right Claw Toggle (Right Trigger)
        if (gamepad2.right_trigger > 0.5 && !rightTriggerWasDown) {
            isRightOpen = !isRightOpen;
            if (isRightOpen) {
                clawRight.setPosition(RIGHT_OPEN);
            } else {
                clawRight.setPosition(RIGHT_CLOSED);
            }
        }

        // Open Both Claws (Y Button)
        if (gamepad2.y) {
            isLeftOpen = true;
            isRightOpen = true;
            clawLeft.setPosition(LEFT_OPEN);
            clawRight.setPosition(RIGHT_OPEN);
        }

        leftTriggerWasDown = gamepad2.left_trigger > 0.5;
        rightTriggerWasDown = gamepad2.right_trigger > 0.5;

        telemetry.addData("Arm Target", armTarget);
        telemetry.addData("Arm Position", currentArmPos);
        telemetry.addData("Arm Error", errorArm);
        telemetry.addData("Max Speed", MAX_SPEED);
        telemetry.update();
    }
}