package mehdi.bad.addons.Mixins;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        if (SkyblockUtils.isInKuudra() && Configs.HideBlindness) {
            ci.cancel();
        }
    }

    @Inject(method = "updateFogColor", at = @At(value = "HEAD"), cancellable = true)
    public void updateFogColor(float partialTicks, CallbackInfo ci) {
        if (SkyblockUtils.isInKuudra() && Configs.HideBlindness) {
            ci.cancel();
        }
    }


}