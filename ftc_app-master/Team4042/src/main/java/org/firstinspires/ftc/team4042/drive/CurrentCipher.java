package org.firstinspires.ftc.team4042.drive;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Dylan on 3/31/2018.
 */

public class CurrentCipher {
    private Cryptobox.Cipher currentTarget;
    private Telemetry telemetry;

    public CurrentCipher(Cryptobox.Cipher startTarget, Telemetry telemetry) {
        currentTarget = startTarget;
        this.telemetry = telemetry;
    }

    public CurrentCipher(Telemetry telemetry) {
        this(Cryptobox.Cipher.BROWN_SNAKE, telemetry);
    }

    public boolean verify(int y, int x, Cryptobox.GlyphColor color) {
        return currentTarget.getGlyphMap()[x][y].equals(color);
    }

    public Cryptobox.Cipher changeCipher(Cryptobox.GlyphColor[][] currentCipher) {
        if(isValidCipher(currentCipher, currentTarget)) {
            return currentTarget;
        }
        for(Cryptobox.Cipher cipher: Cryptobox.Cipher.values()) {
            //telemetry.log().add("" + cipher + "  " + isValidCipher(currentCipher, cipher));
            if(!cipher.equals(Cryptobox.Cipher.NONE) && isValidCipher(currentCipher, cipher)) {
                currentTarget = cipher;
                return cipher;
            }
    }
        currentTarget = Cryptobox.Cipher.NONE;
        return Cryptobox.Cipher.NONE;
    }

    public Cryptobox.Cipher getCurrentTarget() {
        return currentTarget;
    }

    private boolean isValidCipher(Cryptobox.GlyphColor[][] currentCipher, Cryptobox.Cipher targetCipher) {
        if(targetCipher.equals(Cryptobox.Cipher.NONE)) {
            return false;
        }
        for(int ix = 0; ix < currentCipher.length; ix++){
            boolean endOfColumn = false;
            for(int iy = currentCipher[0].length - 1; iy >= 0; iy--){
                //telemetry.log().add("current: " + currentCipher[ix][iy] + "target: " + targetCipher.getGlyphMap()[ix][iy] + " " + currentCipher[ix][iy].equals(targetCipher.getGlyphMap()[ix][iy]));
                if(!(currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE) || currentCipher[ix][iy].equals(targetCipher.getGlyphMap()[ix][iy]))) {
                    return false;
                } else if(!currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE)) {
                    endOfColumn = true;
                } else if(endOfColumn && currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE)) {
                    return false;
                }
            }
        }
        return true;
    }
}
