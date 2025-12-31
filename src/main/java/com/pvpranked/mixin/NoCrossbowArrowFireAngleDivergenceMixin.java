package com.pvpranked.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.pvpranked.StraighterThanAnArrow;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossbowItem.class)
public class NoCrossbowArrowFireAngleDivergenceMixin {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FFLnet/minecraft/entity/LivingEntity;)V"), method = "use")
	private void setFireAngleDivergenceToZero(CrossbowItem instance,
											  World world,
											  LivingEntity shooter,
											  Hand hand,
											  ItemStack stack,
											  float speed,
											  float divergence,
											  LivingEntity target,
											  Operation<Void> original)
	{
		original.call(instance,
				world,
				shooter,
				hand,
				stack,
				speed,
				StraighterThanAnArrow.removeCrossbowArrowFireAngleRNG() ? 0f : divergence,
				target
		);
	}
}