package TrueAvarus.UNSF.WeaponAI;

//By Tartiflette, fast and highly customizable Missile AI.

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicTargeting;

public class HorizonMissleAI implements MissileAIPlugin, GuidedMissileAI {

    //////////////////////
    //     SETTINGS     //
    //////////////////////

    //delay in seconds before missle activates
    private static final float ACTIVATION_DELAY = 1f;

    //Damping of the turn speed when closing on the desired aim. The smaller the snappier.
    private static final float DAMPING = 0.1f;

    //Does the missile find a random target or aways tries to hit the ship's one?
    /*
     *  NO_RANDOM,
     * If the launching ship has a valid target within arc, the missile will pursue it.
     * If there is no target, it will check for an unselected cursor target within arc.
     * If there is none, it will pursue its closest valid threat within arc.
     *
     *  LOCAL_RANDOM,
     * If the ship has a target, the missile will pick a random valid threat around that one.
     * If the ship has none, the missile will pursue a random valid threat around the cursor, or itself.
     * Can produce strange behavior if used with a limited search cone.
     *
     *  FULL_RANDOM,
     * The missile will always seek a random valid threat within arc around itself.
     *
     *  IGNORE_SOURCE,
     * The missile will pick the closest target of interest. Useful for custom MIRVs.
     *
     */

    //Target class priorities
    //set to 0 to ignore that class
    private static final int fighters = 0;
    private static final int frigates = 1;
    private static final int destroyers = 2;
    private static final int cruisers = 4;
    private static final int capitals = 5;

    //Arc to look for targets into
    //set to 360 or more to ignore
    private static final int SEARCH_CONE = 360;


    //should the missile fall back to the closest enemy when no target is found within the search parameters
    //only used with limited search cones
    private final boolean FAILSAFE = true;


    //////////////////////
    //    VARIABLES     //
    //////////////////////

    //range in which the missile seek a target in game units.
    private final int MAX_SEARCH_RANGE;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private Vector2f lead = new Vector2f();
    private int ticks = 0;
    private float activationTimer = 0f; // Timer to track activation delay

    //////////////////////
    //  DATA COLLECTING //
    //////////////////////

    public HorizonMissleAI(MissileAPI missile) {
        this.missile = missile;
        MAX_SEARCH_RANGE = (int) missile.getWeaponSpec().getMaxRange();
    }

    //////////////////////
    //   MAIN AI LOOP   //
    //////////////////////

    @Override
    public void advance(float amount) {

        //Random starting offset for the waving.
        final CombatEngineAPI engine = Global.getCombatEngine();
        // Skip the AI if the game is paused, the missile is engineless, or fading
        if (engine.isPaused() || missile.isFading() || missile.isFizzling()) return;

        // Increment the activation timer
        activationTimer += amount;

        // Only proceed with targeting logic if activationTimer exceeds the delay
        if (!(activationTimer > ACTIVATION_DELAY)) return;

        // Assign a target if there is none or it got destroyed
        if (target == null || ((target instanceof ShipAPI && !((ShipAPI) target).isAlive()) || !engine.isEntityInPlay(target))) {
            setTarget(MagicTargeting.pickTarget(missile, MagicTargeting.targetSeeking.NO_RANDOM,
                MAX_SEARCH_RANGE, SEARCH_CONE, fighters, frigates, destroyers, cruisers, capitals, true));
            // Forced acceleration by default
            missile.giveCommand(ShipCommand.ACCELERATE);
            return;
        }

        ticks++;
        lead = locateTgt();

        // Best velocity vector angle for interception
        float correctAngle = VectorUtils.getAngle(missile.getLocation(), lead);

        // Velocity angle correction
        float offCourseAngle = MathUtils.getShortestRotation(
            VectorUtils.getFacing(missile.getVelocity()),
            correctAngle
        );

        float correction = MathUtils.getShortestRotation(correctAngle, missile.getFacing() + 180)
            * 0.5f * (float) ((FastTrig.sin(MathUtils.FPI / 90 * (Math.min(Math.abs(offCourseAngle), 45)))));

        correctAngle = correctAngle + correction;

        // Target angle for interception
        float aimAngle = MathUtils.getShortestRotation(missile.getFacing(), correctAngle);

        missile.giveCommand(aimAngle < 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT);
        missile.giveCommand(ShipCommand.ACCELERATE);

        // Damp angular velocity if the missile aim is getting close to the targeted angle
        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * DAMPING) {
            missile.setAngularVelocity(aimAngle / DAMPING);
        }
    }

    private static final int DST_DEL = 10000;
    private Vector2f locateTgt() {
        if (target == null) return null;
        if (lead == null) {
            return AIUtils.getBestInterceptPoint(
                missile.getLocation(), missile.getMoveSpeed(),
                target.getLocation(), target.getVelocity());
        }
        final int del = (int) MathUtils.getDistanceSquared(missile.getLocation(), lead) / DST_DEL;
        final int i = (1 << del) - 1;
        if ((ticks & i) == 0) {
            return AIUtils.getBestInterceptPoint(
                missile.getLocation(), missile.getMoveSpeed(),
                target.getLocation(), target.getVelocity());
        }
        return lead;
    }

    //////////////////////
    //    TARGETING     //
    //////////////////////

    @Override
    public CombatEntityAPI getTarget() { return target; }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }

}
