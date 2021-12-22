package se.leddy231.tntprotection.mixin;

import net.minecraft.entity.TntEntity;
import net.minecraft.world.explosion.Explosion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntity.class)
public class TntEntityMixin {
	@Inject(method = "explode()V", at = @At("HEAD"), cancellable = true)
    private void explode(CallbackInfo ci) {
		TntEntity e = (TntEntity) (Object) this;
		double z = e.getZ();
		if (z > -5) {
			ci.cancel();
        	e.world.createExplosion(e, e.getX(), e.getBodyY(0.0625), e.getZ(), 4.0f, Explosion.DestructionType.NONE);
		}
        
    }
}
