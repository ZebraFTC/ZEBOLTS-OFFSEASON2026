package org.firstinspires.ftc.teamcode.Zebolts;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@TeleOp
public class MecanumDrive extends OpMode {
    public static final double LEFT_CLOSED = .25;
    public static final double LEFT_OPEN = 0;
    public static final double RIGHT_OPEN = .18;
    public static final double RIGHT_CLOSED = 0;
    private static final Logger log = LoggerFactory.getLogger(MecanumDrive.class);
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor motorArm;
    public Servo clawLeft;
    public Servo clawRight;

    private double MAX_SPEED = .6;
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

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        offsetArm = motorArm.getCurrentPosition();
        motorArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armTarget = 0;
    }

    public void loop() {
        //drive inputs
        ySpeed = Math.signum(-gamepad1.left_stick_y) * Math.pow(gamepad1.left_stick_y, 2) * MAX_SPEED;
        xSpeed = Math.signum(gamepad1.left_stick_x) * Math.pow(gamepad1.left_stick_x, 2) * MAX_SPEED;
        turnSpeed = Math.signum(gamepad1.right_stick_x) * Math.pow(gamepad1.right_stick_x, 2) * MAX_SPEED;

        //set drive powers
        frontLeft.setPower(ySpeed + xSpeed + turnSpeed);
        frontRight.setPower(ySpeed - xSpeed - turnSpeed);
        backLeft.setPower(ySpeed - xSpeed + turnSpeed);
        backRight.setPower(ySpeed + xSpeed - turnSpeed);
        //if (gamepad1.left_stick_button)
        //{
            //if (MAX_SPEED <= 1){
            //    MAX_SPEED
            //}
       // }

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
        if (gamepad1.a) {
            armTarget = 0;
        }
        if (gamepad1.b) {
            armTarget = 500;

        }
        if (gamepad1.left_trigger_pressed && leftBumperWasDown)
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

        if (gamepad1.right_trigger_pressed)
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
        if (gamepad1.x)
        {
            clawLeft.setPosition(LEFT_OPEN);
            clawRight.setPosition(RIGHT_OPEN);
        }

        leftBumperWasDown = gamepad1.left_trigger_pressed;
        rightBumperWasDown = gamepad1.right_bumper;
    }
}