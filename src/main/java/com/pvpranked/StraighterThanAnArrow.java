package com.pvpranked;

import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;

public class StraighterThanAnArrow implements ModInitializer {
	public static final String MOD_ID = "straighter-than-an-arrow";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final double FALLBACK_ARROW_PUSH_OUT_OF_BLOCKS_MULTIPLIER = Math.pow(3, 1.0/3.0);

	public static boolean removeBowArrowFireAngleRNG() {
		return true;
	}
	public static boolean removeCrossbowArrowFireAngleRNG() {
		return true;
	}
	public static boolean fixArrowImpactPosition() {
		return true;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Straighter Than An Arrow: Helping archers overcome sudden onset siezures since 1994!");
	}
	public static Vec3d getCollisionOffset(PersistentProjectileEntity entity, BlockHitResult blockHitResult) {
//			nope, cannot do this anymore. BlockHitResult.getPos() is not useful because unlike 1.20.1, in this version the arrow has already stopped exactly at blockHitResult.getPos().
//			In short, in this version blockHitResult.getPos().subtract(this.getEntityPos()) is useless because right now blockHitResult.getPos() EQUALS this.getEntityPos()
//			return blockHitResult.getPos().subtract(((PersistentProjectileEntity) (Object) this).getEntityPos()).normalize().multiply(StraighterThanAnArrowFireAngleRNGRemover.ARROW_PUSH_OUT_OF_BLOCKS_MULTIPLIER);
		//velocity is good enough and the default for this version anyways

		return entity.getVelocity().normalize();
	}

	/// yeah so it turns out this is completely unnecessary and I'm back to not understanding at all why mojang put in signum in the first place. Simple normalization works great in all circumstances
	@Deprecated
	private static Vec3d brokenAttemptAtReplicatingPerfectPoint05DistFromSurface(PersistentProjectileEntity entity, BlockHitResult blockHitResult) {
		//the reason for the normal signum code appears to be so that you always push the arrow exactly 0.05 from the surface. We can still do this...

		Vec3d velocity = entity.getVelocity();

		Vec3d surfaceNormalDirection = getAndVerifySurfaceNormal(blockHitResult);

		Vec3d velocityProjectedAlongSurfaceNormal = surfaceNormalDirection.multiply(velocity);

		if(velocityProjectedAlongSurfaceNormal.x == 0
				&& velocityProjectedAlongSurfaceNormal.y == 0
				&& velocityProjectedAlongSurfaceNormal.z == 0) {
			return velocity.normalize().multiply(FALLBACK_ARROW_PUSH_OUT_OF_BLOCKS_MULTIPLIER);
		}

		double surfaceNormalProjectionLength = velocityProjectedAlongSurfaceNormal.length();

		if(surfaceNormalProjectionLength == 0) {
			//the above check should have caught this beforehand but check just in case
			return velocity.normalize().multiply(FALLBACK_ARROW_PUSH_OUT_OF_BLOCKS_MULTIPLIER);
		}

		Vec3d velocityScaledSoItIsDistanceOneInSurfaceNormalDirection = velocity.multiply(1 / surfaceNormalProjectionLength);

		LOGGER.info("Producing push of {} from vel {}, surface normal {}", velocityScaledSoItIsDistanceOneInSurfaceNormalDirection, velocity, surfaceNormalDirection);

		return velocityScaledSoItIsDistanceOneInSurfaceNormalDirection;
	}

	@Unique
	@NotNull
	private static Vec3d getAndVerifySurfaceNormal(BlockHitResult blockHitResult) {
		Vec3d surfaceNormalDirection = blockHitResult.getSide().getDoubleVector();

		if((surfaceNormalDirection.x != 0 && surfaceNormalDirection.y != 0)
				|| (surfaceNormalDirection.y != 0 && surfaceNormalDirection.z != 0)
				|| (surfaceNormalDirection.x != 0 && surfaceNormalDirection.z != 0)
				|| surfaceNormalDirection.lengthSquared() == 0) {
			//if more than one of them is non-zero, should never happen
			throw new IllegalStateException("ERROR FROM MOD " + MOD_ID + " Unexpected surface normal direction " + surfaceNormalDirection + " for arrow collision map will break fixes applied because it has a non zero component in more than one direction. This is because we just multiply the components of the direction by other vectors to get the projection, which only works if the vector is a unit vector along one of the direction axes! \n\nThis is a mod compatibility issue. You must contact pvpranked about this issue, which you can do by joining this discord server: https://discord.gg/gvXYZvBcke and opening a ticket. Feel free to spam ping @pvpranked about this issue, and to screenshot this text to prove its okay if the mods get mad at you for spam pinging.");
		}
		return surfaceNormalDirection;
	}
}