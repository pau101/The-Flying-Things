package ovh.corail.flying_things.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class BooleanConfigOption extends AbstractOption {
    private final BooleanSupplier supplier;
    private final Consumer<Boolean> consumer;

    public BooleanConfigOption(String title, BooleanSupplier supplier, Consumer<Boolean> consumer) {
        super(title);
        this.supplier = supplier;
        this.consumer = consumer;
    }

    private void set(boolean enabled) {
        this.consumer.accept(enabled);
    }

    public boolean get() {
        return this.supplier.getAsBoolean();
    }

    @Override
    public Widget createWidget(GameSettings options, int x, int y, int width) {
        return new OptionButton(x, y, width, 14, this, getOptionName(), pressable -> {
            set(!this.supplier.getAsBoolean());
            pressable.setMessage(getOptionName());
        }) {
            @Override
            public void renderButton(int mouseX, int mouseY, float partialTicks) {
                Minecraft minecraft = Minecraft.getInstance();
                renderBg(minecraft, mouseX, mouseY);
                int j = isHovered() ? 0xff897235 : 0xffffffff;
                drawCenteredString(minecraft.fontRenderer, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255f) << 24);
            }
        };
    }

    private String getOptionName() {
        return getDisplayString() + I18n.format(get() ? "options.on" : "options.off");
    }
}
