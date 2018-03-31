package org.firstinspires.ftc.team4042.drive;

/**
 * Created by Dylan on 3/31/2018.
 */

public class CurrentCipher {
    private Cryptobox.Cipher currentTarget;

    public CurrentCipher(Cryptobox.Cipher startTarget) {
        currentTarget = startTarget;
    }

    public CurrentCipher() {
        this(Cryptobox.Cipher.NONE);
    }

    public boolean verify(int y, int x, Cryptobox.GlyphColor color) {
        return currentTarget.getGlyphMap()[x][y].equals(color);
    }

    public Cryptobox.Cipher changeCipher(Cryptobox.GlyphColor[][] currentCipher) {
        if(isValidCipher(currentCipher)) {
            return currentTarget;
        }
        for(Cryptobox.Cipher cipher: Cryptobox.Cipher.values()) {
            if(!cipher.equals(Cryptobox.Cipher.NONE) && isValidCipher(cipher.getGlyphMap())) {
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

    private boolean isValidCipher(Cryptobox.GlyphColor[][] currentCipher) {
        for(int ix = 0; ix < currentCipher.length; ix++){
            boolean endOfColumn = false;
            for(int iy = currentCipher[0].length - 1; iy >= 0; iy--){
                if(!currentCipher[ix][iy].equals(currentTarget.getGlyphMap()[ix][iy]) && !currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE)) {
                    return false;
                } else if(currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE)) {
                    endOfColumn = true;
                } else if(endOfColumn && !currentCipher[ix][iy].equals(Cryptobox.GlyphColor.NONE)) {
                    return false;
                }
            }
        }
        return true;
    }
}
