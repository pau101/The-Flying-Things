package ovh.corail.flying_things.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.entity.EntityAbstractFlyingThing;
import ovh.corail.flying_things.helper.Helper;
import ovh.corail.flying_things.helper.TextureLocation;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal")
public class GuiOverlayEnergy extends Screen {
    public final int width, height, guiLeft, barLength;

    public GuiOverlayEnergy(Minecraft mc) {
        super(new StringTextComponent("Flying Things Energy"));
        this.minecraft = mc;
        this.width = mc.getMainWindow().getScaledWidth();
        this.height = mc.getMainWindow().getScaledHeight();
        this.barLength = 182;
        this.guiLeft = (this.width - this.barLength) / 2;
        drawScreen();
    }

    private void drawScreen() {
        final ClientPlayerEntity player = Minecraft.getInstance().player;
        if (Helper.isRidingFlyingThing(player)) {
            EntityAbstractFlyingThing mount = ((EntityAbstractFlyingThing) player.getRidingEntity());
            assert mount != null;
            drawBars(this, this.guiLeft, this.height * ConfigFlyingThings.client.barHeightPos.get() / 100, this.barLength, mount.getEnergy(), mount.speed);
        }
    }

    static void drawBars(Screen screen, int guiLeft, int guiTop, int barWidth, int energy, double mountSpeed) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableBlend();
        mc.getTextureManager().bindTexture(TextureLocation.BARS);
        int colorPosY = 60;
        int filled = (int) (energy * barWidth / (double) ConfigFlyingThings.shared_datas.maxEnergy.get()) + 1;
        float[] colors = Helper.getRGBColor3F(ConfigFlyingThings.client.barColorEnergy.get());
        RenderSystem.color4f(colors[0], colors[1], colors[2], 0.5f);
        screen.blit(guiLeft, guiTop, 0, colorPosY, barWidth, 5);
        if (filled > 0) {
            RenderSystem.color4f(colors[0], colors[1], colors[2], 1f);
            screen.blit(guiLeft, guiTop, 0, colorPosY + 5, filled, 5);
        }
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        screen.blit(guiLeft, guiTop, 0, ConfigFlyingThings.client.barGraduationEnergy.get().ordinal() * 10 + 80, barWidth, 5);

        float speed = (float) mountSpeed * 100f;
        float speedMax = (float) ConfigFlyingThings.shared_datas.speedMax.get();
        if (mountSpeed >= speedMax) {
            filled = barWidth + 1;
        } else {
            filled = (int) (speed * barWidth / speedMax) + 1;
        }
        colors = Helper.getRGBColor3F(ConfigFlyingThings.client.barColorSpeed.get());
        RenderSystem.color4f(colors[0], colors[1], colors[2], 0.5f);
        screen.blit(guiLeft, guiTop + 7, 0, colorPosY, barWidth, 5);
        if (filled > 0) {
            RenderSystem.color4f(colors[0], colors[1], colors[2], 1f);
            screen.blit(guiLeft, guiTop + 7, 0, colorPosY + 5, filled, 5);
        }
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        screen.blit(guiLeft, guiTop + 7, 0, ConfigFlyingThings.client.barGraduationSpeed.get().ordinal() * 10 + 80, barWidth, 5);
        if (ConfigFlyingThings.client.barValue.get()) {
            fill(guiLeft, guiTop + 14, guiLeft + 130, guiTop + 40, 0x20000000);
            screen.drawString(mc.fontRenderer, "Speed : " + MathHelper.floor(Math.min(mountSpeed * 100d, ConfigFlyingThings.shared_datas.speedMax.get())) + " / " + ConfigFlyingThings.shared_datas.speedMax.get(), guiLeft + 10, guiTop + 18, 0xa0ffffff);
            screen.drawString(mc.fontRenderer, "Energy : " + energy + " / " + ConfigFlyingThings.shared_datas.maxEnergy.get(), guiLeft + 10, guiTop + 28, 0xa0ffffff);
        }
        RenderSystem.defaultBlendFunc();
    }
}
