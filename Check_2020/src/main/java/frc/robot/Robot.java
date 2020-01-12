/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.util.snail_vision.*;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.networktables.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import java.util.*;
import edu.wpi.first.wpilibj.*;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  WPI_TalonSRX FrontRight;
  WPI_TalonSRX FrontLeft;
  WPI_TalonSRX BackRight;
  WPI_TalonSRX BackLeft;
  SpeedControllerGroup Right;
  SpeedControllerGroup Left;
  DifferentialDrive DriveTrain;
  XboxController Controller;
  double driveSpeed;
  double turnSpeed;
  SnailVision vis;

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    FrontLeft = new WPI_TalonSRX(7);
    BackLeft = new WPI_TalonSRX(6);
    BackRight = new WPI_TalonSRX(1);
    FrontRight = new WPI_TalonSRX(2);

    Right = new SpeedControllerGroup(FrontRight, BackRight);
    Left = new SpeedControllerGroup(FrontLeft, BackLeft);

    DriveTrain = new DifferentialDrive(Left, Right);

    Controller = new XboxController(0);

    driveSpeed = 0;
    turnSpeed = 0;

    vis = new SnailVision(false);
    // Angle correct
    // vis.ANGLE_CORRECT_F = 2;
    vis.ANGLE_CORRECT_P = -0.01f;
    vis.ANGLE_CORRECT_MIN_ANGLE = 0.05f;
    // Distance
    vis.DISTANCE_ESTIMATION_METHOD = "trig";
    vis.TARGETS.add(new Target(48, 48));
    SmartDashboard.putNumber("P", -0.1F);
    SmartDashboard.putNumber("F", 0.2);
  }

  @Override
  public void robotPeriodic() {
    
  }


  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopPeriodic() {
    vis.ANGLE_CORRECT_P = SmartDashboard.getNumber("P", -0.1F);
    vis.ANGLE_CORRECT_F = SmartDashboard.getNumber("F", 0.2F);
    vis.networkTableFunctionality(NetworkTableInstance.getDefault().getTable("limelight"));
    // Basic Teleop Drive Code
    driveSpeed = 0;
    turnSpeed = 0;
    if(Controller.getAButton()) {
          double y = Controller.getY(GenericHID.Hand.kLeft);
          double x = Controller.getX(GenericHID.Hand.kLeft);
          driveSpeed += -y;
          turnSpeed += x;
      } 
      else if(Controller.getBumper(GenericHID.Hand.kLeft)) {
          double y = Controller.getY(GenericHID.Hand.kLeft);
          double x = Controller.getX(GenericHID.Hand.kRight);
          driveSpeed += -y;
          turnSpeed += x;
      } 
      else if(Controller.getBumper(GenericHID.Hand.kRight)) {
          double x = Controller.getX(GenericHID.Hand.kLeft);
          double y = Controller.getY(GenericHID.Hand.kRight);
          driveSpeed += -y;
          turnSpeed += x;
      }
      if(Controller.getBButton()){
        turnSpeed += vis.angleCorrect();
        // driveSpeed += vis.getInDistance(vis.TARGETS.get(0));
      }

     DriveTrain.arcadeDrive(driveSpeed, turnSpeed);
     SmartDashboard.putNumber("driveSpeed", driveSpeed);
     SmartDashboard.putNumber("turnSpeed", turnSpeed);
     SmartDashboard.putNumber("dist", vis.trigDistance(vis.TARGETS.get(0)));
    //  System.out.println(turnSpeed);
  }

  @Override
  public void testPeriodic() {
  }
}
