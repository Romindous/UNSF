package TrueAvarus.UNSF.WeaponAI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MIRV_proj_GPT2 implements MissileAIPlugin, GuidedMissileAI {

    private final MissileAPI missile;
    private final ShipAPI launchingShip;
    private CombatEntityAPI target;

    private float timeSinceLaunch = 0f; // Time since the missile was launched
    private static final float ACTIVATION_DELAY = 1.5f; // Delay time in seconds before activating
    private static final int TARGET_UPDATE_TICKS = 10; // Number of ticks before updating the target

    private boolean activated = false;
    private int tickCounter = 0; // Counter to track ticks
    private Random random = new Random(); // Random number generator

    public MIRV_proj_GPT2(MissileAPI missile, ShipAPI launchingShip) {
        this.missile = missile;
        this.launchingShip = launchingShip;
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
        if (missile.isFizzling() || missile.isFading()) return;

        timeSinceLaunch += amount; // Increment the time since launch

        if (timeSinceLaunch >= ACTIVATION_DELAY) {
            if (!activated) {
                // Activate the missile after the delay
                activated = true;
                tickCounter = 0; // Initialize tick counter
                target = findClosestEnemy(); // Find and set the initial target
            }

            if (activated) {
                tickCounter++; // Increment tick counter

                // Update target every TARGET_UPDATE_TICKS
                if (tickCounter >= TARGET_UPDATE_TICKS) {
                    target = findClosestEnemy(); // Update target
                    tickCounter = 0; // Reset tick counter
                }

                if (target != null) {
                    // Move towards the current target
                    move(missile, target.getLocation(), amount);
                }
            }
        }
    }

    private CombatEntityAPI findClosestEnemy() {
        CombatEngineAPI engine = Global.getCombatEngine();
        List<CombatEntityAPI> enemies = new ArrayList<>();
        Vector2f missileLocation = missile.getLocation();
        CombatEntityAPI closestEnemy = null;
        float closestSquaredDistance = Float.MAX_VALUE;

        // Get all entities in play
        for (CombatEntityAPI entity : engine.getShips()) {
            if (entity instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) entity;

                // Check if the ship is alive, not the launching ship, and is not an ally
                if (ship.isAlive() && (ship != launchingShip) && !isAlly(ship)) {
                    enemies.add(ship);
                }
            }
        }

        // If there are no enemies, return null
        if (enemies.isEmpty()) {
            return null;
        }

        // Find the closest enemy
        for (CombatEntityAPI enemy : enemies) {
            Vector2f enemyLocation = enemy.getLocation();
            float dx = enemyLocation.x - missileLocation.x;
            float dy = enemyLocation.y - missileLocation.y;
            float squaredDistance = dx * dx + dy * dy;

            if (squaredDistance < closestSquaredDistance) {
                closestSquaredDistance = squaredDistance;
                closestEnemy = enemy;
            }
        }

        return closestEnemy;
    }

    private boolean isAlly(ShipAPI ship) {
        // Using the isAlly method provided by ShipAPI to check if the ship is an ally
        // Assumes that launchingShip is of the type ShipAPI
        return ship.isAlly();
    }

    private void move(MissileAPI missile, Vector2f target, float amount) {
        Vector2f displacement = Vector2f.sub(target, missile.getLocation(), new Vector2f());
        float absoluteAngle = VectorUtils.getFacing(displacement);
        float relativeAngle = absoluteAngle - missile.getFacing();
        if (relativeAngle > 180f) relativeAngle -= 360f;

        float turnSpeed = Math.signum(relativeAngle) * missile.getMaxTurnRate();
        turnSpeed *= amount;
        turnSpeed = Math.min(Math.abs(relativeAngle), turnSpeed);

        missile.setFacing(missile.getFacing() + turnSpeed);
        missile.giveCommand(ShipCommand.ACCELERATE);
    }
}