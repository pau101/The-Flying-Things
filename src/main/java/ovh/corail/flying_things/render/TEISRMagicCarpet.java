package ovh.corail.flying_things.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import ovh.corail.flying_things.entity.EntityMagicCarpet;
import ovh.corail.flying_things.item.ItemMagicCarpet;
import ovh.corail.flying_things.registry.ModEntities;

public class TEISRMagicCarpet extends ItemStackTileEntityRenderer {
    private EntityMagicCarpet entity = ModEntities.magic_carpet.create(null);

    @Override
    public void renderByItem(ItemStack stack) {
        entity.setModelType(ItemMagicCarpet.getModelType(stack));
        RenderMagicCarpet.render(Minecraft.getInstance().getRenderManager(), entity, 0d, 0d, 0d, 0f, Minecraft.getInstance().player.ticksExisted, Minecraft.getInstance().getRenderPartialTicks(), false);
    }
}
