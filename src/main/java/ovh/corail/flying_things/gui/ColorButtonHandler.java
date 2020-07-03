package ovh.corail.flying_things.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.SliderPercentageOption;

import java.util.List;

public class ColorButtonHandler {

    private int r, g, b;
    private CustomSelectionButton button1, button2, button3;

    ColorButtonHandler(GameSettings settings, List<Widget> list, List<IGuiEventListener> children, int x, int y, int width, int height, int r, int g, int b, String title) {
        this.r = r;
        this.g = g;
        this.b = b;

        this.button1 = new CustomSelectionButton(settings, x, y, width, height,
                new SliderPercentageOption(title + "_R", 0d, 255d, 1f,
                        s -> (double) this.r, (s, d) -> this.r = d.intValue(), (s, d) -> title + "_R"), this::getMinColorR, this::getMaxColorR
        );
        list.add(button1);
        children.add(button1);
        this.button2 = new CustomSelectionButton(settings, x, y + 6, width, height,
                new SliderPercentageOption(title + "_G", 0d, 255d, 1f,
                        s -> (double) this.g, (s, d) -> this.g = d.intValue(), (s, d) -> title + "_G"), this::getMinColorG, this::getMaxColorG
        );
        list.add(button2);
        children.add(button2);
        this.button3 = new CustomSelectionButton(settings, x, y + 12, width, height,
                new SliderPercentageOption(title + "_B", 0d, 255d, 1f,
                        s -> (double) this.b, (s, d) -> this.b = d.intValue(), (s, d) -> title + "_B"), this::getMinColorB, this::getMaxColorB
        );
        list.add(button3);
        children.add(button3);
    }

    private int getMinColorR() {
        return this.g * 0x0100 + this.b;
    }

    private int getMaxColorR() {
        return getMinColorR() + 0xff0000;
    }

    private int getMinColorG() {
        return this.r * 0x010000 + this.b;
    }

    private int getMaxColorG() {
        return getMinColorG() + 0xff00;
    }

    private int getMinColorB() {
        return this.r * 0x010000 + this.g * 0x0100;
    }

    private int getMaxColorB() {
        return getMinColorB() + 0xff;
    }

    public int getColor() {
        return 0x010000 * this.r + 0x0100 * this.g + this.b;
    }
}
