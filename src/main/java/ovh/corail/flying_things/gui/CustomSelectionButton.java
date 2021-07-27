package ovh.corail.flying_things.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.helper.Helper;

import java.util.function.IntSupplier;

import com.mojang.blaze3d.matrix.MatrixStack;

@OnlyIn(Dist.CLIENT)
public class CustomSelectionButton extends OptionSlider {
    private final IntSupplier intSupplier1, intSupplier2;

    public CustomSelectionButton(GameSettings settings, int x, int y, int width, int height, SliderPercentageOption sliderPercentageOption, IntSupplier intSupplier1, IntSupplier intSupplier2) {
        super(settings, x, y, width, height, sliderPercentageOption);
        this.intSupplier1 = intSupplier1;
        this.intSupplier2 = intSupplier2;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int x, int y) {
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        fill(matrixStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xff000000);
        Helper.fillGradient(this.x, this.y, this.x + this.width, this.y + this.height, this.intSupplier1.getAsInt() + 0xff000000, this.intSupplier2.getAsInt() + 0xff000000, getBlitOffset(), true);
        fillGradient(matrixStack, this.x + (int) (this.width * this.sliderValue) - 1, this.y, this.x + (int) (this.width * this.sliderValue) + 1, this.y + this.height, 0xffc0c0c0, 0xff000000);
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	renderBg(matrixStack, Minecraft.getInstance(), mouseX, mouseY);
    }
}
