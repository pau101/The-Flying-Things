package ovh.corail.flying_things.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.Helper;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal")
public class GuiOverlaySpeed extends Screen {
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private final int width, height, guiLeft, guiTop;
    private final int guiWidth = 128;
    private final int guiHeight = 128;

    public GuiOverlaySpeed(Minecraft mc) {
        super(new StringTextComponent("Flying Things Speed"));
        this.mc = mc;
        this.fontRenderer = this.mc.fontRenderer;
        this.width = this.mc.mainWindow.getScaledWidth();
        this.height = this.mc.mainWindow.getScaledHeight();
        this.guiLeft = this.width - this.guiWidth + 10;
        this.guiTop = this.height - this.guiHeight + 60;
        drawScreen();
    }

    private void drawScreen() {
        if (Helper.isRidingFlyingThing(mc.player)) {
            EntityAbstractFlyingThing mount = ((EntityAbstractFlyingThing) mc.player.getRidingEntity());
            assert mount != null;
            fill(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + this.guiHeight, 0x000000);
            drawString(this.fontRenderer, "Speed : " + String.format("%,.2f", mount.speed * 100d) + " / " + ConfigFlyingThings.General.getSpeedMax(), this.guiLeft + 10, this.guiTop + 10, 0xffffff);
            drawString(this.fontRenderer, "Energy : " + mount.getEnergy() + " / " + ConfigFlyingThings.General.getMaxEnergy(), this.guiLeft + 10, this.guiTop + 20, 0xffffff);
        }
    }
}
