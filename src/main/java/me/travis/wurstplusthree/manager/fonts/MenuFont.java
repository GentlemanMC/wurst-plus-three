package me.travis.wurstplusthree.manager.fonts;

import me.travis.wurstplusthree.gui.Rainbow;
import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.util.Globals;

import java.awt.*;

public class MenuFont implements Globals {

    private CustomFont menuFont = new CustomFont(new Font("Tahoma", 1, 21), true, false);

    public void drawStringWithShadow(String string, float x, float y, int colour) {
        this.drawString(string, x, y, colour, true);
    }

    public float drawString(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.menuFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.menuFont.drawString(string, x, y, colour);
        }
    }

    public float drawStringRainbow(String string, float x, float y, boolean shadow) {
        if (shadow) {
            return this.menuFont.drawStringWithShadow(string, x, y, Rainbow.rgb);
        } else {
            return this.menuFont.drawString(string, x, y, Rainbow.rgb);
        }
    }

    public int getTextHeight() {
        return this.menuFont.getStringHeight();
    }

    public int getTextWidth(String string) {
        return this.menuFont.getStringWidth(string);
    }

}
