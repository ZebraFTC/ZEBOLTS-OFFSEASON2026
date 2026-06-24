package org.firstinspires.ftc.teamcode.Zebolts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class MecanumDrive extends OpMode {
    public static final double LEFT_OPEN = .2;
    public static final double LEFT_CLOSED = 0;
    public static final double RIGHT_CLOSED = .2;
    public static final double RIGHT_OPEN = 0;
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor motorArm;
    public Servo clawLeft;
    public Servo clawRight;

    final double MAX_SPEED = 1;
    public double ySpeed;
    public double xSpeed;
    public double turnSpeed;
    public double offsetArm;
    public double currentArmPos;
    public double errorArm;
    public double KP = 0.01;
    public double armTarget;
    public double armPower;
    public double MIN_ARM_POWER = .05;
    public double ARM_TOLERANCE = 5;
    public double MANUAL_CHANGE = 1;
    public boolean leftBumperWasDown;
    public boolean rightBumperWasDown;


    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class,"FR");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class,"BL");
        motorArm = hardwareMap.get(DcMotor.class, "ARM");
        clawLeft = hardwareMap.get(Servo.class, "CL");
        clawRight = hardwareMap.get(Servo.class, "CR");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        offsetArm = motorArm.getCurrentPosition();
        motorArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armTarget = 0;
    }

    public void loop()
    {
        //drive inputs
        ySpeed = Math.signum(-gamepad1.left_stick_y) * Math.pow(gamepad1.left_stick_y, 2) * MAX_SPEED;
        xSpeed = Math.signum(gamepad1.left_stick_x) * Math.pow(gamepad1.left_stick_x, 2) * MAX_SPEED;
        turnSpeed = Math.signum(gamepad1.right_stick_x) * Math.pow(gamepad1.right_stick_x, 2) * MAX_SPEED;

        //set drive powers
        frontLeft.setPower(ySpeed + xSpeed + turnSpeed);
        frontRight.setPower(ySpeed - xSpeed - turnSpeed);
        backLeft.setPower(ySpeed - xSpeed + turnSpeed);
        backRight.setPower(ySpeed + xSpeed - turnSpeed);

        //arm logic
        currentArmPos = motorArm.getCurrentPosition() - offsetArm;
        errorArm = armTarget - currentArmPos;
        armPower = errorArm * KP;
        armPower = Math.max(Math.abs(armPower), MIN_ARM_POWER) * Math.signum(armPower);
        if (Math.abs(errorArm) < ARM_TOLERANCE)
        {
            motorArm.setPower(0);
        }
        else
        {
            motorArm.setPower(armPower);
        }
        telemetry.addData("error", errorArm);
        telemetry.addData("Target", armTarget);
        telemetry.addData("CurPos", currentArmPos);

        //arm position stuff
        if (gamepad1.dpad_up)
        {
            armTarget += MANUAL_CHANGE;
        } else if (gamepad1.dpad_down)
        {
            armTarget -= MANUAL_CHANGE;
        }

        if (gamepad1.left_bumper && !leftBumperWasDown)
        {
            if(clawLeft.getPosition() == LEFT_CLOSED)
            {
                clawLeft.setPosition(LEFT_OPEN);
            }
            else
            {
                clawLeft.setPosition(LEFT_CLOSED);
            }
        }

        if (gamepad1.right_bumper && !rightBumperWasDown)
        {
            if(clawRight.getPosition() == RIGHT_CLOSED)
            {
                clawRight.setPosition(RIGHT_OPEN);
            }
            else
            {
                clawRight.setPosition(RIGHT_CLOSED);
            }
        }

        leftBumperWasDown = gamepad1.left_bumper;
        rightBumperWasDown = gamepad1.right_bumper;
    }
}