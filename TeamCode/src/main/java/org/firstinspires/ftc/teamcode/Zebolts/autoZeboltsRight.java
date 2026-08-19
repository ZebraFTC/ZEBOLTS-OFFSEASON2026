package org.firstinspires.ftc.teamcode.Zebolts;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Drive Right Strafe Auto", group = "autoZebolts")
public class autoZeboltsRight extends LinearOpMode {

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");


        backRight.setDirection(DcMotorSimple.Direction.REVERSE);


        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "initialized");
        telemetry.update();


        waitForStart();
        runtime.reset();


        while (opModeIsActive() && runtime.seconds() < 1.3) { //time

            frontLeft.setPower(-0.8);
            frontRight.setPower(0.8);
            backLeft.setPower(0.8);
            backRight.setPower(-0.8);

            telemetry.addData("path", "runningFor4Seconds", runtime.seconds());
            telemetry.update();
        }


        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);

        telemetry.addData("path", "complete");
        telemetry.update();
    }
}