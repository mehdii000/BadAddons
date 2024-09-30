package mehdi.bad.addons.Mixins;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Features.General.ReplacedWordsConfig;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(FontRenderer.class)
public class FontRendererMixin {

    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
    private String replaceWords(String string) {
        if (Configs.WordReplacing && (!string.contains("[BadAddons]")) && (!string.contains("/bb replaceword")) ) {
            for (Map.Entry<String, String> entry : ReplacedWordsConfig.wordReplacements.entrySet()) {
                String originalWord = entry.getKey();
                String newWord = entry.getValue();
                string = string.replace(originalWord, newWord);
            }
        }
        return string;
    }

}