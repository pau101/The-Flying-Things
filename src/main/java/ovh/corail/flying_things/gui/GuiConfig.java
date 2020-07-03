package ovh.corail.flying_things.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import ovh.corail.flying_things.config.ConfigFlyingThings;
import ovh.corail.flying_things.config.ConfigFlyingThings.Client.Graduation;
import ovh.corail.flying_things.helper.Helper;

public class GuiConfig extends Screen {
    private int xSize = 200, ySize = 200, halfWidth, guiLeft, guiTop, guiRight;
    private final int bgColor = 0x80263a57;
    private final int textColor = 0xffffffff;
    private ColorButtonHandler colorHandler1, colorHandler2;


    public GuiConfig(Minecraft mc) {
        super(new StringTextComponent("The Flying Things Config"));
        this.minecraft = mc;
    }

    public GuiConfig(Minecraft minecraft, Screen parentScreen) {
        this(minecraft);
    }

    @Override
    public void init() {
        this.halfWidth = this.width / 2;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.guiRight = this.guiLeft + this.xSize;
        this.buttons.clear();
        int sliderX = this.guiRight - 60;
        addButton(new CustomSlider(getMinecraft().gameSettings, sliderX, this.guiTop + 57, 50, 20, new SliderPercentageOption("bar_pos_y", 0d, 100d, 1f, settings -> (double) ConfigFlyingThings.client.barHeightPos.get(), (settings, d) -> ConfigFlyingThings.client.barHeightPos.set(d.intValue()), (settings, slider) -> MathHelper.floor(slider.get(settings)) + "%")));
        addButton(new BooleanConfigOption("Render Effect", ConfigFlyingThings.client.renderEffect::get, ConfigFlyingThings.client.renderEffect::set).createWidget(getMinecraft().gameSettings, this.guiLeft + 3, this.guiTop + 80, this.xSize));
        addButton(new BooleanConfigOption("Display Bar Values", ConfigFlyingThings.client.barValue::get, ConfigFlyingThings.client.barValue::set).createWidget(getMinecraft().gameSettings, this.guiLeft + 3, this.guiTop + 100, this.xSize));
        int[] colors = Helper.getRGBColor3I(ConfigFlyingThings.client.barColorEnergy.get());
        this.colorHandler1 = new ColorButtonHandler(getMinecraft().gameSettings, this.buttons, this.children, this.halfWidth + 10, this.guiTop + 120, 80, 4, colors[0], colors[1], colors[2], "energy_bar_color");
        colors = Helper.getRGBColor3I(ConfigFlyingThings.client.barColorSpeed.get());
        this.colorHandler2 = new ColorButtonHandler(getMinecraft().gameSettings, this.buttons, this.children, this.halfWidth + 10, this.guiTop + 140, 80, 4, colors[0], colors[1], colors[2], "speed_bar_color");
        addButton(new IntegerConfigOption("Energy Bar Graduation", () -> ConfigFlyingThings.client.barGraduationEnergy.get().ordinal(), i -> ConfigFlyingThings.client.barGraduationEnergy.set(Graduation.values()[i]), Graduation.values().length - 1, i -> "Energy Bar Graduation : " + Graduation.values()[i].name()).createWidget(getMinecraft().gameSettings, this.guiLeft + 3, this.guiTop + 160, 190));
        addButton(new IntegerConfigOption("Speed Bar Graduation", () -> ConfigFlyingThings.client.barGraduationSpeed.get().ordinal(), i -> ConfigFlyingThings.client.barGraduationSpeed.set(Graduation.values()[i]), Graduation.values().length - 1, i -> "Speed Bar Graduation : " + Graduation.values()[i].name()).createWidget(getMinecraft().gameSettings, this.guiLeft + 3, this.guiTop + 180, 190));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void onClose() {
        int color1 = this.colorHandler1.getColor();
        if (color1 != ConfigFlyingThings.client.barColorEnergy.get()) {
            ConfigFlyingThings.client.barColorEnergy.set(color1);
        }
        int color2 = this.colorHandler2.getColor();
        if (color2 != ConfigFlyingThings.client.barColorSpeed.get()) {
            ConfigFlyingThings.client.barColorSpeed.set(color2);
        }
        super.onClose();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        fill(this.guiLeft + 5, this.guiTop + 5, this.guiRight - 5, this.guiTop + 20 + font.FONT_HEIGHT, this.bgColor);
        drawStringAt(title.getFormattedText(), 14, this.textColor, true);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        drawStringAt("Bar Pos Y (in %)", 65);
        drawStringAt("Speed Bar Color", 125, this.colorHandler1.getColor(), false);
        drawStringAt("Energy Bar Color", 145, this.colorHandler2.getColor(), false);
        super.render(mouseX, mouseY, partialTicks);
    }

    private void drawStringAt(String text, int y) {
        drawStringAt(text, y, this.textColor, false);
    }

    private void drawStringAt(String text, int y, int textColor, boolean centered) {
        drawString(this.font, text, centered ? this.halfWidth - this.font.getStringWidth(text) / 2 : this.guiLeft + 10, this.guiTop + y, textColor);
    }


        @Override
    public void renderBackground() {
        super.renderBackground();
        fill(0, 0, this.width, this.height, 0x80000000);
        fill(this.guiLeft - 3, this.guiTop - 3, this.guiLeft + this.xSize + 3, this.guiTop + this.ySize + 3, this.bgColor);
    }
}
