package TrueAvarus.UNSF.FistingManifesto;

import java.awt.*;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Pings;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialMissionIntel;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class HyperspaceWindowGenerator extends BaseDurationAbility {
	private static final float PAD = 10f;
	private Vector2f lockedDirection = null; // Variable to store initial direction

	public static float FUEL_USE_MULT = 0.5f;
	protected boolean canUseToJumpToHyper() {
		return true;
	}
	protected boolean canUseToJumpToSystem() {
		return true;
	}
	protected Boolean primed = null;
	protected EveryFrameScript ping = null;

	@Override
	protected void activateImpl() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;

		// Ensure we're not in a hyperspace transition
		if (fleet.isInHyperspaceTransition()) return;

		// Check if fleet is in hyperspace and can jump to a system
		if (fleet.isInHyperspace() && canUseToJumpToSystem()) {
			// Start priming the jump
			ping = Global.getSector().addPing(fleet, Pings.TRANSVERSE_JUMP);
			primed = true;

			// Play sound when ability is primed
			Global.getSoundPlayer().playSound("unsf_hyperspace_window", 1f, 2f, fleet.getLocation(), fleet.getVelocity());

			// Define the color for the particle effect with lower brightness and less white
			Color particleColor = new Color(150, 210, 180, 20); // Light green color, semi-transparent with lower alpha

			// Get the campaign location (useful for placing the particle on the campaign map)
			LocationAPI location = Global.getSector().getCurrentLocation();

			// Fixed number of particles
			int particleCount = 15; // Set the fixed particle count

			for (int i = 0; i < particleCount; i++) {
				// Calculate the forward offset based on the fleet's facing direction
				float distanceInFront = 170f; // Distance in front of the fleet
				float facing = fleet.getFacing();
				Vector2f forwardOffset = new Vector2f(
						(float) Math.cos(Math.toRadians(facing)) * distanceInFront,
						(float) Math.sin(Math.toRadians(facing)) * distanceInFront
				);

				// Calculate the base particle position in front of the fleet
				Vector2f baseParticlePosition = new Vector2f(
						fleet.getLocation().x + forwardOffset.x,
						fleet.getLocation().y + forwardOffset.y
				);

				// Add a random offset to the particle position for a more organic look
				float randomOffsetX = (float) (Math.random() * 30 - 15); // Random offset between -15 and 15
				float randomOffsetY = (float) (Math.random() * 30 - 15); // Random offset between -15 and 15

				Vector2f particlePosition = new Vector2f(
						baseParticlePosition.x + randomOffsetX,
						baseParticlePosition.y + randomOffsetY
				);

				// Set particle parameters for effect
				Vector2f particleVelocity = new Vector2f(0, 0); // Static particle (keeping them together)
				float initialSize = 90f + (float) Math.random() * 30f; // Initial particle size variation (60 to 90)
				float rampUp = 1.5f; // Time to reach full brightness
				float duration = 4f + (float) Math.random() * 2f; // Particle lifespan (4 to 6 seconds)

				// Add the glowing particle effect on the campaign map
				Misc.addGlowyParticle(location, particlePosition, particleVelocity, initialSize, rampUp, duration, particleColor);
			}

		} else if (!fleet.isInHyperspace() && canUseToJumpToHyper() &&
				fleet.getContainingLocation() instanceof StarSystemAPI) {
			ping = Global.getSector().addPing(fleet, Pings.TRANSVERSE_JUMP);
			primed = true;
			// Play sound when ability is primed
			Global.getSoundPlayer().playSound("unsf_hyperspace_window", 1f, 2f, fleet.getLocation(), fleet.getVelocity());


			// Define the color for the particle effect with lower brightness and less white
			Color particleColor = new Color(150, 210, 180, 20); // Light green color, semi-transparent with lower alpha

			// Get the campaign location (useful for placing the particle on the campaign map)
			LocationAPI location = Global.getSector().getCurrentLocation();

			// Fixed number of particles
			int particleCount = 15; // Set the fixed particle count

			for (int i = 0; i < particleCount; i++) {
				// Calculate the forward offset based on the fleet's facing direction
				float distanceInFront = 170f; // Distance in front of the fleet
				float facing = fleet.getFacing();
				Vector2f forwardOffset = new Vector2f(
						(float) Math.cos(Math.toRadians(facing)) * distanceInFront,
						(float) Math.sin(Math.toRadians(facing)) * distanceInFront
				);

				// Calculate the base particle position in front of the fleet
				Vector2f baseParticlePosition = new Vector2f(
						fleet.getLocation().x + forwardOffset.x,
						fleet.getLocation().y + forwardOffset.y
				);

				// Add a random offset to the particle position for a more organic look
				float randomOffsetX = (float) (Math.random() * 30 - 15); // Random offset between -15 and 15
				float randomOffsetY = (float) (Math.random() * 30 - 15); // Random offset between -15 and 15

				Vector2f particlePosition = new Vector2f(
						baseParticlePosition.x + randomOffsetX,
						baseParticlePosition.y + randomOffsetY
				);

				// Set particle parameters for effect
				Vector2f particleVelocity = new Vector2f(0, 0); // Static particle (keeping them together)
				float initialSize = 90f + (float) Math.random() * 30f; // Initial particle size variation (60 to 90)
				float rampUp = 1.5f; // Time to reach full brightness
				float duration = 4f + (float) Math.random() * 2f; // Particle lifespan (4 to 6 seconds)

				// Add the glowing particle effect on the campaign map
				Misc.addGlowyParticle(location, particlePosition, particleVelocity, initialSize, rampUp, duration, particleColor);
			}


		} else {
			deactivate();
		}
	}

	@Override
	public void deactivate() {
		if (ping != null) {
			Global.getSector().removeScript(ping);
			ping = null;
		}
		super.deactivate();
	}

	@Override
	protected void applyEffect(float amount, float level) {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;

		//THIS BITCH FORCES FLEET TO SLOWDOWN TO MINIMUM SPEED
		//AT THE SAME TIME IT LOCKS DIRECTION OF FLEET SO FLEET SLAMS INTO HYPERSPACE WINDOW
		//CHAT GPT DID THIS SO LETS HOPE IT WONT BREAK SOMETHING IMPORTANT

		// Set minimum speed threshold
		float minSpeed = 100f;  // Adjust this to your desired minimum speed

		if (level > 0 && level < 1 && amount > 0) {
			float activateSeconds = getActivationDays() * Global.getSector().getClock().getSecondsPerDay();
			float speed = fleet.getVelocity().length();

			// Capture initial locked direction at the start of the effect
			if (lockedDirection == null) {
				// Capture the direction based on the current velocity or fleet facing if stationary
				if (speed > 1f) {
					lockedDirection = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(fleet.getVelocity()));
				} else {
					lockedDirection = Misc.getUnitVectorAtDegreeAngle(fleet.getFacing());
				}
			}

			// Calculate acceleration and delta speed
			float acc = Math.max(speed, 800f) / activateSeconds + fleet.getAcceleration();
			float ds = acc * amount;

			// Adjust ds to prevent speed from dropping below minSpeed
			if (speed - ds < minSpeed) {
				ds = speed - minSpeed;  // Ensure final speed equals minSpeed
			} else if (ds > speed) {
				ds = speed; // Cap ds to speed to prevent increasing speed
			}

			// Scale locked direction to new speed and apply it
			Vector2f newVelocity = new Vector2f(lockedDirection);
			newVelocity.scale(Math.max(speed - ds, minSpeed)); // Ensure it doesn't drop below minSpeed

			// Set the new velocity
			fleet.setVelocity(newVelocity.x, newVelocity.y);

			// Ensure facing matches locked direction
			fleet.setFacing(Misc.getAngleInDegrees(lockedDirection));

		} else {
			// Reset when effect is not active to restore control
			lockedDirection = null;
		}

		if (level == 1 && primed != null) {
			// Get the nearest star system to jump into
			StarSystemAPI nearestStar = findNearestStar();

			if (nearestStar != null && fleet.isInHyperspace() && canUseToJumpToSystem()) {
				// Get the location of the player fleet
				Vector2f fleetLocation = fleet.getLocation();

				// Calculate the distance scaling based on the distance to the nearest star and its maximum radius
				float distanceToStar = MathUtils.getDistance(fleetLocation, nearestStar.getLocation());
				float distScale = distanceToStar / nearestStar.getMaxRadiusInHyperspace();

				// Calculate the entry distance in the system based on the scaling factor
				float distInSystem = distScale * 25000f; // Using the provided magic number for system width

				// Calculate the angle between the nearest star and the player fleet
				float angle = VectorUtils.getAngle(nearestStar.getLocation(), fleetLocation);

				// Determine the exact location to jump into the star system
				Vector2f entryLocation = MathUtils.getPointOnCircumference(Misc.ZERO, distInSystem, angle);

				// Create a jump destination at the calculated location
				SectorEntityToken token = nearestStar.createToken(entryLocation.x, entryLocation.y);
				JumpDestination destination = new JumpDestination(token, null);

				// Perform the hyperspace transition
				Global.getSector().doHyperspaceTransition(fleet, fleet, destination);


			} else if (!fleet.isInHyperspace() && canUseToJumpToHyper()
				&& fleet.getContainingLocation() instanceof final StarSystemAPI system) {
				// System exit to hyperspace
				float cost = computeFuelCost();
				fleet.getCargo().removeFuel(cost);

                Vector2f offset = Vector2f.sub(fleet.getLocation(), system.getCenter().getLocation(), new Vector2f());

				float maxInSystem = 20000f;
				float maxInHyper = 2000f;

				// Scaling factor constrained to 0.5f maximum
				float f = Math.min(offset.length() / maxInSystem, 0.5f);

				// Calculate angle and offset for exit position in hyperspace
				float angle = Misc.getAngleInDegreesStrict(offset);
				Vector2f destOffset = Misc.getUnitVectorAtDegreeAngle(angle);
				destOffset.scale(f * maxInHyper);

				// Place fleet in hyperspace with calculated offset
				Vector2f.add(system.getLocation(), destOffset, destOffset);
				SectorEntityToken token = Global.getSector().getHyperspace().createToken(destOffset.x, destOffset.y);

				JumpDestination dest = new JumpDestination(token, null);
				Global.getSector().doHyperspaceTransition(fleet, fleet, dest);

			}
			primed = null;
		}
	}

	private StarSystemAPI findNearestStar() {
		CampaignFleetAPI fleet = getFleet();
		StarSystemAPI nearestStar = null;
		float minDistance = Float.MAX_VALUE;
		float maxDistance = 3000f;  // Example max distance in hyperspace units

		for (StarSystemAPI system : Global.getSector().getStarSystems()) {
			final SectorEntityToken anchor = system.getHyperspaceAnchor();
			if (anchor == null) continue;
			float distance = MathUtils.getDistance(fleet.getLocation(), anchor.getLocation());
			if (distance < minDistance && distance <= maxDistance) {
				minDistance = distance;
				nearestStar = system;
			}
		}
		return nearestStar;
	}

	@Override
	protected String getActivationText() {
		return super.getActivationText();
	}

	@Override
	protected void deactivateImpl() {
		cleanupImpl();
	}

	@Override
	protected void cleanupImpl() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
	}

	@Override
	public boolean isUsable() {
		if (!super.isUsable()) return false;
		if (getFleet() == null) return false;

		CampaignFleetAPI fleet = getFleet();

		if (fleet.isInHyperspaceTransition()) return false;

		if (TutorialMissionIntel.isTutorialInProgress()) return false;

		// Check for the nearest star
		StarSystemAPI nearestStar = findNearestStar();
		float distanceToStar = Float.MAX_VALUE; // Initialize to a large value

		// Calculate the distance to the nearest star if it exists
		if (nearestStar != null) {
			distanceToStar = MathUtils.getDistance(fleet.getLocation(), nearestStar.getLocation());
		}

		// Check if the fleet can jump to the system and is in hyperspace while being more than 1500 units from the star
		if (canUseToJumpToSystem() && fleet.isInHyperspace() && distanceToStar < 1500f) {
			return true;
		}

		// Check if the fleet can jump to hyperspace
		if (canUseToJumpToHyper() && !fleet.isInHyperspace()) {
            return fleet.isAIMode() || computeFuelCost() <= fleet.getCargo().getFuel();
		}

		return false;
	}

	/*
	public NascentGravityWellAPI getNearestWell(float maxDist) {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return null;
		if (!fleet.isInHyperspace()) return null;

		float minDist = Float.MAX_VALUE;
		NascentGravityWellAPI closest = null;
		List<Object> wells = fleet.getContainingLocation().getEntities(NascentGravityWellAPI.class);
		for (Object o : wells) {
			NascentGravityWellAPI well = (NascentGravityWellAPI) o;
			float dist = Misc.getDistance(well.getLocation(), fleet.getLocation());
			dist -= well.getRadius() + fleet.getRadius();
			if (dist > maxDist) continue;
			if (dist < minDist) {
				minDist = dist;
				closest = well;
			}
		}
		return closest;
	}
*/

	@Override
	public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;

		Color gray = Misc.getGrayColor();
		Color highlight = Misc.getHighlightColor();
		Color fuel = Global.getSettings().getColor("progressBarFuelColor");
		Color bad = Misc.getNegativeHighlightColor();

		LabelAPI title = tooltip.addTitle("Hyperspace window generator");

		tooltip.addPara("Jump into hyperspace without the use of a jump-point, or " +
						"jump into a star system across the hyperspace boundary near a nascent gravity well, " +
						"emerging near the entity corresponding to the gravity well.", PAD);

		float fuelCost = computeFuelCost();

		if (!fleet.isInHyperspace()) {
			if (fuelCost > fleet.getCargo().getFuel()) {
				tooltip.addPara("Not enough fuel.", bad, PAD);
			}
		}
		addIncompatibleToTooltip(tooltip, expanded);
	}

	public boolean hasTooltip() {
		return true;
	}

	@Override
	public void fleetLeftBattle(BattleAPI battle, boolean engagedInHostilities) {
		if (engagedInHostilities) {
			deactivate();
		}
	}

	@Override
	public void fleetOpenedMarket(MarketAPI market) {
		deactivate();
	}


	protected float computeFuelCost() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return 0f;

        return fleet.getLogistics().getFuelCostPerLightYear() * FUEL_USE_MULT;
	}

	@Override
	public boolean showCooldownIndicator() {
		return super.showCooldownIndicator();
	}
	@Override
	public boolean isOnCooldown() {
		return super.getCooldownFraction() < 1f;
	}



}





