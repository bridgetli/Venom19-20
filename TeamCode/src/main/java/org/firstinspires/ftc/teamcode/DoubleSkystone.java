package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.RobotLog;


@Autonomous(name = "DoubleSkystone", group = "6209")
public class DoubleSkystone extends BaseLinearOpMode
{

        boolean configOnly = false;
        boolean isStartingBlue = true;
        boolean parkOnSide = true;

        // TODO: test these distances
        // TODO: change this to work with the new mechanism

        private static int MOVE_OFF_OF_WALL_DISTANCE = 2;
        private static int STRAFE_TO_POS_1_OR_2_DIST = 9;
        private static int STRAFE_TO_POS_3_DIST = 3;
        private static int MOVE_FORWARD_TO_GET_1ST_STONE_DIST = 27;
        private static int MOVE_BACKWARD_AFTER_GRAB_DIST = 12;
        private static int STRAFE_UNDER_BRIDGE_1ST_STONE_DIST = 48;
        private static int STRAFE_BACK_TO_GET_STONE_45_DIST = 72;
        private static int MOVE_FORWARD_TO_GET_STONE_45_DIST = 13;
        private static int MOVE_FORWARD_TO_GET_STONE_6_DIST = 11;
        private static int STRAFE_UNDER_BRIDGE_2ND_STONE_DIST = 73;
        private static int STRAFE_TO_PARK_DIST = 20;

        int position = 3;

        @Override
        public void runOpMode()
        {
            // using park on side to change this for now
            // but eventually we will use the value returned from findSkystonePosition();

            super.initialize(true);
            configMode();

            telemetry.addData(">", "Press Play to start op mode");
            telemetry.update();
            waitForStart();

            if (opModeIsActive()) {
                if (position == 3) {
                    driveTo3();
                    lowerHook();
                    STRAFE_UNDER_BRIDGE_1ST_STONE_DIST += 6;
                    deposit123();
                    driveTo456();
                    STRAFE_UNDER_BRIDGE_2ND_STONE_DIST += 8;
                    deposit456();
                    park();
                }

                else {
                    driveTo1Or2();
                    lowerHook();
                    deposit123();
                    driveTo456();
                    deposit456();
                    park();
                    // make compatible with blue

                }
            }

        }



        public void lowerHook()
        {
            if (isStartingBlue) {
                if (position == 1)
                    robot.hooks.lowerOneHook("R");
                else
                    robot.hooks.lowerOneHook("L");
            } else {
                if (position == 1)
                    robot.hooks.lowerOneHook("L");
                else
                    robot.hooks.lowerOneHook("R");
            }
        }
        // EVERYTHING IS FLIPPED BECAUSE ROBOT IS BACKWARDS
        public void driveTo1Or2()
        {
            if(isStartingBlue)
            {
                moveBackwardByInches(0.5, MOVE_OFF_OF_WALL_DISTANCE);
                strafeRightByInches(0.85, STRAFE_TO_POS_1_OR_2_DIST);
                moveBackwardByInches(1, MOVE_FORWARD_TO_GET_1ST_STONE_DIST);
            } else{
                moveBackwardByInches(0.5, MOVE_OFF_OF_WALL_DISTANCE);
                strafeLeftByInches(0.85, STRAFE_TO_POS_1_OR_2_DIST);
                moveBackwardByInches(1, MOVE_FORWARD_TO_GET_1ST_STONE_DIST);
            }

        }

        public void driveTo3()
        {
            if(isStartingBlue)
            {
                moveBackwardByInches(0.5, MOVE_OFF_OF_WALL_DISTANCE);
                strafeRightByInches(0.85, STRAFE_TO_POS_3_DIST);
                moveBackwardByInches(1, MOVE_FORWARD_TO_GET_1ST_STONE_DIST);
            } else {
                moveBackwardByInches(0.5, MOVE_OFF_OF_WALL_DISTANCE);
                strafeLeftByInches(0.85, STRAFE_TO_POS_3_DIST);
                moveBackwardByInches(1, MOVE_FORWARD_TO_GET_1ST_STONE_DIST);
            }
        }

        public void driveTo456 ()
        {
            if(isStartingBlue) {
                if (position == 3) {
                    strafeLeftByInches(1, STRAFE_BACK_TO_GET_STONE_45_DIST + 8);
                    moveBackwardByInches(1, MOVE_FORWARD_TO_GET_STONE_6_DIST);
                } else {
                    strafeLeftByInches(1, STRAFE_BACK_TO_GET_STONE_45_DIST);
                    moveBackwardByInches(1, MOVE_FORWARD_TO_GET_STONE_45_DIST);
                }
            }
            else {
                if(position == 3) {
                    strafeRightByInches(1, STRAFE_BACK_TO_GET_STONE_45_DIST + 8);
                    moveBackwardByInches(1, MOVE_FORWARD_TO_GET_STONE_6_DIST);
                }
                else {
                    strafeRightByInches(1, STRAFE_BACK_TO_GET_STONE_45_DIST);
                    moveBackwardByInches(1, MOVE_FORWARD_TO_GET_STONE_45_DIST);
                }
            }

            lowerHook();
        }

