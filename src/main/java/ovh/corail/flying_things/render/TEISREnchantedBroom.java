package ovh.corail.flying_things.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import ovh.corail.flying_things.entity.EntityEnchantedBroom;
import ovh.corail.flying_things.item.ItemEnchantedBroom;
import ovh.corail.flying_things.registry.ModEntities;

public class TEISREnchantedBroom extends ItemStackTileEntityRenderer {
    private EntityEnchantedBroom entity = ModEntities.enchanted_broom.create(null);
    private RenderEnchantedBroom renderer = null;

    @Override
    @SuppressWarnings("unchecked")
    public void renderByItem(ItemStack stack) {
        this.entity.setModelType(ItemEnchantedBroom.getModelType(stack));
        this.entity.setHeadType(ItemEnchantedBroom.getHeadType(stack));
        if (this.renderer == null) {
            this.renderer = new RenderEnchantedBroom(Minecraft.getInstance().getRenderManager());
        }
        this.renderer.renderBroom(entity, 0d, 0d, 0d, 0f, Minecraft.getInstance().getRenderPartialTicks(), true);
    }
}
