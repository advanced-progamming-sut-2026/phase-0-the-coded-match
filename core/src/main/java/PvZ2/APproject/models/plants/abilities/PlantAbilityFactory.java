package PvZ2.APproject.models.plants.abilities;

import PvZ2.APproject.enums.PlantAbility;

import java.util.HashMap;
import java.util.Map;

public class PlantAbilityFactory {
    private static final Map<PlantAbility, PlantAbilityHandler> HANDLERS = new HashMap<>();

    static {
        PlantAbilityHandler singleShot = new ShootAbility(1);
        HANDLERS.put(PlantAbility.SHOOT, singleShot);
        HANDLERS.put(PlantAbility.SHOOT_FORWARD, singleShot);
        HANDLERS.put(PlantAbility.DAMAGE_FIRST_ZOMBIE_IN_LANE, singleShot);
        HANDLERS.put(PlantAbility.DOUBLE_SHOT, new ShootAbility(2));
        HANDLERS.put(PlantAbility.QUAD_SHOT, new ShootAbility(4));
        HANDLERS.put(PlantAbility.TRIPLE_LANE_SHOT, new MultiLaneShootAbility());
        HANDLERS.put(PlantAbility.PIERCE_SHOT, new ShootAbility(1));
        HANDLERS.put(PlantAbility.RAPID_SHOOT, new ShootAbility(5));
        HANDLERS.put(PlantAbility.BIG_PEA, new ShootAbility(1, 20));
        HANDLERS.put(PlantAbility.PRODUCE_SUN, new ProduceSunAbility(false));
        HANDLERS.put(PlantAbility.INSTANT_SUN, new ProduceSunAbility(true));
        HANDLERS.put(PlantAbility.LOB, new LobAbility(1));
        HANDLERS.put(PlantAbility.RAPID_LOB, new LobAbility(3));
        HANDLERS.put(PlantAbility.EXPLODE, new ExplodeAbility(1));
        HANDLERS.put(PlantAbility.POWER_EXPLODE, new ExplodeAbility(2));
        HANDLERS.put(PlantAbility.MELEE_ATTACK, new MeleeAttackAbility(1));
        HANDLERS.put(PlantAbility.POWER_MELEE, new MeleeAttackAbility(3));
        HANDLERS.put(PlantAbility.HOMING_ATTACK, new HomingAttackAbility(1));
        HANDLERS.put(PlantAbility.POWER_HOMING, new HomingAttackAbility(3));
        HANDLERS.put(PlantAbility.MINT_BOOST, new MintBoostAbility());
        HANDLERS.put(PlantAbility.REINFORCE, new ReinforceAbility());
    }

    private PlantAbilityFactory() {
    }

    public static PlantAbilityHandler getHandler(String abilityName) {
        if (abilityName == null || abilityName.trim().isEmpty()) {
            return null;
        }
        try {
            PlantAbility ability = PlantAbility.valueOf(normalize(abilityName));
            return HANDLERS.get(ability);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static String normalize(String abilityName) {
        return abilityName.trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }
}