        // EVERYTHING IS FLIPPED BECAUSE ROBOT IS BACKWARDS!
        public void deposit123()
        {
            moveForwardByInches(0.85, MOVE_BACKWARD_AFTER_GRAB_DIST);

            if (isStartingBlue)
                strafeRightByInches(1, STRAFE_UNDER_BRIDGE_1ST_STONE_DIST);
            else
                strafeLeftByInches(1, STRAFE_UNDER_BRIDGE_1ST_STONE_DIST);

            robot.hooks.raiseHooks();
        }

        public void deposit456()
        {
            moveForwardByInches(0.85, MOVE_BACKWARD_AFTER_GRAB_DIST);

            if (isStartingBlue)
                strafeRightByInches(1, STRAFE_UNDER_BRIDGE_2ND_STONE_DIST);
            else
                strafeLeftByInches(1, STRAFE_UNDER_BRIDGE_2ND_STONE_DIST);

            robot.hooks.raiseHooks();
        }

        public void park() {
            if (isStartingBlue)
                strafeLeftByInches(1, STRAFE_TO_PARK_DIST);
            else
                strafeRightByInches(1, STRAFE_TO_PARK_DIST);
        }

        public void runOpMode2()
        {
            super.initialize(true);
            configMode();

            telemetry.addData(">", "Press Play to start op mode");
            telemetry.update();
            waitForStart();

            if (opModeIsActive())
            {
                moveForwardByInches(0.6, 14);

                // IF IT'S THE FIRST STONE
                if (isSkystone(isStartingBlue)) {
                    moveClampOutInAuto();
                    grabAndTurn(isStartingBlue);
                    moveForwardByInches(1, 30);
                    dropStone();

                    // return to get the 4th stone
                    getNextStone(isStartingBlue, 4);
                    moveForwardByInches(1, 54);
                    dropStone();

                    parkOnlySkystone();
                }

                else
                {
                    // goes to the next stone
                    strafeToStone(isStartingBlue);

                    // IF IT'S THE SECOND STONE
                    if(isSkystone(isStartingBlue)) {
                        moveClampOutInAuto();
                        grabAndTurn(isStartingBlue);
                        moveForwardByInches(1, 38);
                        dropStone();

                        getNextStone(isStartingBlue, 5);
                        moveForwardByInches(1, 62);
                        dropStone();

                        parkOnlySkystone();
                    }
                    else
                    {
                        // goes to the next stone
                        strafeToStone(isStartingBlue);
                        moveClampOutInAuto();
                        grabAndTurn(isStartingBlue);
                        moveForwardByInches(1, 46);

                        // Can we get the 6th stone???
                        getNextStone(isStartingBlue, 6);
                        moveForwardByInches(1, 70);
                        dropStone();

                        parkOnlySkystone();
                    }
                }
            }
        }

        public void configMode() {
            String lastModes = "";
            telemetry.addData("Entering " , "ConfigMode");
            telemetry.update();
            do {
                if (VenomUtilities.isValueChangedAndEqualTo("1.y", gamepad1.y, true))
                    isStartingBlue = !isStartingBlue;

                if (VenomUtilities.isValueChangedAndEqualTo("1.a", gamepad1.a, true))
                    parkOnSide = !parkOnSide;

                logConfigModes(true);
            }

            while (!gamepad1.right_bumper && !isStarted() &&  !isStopRequested());
            telemetry.addData("ConfigMode" , lastModes);
            telemetry.update();

            RobotLog.i("configMode() stop");
        }

        private String lastModes="";
        void logConfigModes(boolean update) {
            String modes="";
            //  modes+="Alliance="+(isRedAlliance?"Red":"Blue");
            modes+=", Starting="+(isStartingBlue?"Blue":"Red");
            modes+=", Starting="+(parkOnSide?"Side":"Center");


            telemetry.addData("Alliance (Y)", isStartingBlue?"Blue":"Red");
            telemetry.addData("Park Location (A)", parkOnSide?"Side":"Center");

            if (configOnly) telemetry.addData("ConfigMode" , "Press right bumper to leave config mode.");
            if (update) telemetry.update();

            if (!modes.equals(lastModes)) {
                RobotLog.i(modes);
                lastModes=modes;
            }
        }

        public void parkOnlySkystone() {

            if (isStartingBlue) {
                strafeRightByInches(0.7, 3);
            } else {
                strafeLeftByInches(0.7, 3);
            }
            moveBackwardByInches(0.7, 20);
            /**if (parkOnSide) {
             moveBackwardByInches(0.7, 20);
             } else {
             moveBackwardByInches(0.7, 20);
             }**/
        }


        // TODO: do we need to hit the wall to reset ourselves?
    public void getNextStone(boolean blue, int numStone) {
        moveBackwardByInches(0.75, 12);
        if (blue) {
            // moves backwards before it turns so the clamp doesn't hit the sky bridge
            // turn counter clockwise 90 (back to starting yaw)
            rotateToAbsoluteYaw(0);

            // TODO: FIGURE OUT THESES DISTANCES!!!
            if (numStone == 4)
                strafeRightByInches(1, 26);

            // gets the 5th either way bc we can't get the 6th i don't think
            else
                strafeRightByInches(1, 32);

            grabAndTurn(true);

        } else {
            // turn clockwise 90 (back to starting yaw)
            rotateToAbsoluteYaw(0);
            // strafe to correct stone
            if (numStone == 4)
                strafeLeftByInches(1, 26);

            // gets the 5th either way bc we can't get the 6th i don't think
            else
                strafeLeftByInches(1, 32);

            grabAndTurn(false);
        }
    }

}
