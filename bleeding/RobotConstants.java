package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.config.Config;
@Config
public class RobotConstants {
    //Servos (update Before Full Hardware testing)
    public static double INTAKE_OPEN = 0.82;
    public static double INTAKE_CLOSED = 0.35;
    public static double DUMP_STOWED = 0.10;
    public static double DUMP_ACTIVE = 0.90;
    //Slide, coded with encoder in ticks
    public static int SLIDE_GROUND = 0;
    public static int SLIDE_INTAKE = 500;
    public static int SLIDE_SCORE_LOW = 1000;
    public static int SLIDE_SCORE_HIGH = 1500;
    public static int SLIDE_MAX_SAFE = 1900; // 95% of physica extension
    //Sensors detecting game pieces
    public static double GAME_PIECE_DISTANCE_CM = 5.0;
    public static boolean LIMIT_SWITCH_INVERTED = true;    
    //PID dashboard controllable
    public static double SLIDE_P = 10.0;
    public static double SLIDE_I = 0.5;
    public static double SLIDE_D = 1.0;
}
