package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OutputController
{
    // Positions for the wrist servo.
    public static final double WRIST_POSITION_SIDEWAYS = 0.45;
    public static final double WRIST_POSITION_INSIDE_ROBOT = 0;
    public static final double WRIST_POSITION_OUTSIDE_ROBOT= 1;
    public static final long WRIST_POSITION_DURATION = 200;
    public static final long ELBOW_POSITION_DURATION = 900;

    // Positions for the elbow servo.
    public static final double ELBOW_POSITION_INSIDE_ROBOT = 0;
    public static final double ELBOW_POSITION_OUTSIDE_ROBOT_PARALLEL = 0.8;
    public static final double ELBOW_POSITION_OUTSIDE_ROBOT_AND_DOWN = 0.85;
    public static final double ELBOW_POSITION_OUTSIDE_ROBOT_AND_PARTIALLY_UP = 0.65;

    public static final double LIFT_POWER_UP = -1;
    public static final double LIFT_TENSION_POWER = -0.15;
    public static final long LIFT_TENSION_DURATION = 1000;
    // This is negative to move down and has a much smaller absolute value since gravity
    // helps us down.
    private static final double LIFT_POWER_DOWN = 0.5;

    // How long to move the lift up and then down when moving the clamp in or out of the robot.
    private static final long MOVE_CLAMP_LIFT_DURATION = 150;


    public DcMotor motorLift;
    public Servo elbowR;
    public Servo elbowL;
    public Servo wrist;
    public CRServo clamp;
    int position = 1;

    HardwareMap hwMap;
    Telemetry telemetry;

    public void init(HardwareMap hwMap, Telemetry telemetry)
    {
        this.hwMap = hwMap;
        this.telemetry = telemetry;

        motorLift = hwMap.dcMotor.get("motorLift");

        // elbows flip
        // 1 and 2 should work simultaneously
        elbowR = hwMap.servo.get("elbow1");
        elbowL = hwMap.servo.get("elbow2");

        // This is necessary so that we can detect when the wrist is in and out of the robot.
        setElbowPositions(ELBOW_POSITION_INSIDE_ROBOT);

        // wrist rotates block
        wrist = hwMap.servo.get("wrist");
        // clamp opens and closes on block
        clamp = hwMap.crservo.get("clamp");

        telemetry.addData("Output Servo Initialization Complete", "");

        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLift.setPower(LIFT_TENSION_POWER);
        sleep(LIFT_TENSION_DURATION);
        motorLift.setPower(0);

        telemetry.addData("Output Motor Initialization Complete", "");
        telemetry.update();
    }

    private void setLiftPower(double liftPower) {
        motorLift.setPower(liftPower);
        telemetry.addData("Lift Power", "" + liftPower);
        telemetry.update();
    }

    public void startMoveLiftUp() {
        setLiftPower(LIFT_POWER_UP);
    }

    public void startMoveLiftDown() {
        setLiftPower(LIFT_POWER_DOWN);
    }

    public void stopLift() {
        setLiftPower(0);
    }

//
//
//    public void moveToPosition(int newPos)
//    {
//        if (newPos == 1)
//        {
//            // inside
//
//            telemetry.addData("" + position, "");
//
//            if (position == 3)
//            {
//
//                threeToTwo();
//            }
//
//            twoToOne();
//
//            position = 1;
//
//            telemetry.addData("" + position, "");
//            telemetry.update();
//        }
//
//        else if (newPos == 2)
//        {
//            // outside high
//
//            telemetry.addData("" + position, "");
//
//            // go outward from position 1
//            if (position == 1)
//                oneToTwo();
//
//            // raise from position 3
//            if (position == 3)
//                threeToTwo();
//
//            position = 2;
//
//            telemetry.addData("" + position, "");
//            telemetry.update();
//
//        }
//
//        else if (newPos == 3)
//        {
//            // outside low
//            // lower from position 2;
//
//            telemetry.addData("" + position, "");
//
//            if (position == 1) {
//                oneToTwo();
//
//                setLiftPower(-0.5);
//                sleep(1000);
//                setLiftPower(0);
//
//                twoToThree();
//            }
//
//            if (position == 2)
//                twoToThree();
//
//            position = 3;
//
//            telemetry.addData("" + position, "");
//            telemetry.update();
//        }
//    }
//
//    // since intake not working right now, this will only be used in auto to go out
//    public void oneToTwo() {
//        startClosingClamp();
//        sleep(1000);
//
//        setLiftPower(0.5);
//        sleep(1000);
//        setLiftPower(0);
//
//        setElbowPositions(0.3);
//        wrist.setPosition(0.5); // 90 degrees
//        setElbowPositions(0.7);
//        wrist.setPosition(1);
//    }
//
//    // won't use in AML2
//    public void twoToOne() {
//        setLiftPower(0.5);
//        sleep(1000);
//        setLiftPower(0);
//
//        wrist.setPosition(0.5);
//        setElbowPositions(0.3);
//        wrist.setPosition(0);
//
//        setLiftPower(-0.5);
//        sleep(1000);
//        setLiftPower(0);
//
//        setElbowPositions(0);
//
//        startOpeningClamp();
//        sleep(500);
//    }
//
//    public void threeToTwo() {
//        startClosingClamp();
//        sleep(1000);
//
//        setElbowPositions(0.7);
//    }
//
//    public void twoToThree()
//    {
//        setElbowPositions(1);
//
//        startOpeningClamp();
//        sleep(1000);
//    }

    public void openClampFully() {
        startOpeningClamp();
        sleep(3000);
        stopClamp();
    }

    public void closeClampFully() {
        startClosingClamp();
        sleep(3500); // This is a little bigger than on open to be sure we close on the stone.
        stopClamp();
    }

    public void closeClampPartway() {
        // Closes clamp enough to grab a stone longways
        startClosingClamp();
        sleep(1000);
        stopClamp();
    }
    public void startOpeningClamp()
    {
        clamp.setPower(1);
    }

    public void startClosingClamp()
    {
        clamp.setPower(-1);
    }

    public void stopClamp()
    {
        clamp.setPower(0);
    }

    private void printElbows(String when, StringBuilder history) {
        history.append(when + " L elbow=" + getLeftElbowPos() + "  R elbow=" + getRightElbowPos() + "\n");
        telemetry.addData(history.toString(), "");
        telemetry.update();
    }

    public void test()
    {
//        long stopTime = System.currentTimeMillis() + 25000;


        StringBuilder history = new StringBuilder();
        printElbows("At start", history);

        sleep(4000);

        moveElbowToPosition(ELBOW_POSITION_OUTSIDE_ROBOT_PARALLEL);

        sleep(4000);
        printElbows("After outside", history);

        moveElbowToPosition(ELBOW_POSITION_INSIDE_ROBOT);

        sleep(4000);
        printElbows("After inside", history);

        sleep(10000);


//        moveClampOutOfRobot();
    }

    // This needs to be faster than 5 seconds.
    public void moveClampOutOfRobot()
    {
        long startMillis = System.currentTimeMillis();

        if (! isClampInRobot())
        {
            telemetry.addData("WARNING. Clamp is not in robot!" +
                    " L elbow=" + getLeftElbowPos() + "  R elbow=" + getRightElbowPos(), "");
            telemetry.update();
            return;
        }

        // Raise the lift some.
        startMoveLiftUp();
        sleep(MOVE_CLAMP_LIFT_DURATION);
        stopLift();

        moveWristToPosition(WRIST_POSITION_SIDEWAYS);

        moveElbowToPosition(ELBOW_POSITION_OUTSIDE_ROBOT_PARALLEL);

        moveWristToPosition(WRIST_POSITION_OUTSIDE_ROBOT);

        // Lower the lift back down.
        startMoveLiftDown();
        sleep(MOVE_CLAMP_LIFT_DURATION);
        stopLift();

        long durationMillis = System.currentTimeMillis() - startMillis;
        telemetry.addData("Moving clamp out took " + durationMillis + " ms", "");
        telemetry.update();
    }

    public void moveClampIntoRobot()
    {
        if (! isClampOutOfRobot())
        {
            telemetry.addData("WARNING. Clamp is not in robot!" +
                    " L elbow=" + getLeftElbowPos() + "  R elbow=" + getRightElbowPos(), "");
            telemetry.update();
            return;
        }

        // Raise the lift some.
        startMoveLiftUp();
        sleep(MOVE_CLAMP_LIFT_DURATION);
        stopLift();

        moveWristToPosition(WRIST_POSITION_SIDEWAYS);

        moveElbowToPosition(ELBOW_POSITION_INSIDE_ROBOT);

        moveWristToPosition(WRIST_POSITION_INSIDE_ROBOT);

        // Lower the lift back down.
        startMoveLiftDown();
        sleep(MOVE_CLAMP_LIFT_DURATION);
        stopLift();
    }

    private boolean isClampInRobot()
    {
        // elbowL is not connected.
//        return getLeftElbowPos() < 0.4;
        // Left starts at 0 and right starts at 1

        return (getRightElbowPos() < 0.4 || getLeftElbowPos() < 0.4);
    }

    private boolean isClampOutOfRobot()
    {
        // elbowL is not connected.
//        return getLeftElbowPos() > 0.6;

        return (getRightElbowPos() > 0.6 || getLeftElbowPos() > 0.6);
    }

    public void moveWristToPosition(double pos)
    {
        wrist.setPosition(pos);

        sleep(WRIST_POSITION_DURATION);
    }

    public void moveElbowToPosition(double pos)
    {
        setElbowPositions(pos);

        sleep(ELBOW_POSITION_DURATION);
    }


    private double getLeftElbowPos()
    {
        return elbowL.getPosition();
    }

    private double getRightElbowPos()
    {
        // The right elbow positions are flipped, so we have to convert them.
        return convertRightPosition(elbowR.getPosition());
    }

    private double convertRightPosition(double pos)
    {
        return 1- pos;
    }

    public void setElbowPositions(double pos)
    {
        telemetry.addData("Move to", pos + "");
        telemetry.addData("At", elbowL.getPosition() + "");
        telemetry.update();

        double posL = pos;
        double posR = convertRightPosition(pos);

        elbowL.setPosition(posL);
        elbowR.setPosition(posR);
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}