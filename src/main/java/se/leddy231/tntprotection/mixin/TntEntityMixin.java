package se.leddy231.tntprotection.mixin;

import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import se.leddy231.tntprotection.ProtectionStateManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntity.class)
public class TntEntityMixin {

	boolean isWilderness(BlockPos pos, World dimension) {
		return pos.getZ() <= -5;
	}

	boolean isNetherRoof(BlockPos pos, World dimension) {
		return dimension.getRegistryKey().equals(World.NETHER) && pos.getY() >= 120;
	}

	boolean isWhitelistedArea(BlockPos pos, World dimension) {
		return ProtectionStateManager.instance.isWhitelisted(pos, dimension);
	}

	@Inject(method = "explode()V", at = @At("HEAD"), cancellable = true)
    private void explode(CallbackInfo ci) {
		TntEntity e = (TntEntity) (Object) this;
		BlockPos pos = e.getBlockPos();
		World dimension = e.getEntityWorld();
		if (isWilderness(pos, dimension) || isNetherRoof(pos, dimension) || isWhitelistedArea(pos, dimension)) {
			return;
		}
		ci.cancel();
		e.world.createExplosion(e, e.getX(), e.getBodyY(0.0625), e.getZ(), 4.0f, Explosion.DestructionType.NONE);
    }
}
