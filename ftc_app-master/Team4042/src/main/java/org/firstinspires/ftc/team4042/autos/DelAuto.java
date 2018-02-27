package org.firstinspires.ftc.team4042.autos;
import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.File;
import java.io.IOException;
//import com.qualcomm.ftccommon.DbgLog;

@Autonomous(name="DelAuto", group="autos")
@Deprecated
public class DelAuto extends OpMode {
    private Telemetry.Log log;
    public void init() {
        //log = telemetry.log();
        File tfile;
        File ofile;
        String[] autos = new String[] {
                "blue", "bluebottom", "bluejewel", "bluetop", "constants", "ir", "jewel", "proto",
                "red", "redbottom", "redjewel", "redtop", "test"
        };
        for(String auto : autos) {
            tfile = new File(Auto.autoRoot, auto + "-1.txt");
            //log.add("\n\n\n\n\n\nno\n\n\n\n\n\n");
            telemetry.addLine("mebe time");
            if(tfile.exists()) {
                telemetry.addLine("go time");
                //log.add("\n\n\n\n\n\ntest\n\n\n\n\n\n");
                //String f = Environment.getExternalStoragePublicDirectory()
                ofile = new File(Auto.autoRoot,  auto + ".txt");
                ofile.delete();
                ofile = new File(Auto.autoRoot, auto + ".txt");
                tfile.renameTo(ofile);
            }
        }
        telemetry.update();
    }
    public void runOpMode() {

    }
    @Override
    public void loop() {

    }
}