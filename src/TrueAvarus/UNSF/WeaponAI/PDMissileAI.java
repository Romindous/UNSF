package TrueAvarus.UNSF.WeaponAI;

import TrueAvarus.UNSF.Objects.IntHashMap;
import TrueAvarus.UNSF.Utils.NumUtil;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class PDMissileAI implements MissileAIPlugin, GuidedMissileAI {

    public static final IntHashMap<Integer> counts = new IntHashMap<>();

    private static CombatEngineAPI engine;

    private final MissileAPI missile;
    private final int maxOnMissle;
    private final int maxOnWing;
    private final float maxDstSq;
    private CombatEntityAPI target;
    private Vector2f tgtLoc;

    private float timeSinceLaunch = 0f; // Time since the missile was launched
    private int tickCounter = 0; // Counter to track ticks

    public PDMissileAI(MissileAPI missile, ShipAPI ship) {
        this.missile = missile;
        final float acc = missile.getAcceleration();
        final float speed = missile.getMaxSpeed();
        final float time = speed / acc;
        final float maxDst = 0.5f * acc * time * time
            + (speed * (missile.getMaxFlightTime() - time));
        this.maxDstSq = maxDst * maxDst;
        this.maxOnMissle = missile.getWeaponSpec().getBurstSize() >> 2;
        this.maxOnWing = missile.getWeaponSpec().getBurstSize() >> 1;
        this.target = null;
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }

    @Override
    public void advance(float amount) {
        if (engine == null) engine = Global.getCombatEngine();
        if (engine == null || missile.isFizzling() || missile.isFading()) return;

        timeSinceLaunch += amount; // Increment the time since launch
        tickCounter++; // Increment tick counter

        if (target == null) {
            target = findTarget();
            return;
        }

        if (target instanceof final MissileAPI ms && (ms.isFizzling() || ms.isFading())) {
            target = null; tgtLoc = null; return;
        } else if (target instanceof final ShipAPI shp && (!shp.isAlive() || shp.isPhased())) {
            target = null; tgtLoc = null; return;
        }

        // Move towards the current target
        move(missile, amount);
    }

    /*public void explode() {
        if (engine == null || target == null) return;
        engine.removeObject(missile);
        engine.spawnDamagingExplosion(missile.getSpec().getExplosionSpec(),
            missile.getSource(), missile.getLocation());
    }*/

    /*public void explode() {
        if (engine == null || target == null) return;
        engine.removeObject(missile);
        missile.getSpec().getOnHitEffect().onHit(missile, target,
            missile.getLocation(), false, null, engine);
    }*/

    private CombatEntityAPI findTarget() {
        final int ally = missile.getOwner();
        float lastDstSq = Float.MAX_VALUE;
        CombatEntityAPI target = null;
        CombatEntityAPI taken = null;
        for(final MissileAPI ms : engine.getMissiles()) {
            if (ms.getOwner() == ally || ms.isFading() || ms.isFizzling()
                || ms.isFlare() || ms.isDecoyFlare()) continue;
            float dstSq = MathUtils.getDistanceSquared(missile.getLocation(), ms.getLocation());
            if (dstSq > lastDstSq || dstSq > maxDstSq) continue;
            if (hasOnTarget(ms)) {
                taken = ms;
                continue;
            }
            lastDstSq = dstSq;
            target = ms;
        }
        if (target != null) return target;

        for (final ShipAPI shp : engine.getShips()) {
            if (shp.getOwner() == ally || !shp.isAlive() || shp.isPhased()) continue;
            float dstSq = MathUtils.getDistanceSquared(missile.getLocation(), shp.getLocation());
            if (dstSq > lastDstSq || dstSq > maxDstSq) continue;
            if (shp.isFighter() && hasOnTarget(shp)) {
                taken = shp;
                continue;
            }
            lastDstSq = dstSq;
            target = shp;
        }

        if (target != null) return target;
        return taken;
    }

    private boolean hasOnTarget(ShipAPI shp) {
        final int id = shp.hashCode();
        final Integer cnt = counts.get(id);
        if (cnt == null) counts.put(id, 1);
        else {
            if (cnt > maxOnWing) return true;
            counts.put(id, cnt + 1);
        }
        return false;
    }

    private boolean hasOnTarget(MissileAPI ms) {
        final int id = NumUtil.vecode(ms.getStart());
        final Integer cnt = counts.get(id);
        if (cnt == null) counts.put(id, 1);
        else {
            if (cnt > maxOnMissle) return true;
            counts.put(id, cnt + 1);
        }
        return false;
    }

    private static final int ACC_ANGLE = 60;
    private static final int OFFSET = 4;
    private void move(MissileAPI missile, float amount) {
        if (tgtLoc == null) tgtLoc = AIUtils.getBestInterceptPoint(
            missile.getLocation(), missile.getMoveSpeed(),
            target.getLocation(), target.getVelocity());
        if (tgtLoc == null) return;
        final Vector2f dst = Vector2f.sub(tgtLoc, missile.getLocation(), new Vector2f());
        float absoluteAngle = VectorUtils.getFacing(dst);
        int relativeAngle = (int) (absoluteAngle - missile.getFacing()) + NumUtil.rndSignNum(OFFSET, OFFSET);
        relativeAngle = (relativeAngle - relativeAngle / 180 * 360) % 360;
        final float turnSpeed = Math.min(relativeAngle,
            Integer.signum(relativeAngle) * missile.getMaxTurnRate() * amount);

        missile.setFacing(missile.getFacing() + turnSpeed);
        if (relativeAngle < ACC_ANGLE) missile.giveCommand(ShipCommand.ACCELERATE);
    }
}