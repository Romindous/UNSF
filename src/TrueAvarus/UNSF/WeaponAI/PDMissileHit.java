package TrueAvarus.UNSF.WeaponAI;

import java.awt.*;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

public class PDMissileHit implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
        Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (!(projectile instanceof final MissileAPI ms)) return;
        engine.spawnDamagingExplosion(getExpSpec(ms),
            projectile.getSource(), projectile.getLocation());
        PDMissileAI.counts.clear();
    }

    private static DamagingExplosionSpec getExpSpec(MissileAPI ms) {
        final DamagingExplosionSpec des = ms.getSpec().getExplosionSpec();
        des.setCoreRadius(ms.getCollisionRadius());
        des.setRadius(des.getCoreRadius() + ms.getSpec().getExplosionRadius());
        des.setSoundSetId("explosion_flak");
        des.setShowGraphic(true);
        return des;
    }
}