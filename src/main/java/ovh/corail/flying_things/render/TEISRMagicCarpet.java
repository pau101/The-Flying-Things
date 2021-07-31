package ovh.corail.flying_things.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ovh.corail.flying_things.entity.EntityMagicCarpet;
import ovh.corail.flying_things.item.ItemMagicCarpet;
import ovh.corail.flying_things.registry.ModEntities;

@OnlyIn(Dist.CLIENT)
public class TEISRMagicCarpet extends ItemStackTileEntityRenderer {
    private EntityMagicCarpet entity;
    
    @Override
    public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLight, int combinedOverlay) {
    	if(entity == null) {
    		entity = ModEntities.magic_carpet.get().create(null);
    	}
        entity.setModelType(ItemMagicCarpet.getModelType(stack));
        ClientPlayerEntity player = Minecraft.getInstance().player;
        RenderMagicCarpet.render(entity, 0f, player != null ? player.tickCount : 0, Minecraft.getInstance().getFrameTime(), matrixStack, iRenderTypeBuffer, combinedLight, true);
    }
}
