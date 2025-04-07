/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package github.kawaiior.juggernaut.mixin;

import net.minecraft.client.renderer.RenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderState.class)
public interface AccessorRenderState {

	@Accessor("TRANSLUCENT_TRANSPARENCY")
	static RenderState.TransparencyState getTranslucentTransparency() {
		throw new IllegalStateException();
	}

	@Accessor("ITEM_ENTITY_TARGET")
	static RenderState.TargetState getItemEntityTarget() {
		throw new IllegalStateException();
	}
}
