package ovh.corail.flying_things.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.TextureLocation;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal")
public class GuiOverlayEnergy extends Screen {
    private final Minecraft mc;
    private final int width, height, guiLeft, guiTop;
    private final int barWidth;

    public GuiOverlayEnergy(Minecraft mc) {
        super(new StringTextComponent("Flying Things Energy"));
        this.mc = mc;
        this.width = this.mc.mainWindow.getScaledWidth();
        this.height = this.mc.mainWindow.getScaledHeight();
        this.barWidth = ConfigFlyingThings.client.barLength.get();
        this.guiLeft = (this.width - this.barWidth) / 2;
        this.guiTop = this.height - ConfigFlyingThings.client.barHeightPos.get();
        drawScreen();
    }

    private void drawScreen() {
        final ClientPlayerEntity player = Minecraft.getInstance().player;
        if (!(Helper.isRidingFlyingThing(player))) {
            return;
        }
        EntityAbstractFlyingThing mount = ((EntityAbstractFlyingThing) player.getRidingEntity());
        assert mount != null;
        this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
        int filled = (int) ((double) mount.getEnergy() * (double) (this.barWidth) / (double) ConfigFlyingThings.General.getMaxEnergy()) + 1;
        blit(this.guiLeft, this.guiTop, 0, 84, this.barWidth, 5);
        if (filled > 0) {
            blit(this.guiLeft, this.guiTop, 0, 89, filled, 5);
        }

        this.mc.getTextureManager().bindTexture(TextureLocation.BARS);
        float speed = (float) mount.speed * 100f;
        float speedMax = (float) ConfigFlyingThings.General.getSpeedMax();
        if (mount.speed >= speedMax) {
            filled = this.barWidth + 1;
        } else {
            filled = (int) (speed * (float) this.barWidth / speedMax) + 1;
        }
        blit(this.guiLeft, this.guiTop + 7, 0, 50, this.barWidth, 5);
        if (filled > 0) {
            blit(this.guiLeft, this.guiTop + 7, 0, 55, filled, 5);
        }
        blit(this.guiLeft, this.guiTop + 7, 0, 115, this.barWidth, 5);
    }
}
