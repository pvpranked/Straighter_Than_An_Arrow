package com.pvpranked.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.pvpranked.StraighterThanAnArrow;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BowItem.class)
public class NoBowArrowFireAngleDivergenceMixin {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;shootAll(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/entity/LivingEntity;)V"), method = "onStoppedUsing")
	private void setFireAngleDivergenceToZero(BowItem instance,
											  ServerWorld world,
											  LivingEntity shooter,
											  Hand hand,
											  ItemStack stack,
											  List<ItemStack> projectiles,
											  float speed,
											  float divergence,
											  boolean critical,
											  @Nullable LivingEntity target,
											  Operation<Void> original)
	{
		original.call(instance,
				world,
				shooter,
				hand,
				stack,
				projectiles,
				speed,
				StraighterThanAnArrow.removeBowArrowFireAngleRNG() ? 0f : divergence,
				critical,
				target
		);
	}
}