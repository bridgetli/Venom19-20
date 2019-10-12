package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.graphics.Color.blue;
import static android.graphics.Color.red;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

@Autonomous
public class CustomLinearOpMode extends LinearOpMode {


    protected static final String VUFORIA_KEY = "";
    protected VuforiaLocalizer vuforia;
    protected TFObjectDetector tfod;

    // Speed
    double left = 1.00;

    // AUTO
    // Declare motors
    public DcMotor motorFR;
    public DcMotor motorFL;
    public DcMotor motorBR;
    public DcMotor motorBL;
    public DcMotor motorIntakeL;
    public DcMotor motorIntakeR;
    public DcMotor motorLift;
    public DcMotor motorOutput;

    // Declare servos
    public CRServo servoClamp;
    public CRServo servoTwist;
    public Servo servoHookL;
    public Servo servoHookR;

    ModernRoboticsI2cRangeSensor rangeSensorB;

    ElapsedTime eTime;
    protected ElapsedTime time = new ElapsedTime();

    IMU imu;

    String alliance;
    String action;
    String park;

    @Override
    public void runOpMode() throws InterruptedException
    {
        initialize();
        initVuforia();

        setParameters();

        if (action.equals("foundation") && alliance.equals("blue")) {
            runOpModeFoundationBlue();
        } else if (action.equals("foundation") && alliance.equals("red")) {
            runOpModeFoundationRed();
        } else if (action.equals("Skystone") && alliance.equals("blue")) {
            runOpModeSkystoneBlue();
        } else if (action.equals("Skystone") && alliance.equals("red")) {
            runOpModeSkystoneRed();
        }
    }

    public void runOpModeFoundationBlue() throws InterruptedException
    {
        // go to platform
        movePlatform();
        // go to side, get out of the way
        park();
    }

    public void runOpModeFoundationRed() throws InterruptedException
    {
        // go to platform
        movePlatform();
        // go to side, get out of the way
        park();
    }

    public void runOpModeSkystoneBlue() throws InterruptedException
    {
        // go to stones
        findSkystone();
        // go across line
        dropStone();
        // return to saved location (3 blocks distance from first Skystone)
        findSkystone();
        // go across line
        dropStone();
        park();
    }

    public void runOpModeSkystoneRed() throws InterruptedException
    {
        // go to stones
        findSkystone();
        // go across line
        dropStone();
        // return to saved location (3 blocks distance from first Skystone)
        findSkystone();
        // go across line
        dropStone();
        park();
    }

    public void setParameters() {

        while (opModeIsActive()) {
            if (gamepad1.x)
                alliance = "blue";
            if (gamepad1.b)
                alliance = "red";
            if (gamepad1.a)
                action = "foundation";
            if (gamepad1.y)
                action = "Skystone";
            if (gamepad1.dpad_left)
                park = "side";
            if (gamepad1.dpad_right)
                park = "center";
        }
    }

    public void initialize() {

        motorFR = hardwareMap.dcMotor.get("motorFR");
        motorFL = hardwareMap.dcMotor.get("motorFL");
        motorBR = hardwareMap.dcMotor.get("motorBR");
        motorBL = hardwareMap.dcMotor.get("motorBL");
        motorIntakeL = hardwareMap.dcMotor.get("motorIntakeL");
        motorIntakeR = hardwareMap.dcMotor.get("motorIntakeR");
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorOutput = hardwareMap.dcMotor.get("motorOutput");

        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorIntakeL.setDirection(DcMotorSimple.Direction.REVERSE);

        resetEncoders();

        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorIntakeL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorIntakeR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorOutput.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stopMotors();

        telemetry.addData("Motor Initialization Complete", "");

        // servos vs continuous servos?
        servoClamp = hardwareMap.crservo.get("servoClamp");
        servoTwist = hardwareMap.crservo.get("servoTwist");
        servoHookL = hardwareMap.servo.get("servoHookL");
        servoHookR = hardwareMap.servo.get("servoHookR");

        // set servo positions

        telemetry.addData("Servo Initialization Complete", "");

        rangeSensorB = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensorB");

        imu = new IMU(hardwareMap.get(BNO055IMU.class, "imu"));
        imu.IMUinit(hardwareMap);

        telemetry.addData("IMU Initialization Complete", "");
    }

    private void initVuforia()
    {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        vuforia.setFrameQueueCapacity(1);
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.leftDrive.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightDrive.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            robot.leftDrive.setTargetPosition(newLeftTarget);
            robot.rightDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            robot.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftDrive.setPower(Math.abs(speed));
            robot.rightDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftDrive.isBusy() && robot.rightDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        robot.leftDrive.getCurrentPosition(),
                        robot.rightDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            stopMotors();

            // Turn off RUN_TO_POSITION
            robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void stopMotors() {
        motorBL.setPower(0);
        motorFL.setPower(0);
        motorBR.setPower(0);
        motorFR.setPower(0);
    }

    public double getDistB() {
        double dist = rangeSensorB.getDistance(DistanceUnit.INCH);
        while ((dist > 200 || Double.isNaN(dist)) && opModeIsActive()) {
            dist = rangeSensorB.getDistance(DistanceUnit.INCH);
        }
        return dist;
    }

    public void resetEncoders() {
        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorIntakeL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorIntakeR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorOutput.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void findSkystone() throws InterruptedException {
        // Tensor Flow stuff: detect Skystones

        // use intake to get Skystone
        // scans from middle --> right
        // if !Skystone
            // keep strafing
        // if Skystone
            // get Skystone

        // Scan first stones from left to right until first Skystone detected
        // If in first three, come back for second Skystone
        // Scan last three stones
        // If second Skystone still there, get
        // If in last three, ally already got first Skystone, get second
        TensorFlowSkyStone tf = new TensorFlowSkyStone();
        tf.runOpMode();

        // Will change to certain amount of distance
        while (opModeIsActive()) {
            tfod = tf.getTfod();
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel().equals("Skystone")) {
                    // if within range
                    // left: 140-200
                    // right: 560-640

                    // stopMotors();
                    intakeStone();
                }
            }
        }
    }

    public void intakeStone() throws InterruptedException
    {
        // use output to get stone
        encoderDrive(left, -12, -12, 4.0);
    }

    public void movePlatform()
    {
        // Move platform into depot
        // Need to figure out mechanism (hooks to drag in)
    }

    public void dropStone()
    {
        // Drop Stone across the tape
    }

    public void placeStone()
    {
        // Place Stone on the foundation
        // Don't have to stack, can just throw on
        // For LM 1, only pushing across tape: use dropStone()
    }

    public void stackStone()
    {
        // Stacks the stones
        // Not needed in auto for AML 1
    }


    public void park()
    {
        if (park.equals("center")){
            // park in center
        } else if (park.equals("side")){
            // park in side
        }
        // Call method when t =
    }
}