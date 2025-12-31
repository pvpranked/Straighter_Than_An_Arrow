package com.pvpranked.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.pvpranked.StraighterThanAnArrow;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public class ArrowCollisionMathFixMixin {
	@WrapOperation(at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"), method = "onBlockHit")
	private Vec3d revertSignumNonsenseToOlderVersion(double x, double y, double z, Operation<Vec3d> original, @Local(argsOnly = true)BlockHitResult blockHitResult)
	{
		if(StraighterThanAnArrow.fixArrowImpactPosition()) {
			return StraighterThanAnArrow.getCollisionOffset(((PersistentProjectileEntity)(Object)this), blockHitResult);
		} else {
			return original.call(x, y, z);
		}
	}
}