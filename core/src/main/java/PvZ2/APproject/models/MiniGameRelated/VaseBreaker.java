package PvZ2.APproject.models.MiniGameRelated;

import PvZ2.APproject.enums.LevelType;
import PvZ2.APproject.enums.VaseType;
import PvZ2.APproject.models.DroppedSeedPacket;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class VaseBreaker extends MiniGame {

    private final int stageNumber;
    private final List<Vase> activeVases;
    private final List<DroppedSeedPacket> groundSeeds;
    private final List<String> collected = new ArrayList<>();

    public VaseBreaker(int stageNumber) {
        super(createVasebreakerLevelData(stageNumber));
        this.stageNumber = stageNumber;
        this.activeVases = new ArrayList<>();
        this.groundSeeds = new ArrayList<>();

        setupStage(stageNumber);
    }


    public static class Vase {
        private final int x;
        private final int y;
        private final VaseType vaseType;
        private final PlantData plantContent;
        private final ZombieData zombieContent;
        private boolean broken;

        public Vase(int x, int y, VaseType vaseType, PlantData plantContent, ZombieData zombieContent) {
            this.x = x;
            this.y = y;
            this.vaseType = vaseType;
            this.plantContent = plantContent;
            this.zombieContent = zombieContent;
            this.broken = false;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public VaseType getVaseType() { return vaseType; }
        public PlantData getPlantContent() { return plantContent; }
        public ZombieData getZombieContent() { return zombieContent; }
        public boolean isBroken() { return broken; }
        public void setBroken(boolean broken) { this.broken = broken; }
    }

    private static class VaseContentHolder {
        VaseType type;
        PlantData plant;
        ZombieData zombie;

        VaseContentHolder(VaseType type, PlantData plant, ZombieData zombie) {
            this.type = type;
            this.plant = plant;
            this.zombie = zombie;
        }
    }

    private static LevelData createVasebreakerLevelData(int stageNumber) {
        LevelData data = new LevelData();
        data.setLevelNumber(stageNumber);
        data.setLevelType(LevelType.VASEBREAKER);
        data.setUnlocked(true);
        data.setMap(new GameMap(5, 9));
        return data;
    }

    private void setupStage(int stage) {
        List<VaseContentHolder> pool = new ArrayList<>();
        PlantRepository plants = PlantRepository.getInstance();
        ZombieRepository zombies = ZombieRepository.getInstance();

        int startCol;
        int endCol = 8;

        switch (stage) {
            case 1:
                // stage 1 (easy) 15 vases col 6-8
                startCol = 6;
                addVasesToPool(pool, 2, VaseType.BASIC_VASE, null, null);
                addVasesToPool(pool, 4, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Default"));
                addVasesToPool(pool, 3, VaseType.PLANT_VASE, plants.findByName("squash"), null);
                addVasesToPool(pool, 5, VaseType.BASIC_VASE, plants.findByName("peashooter"), null);
                addVasesToPool(pool, 1, VaseType.GARGANTUAR_VASE, null, zombies.findByDisplayName("gargantuar"));
                break;

            case 2:
                // stage 2 (medium) 20 vases col 5-8
                startCol = 5;
                addVasesToPool(pool, 3, VaseType.BASIC_VASE, plants.findByName("cactus"), null);
                addVasesToPool(pool, 5, VaseType.BASIC_VASE, plants.findByName("peashooter") , null);
                addVasesToPool(pool, 3, VaseType.PLANT_VASE, plants.findByName("cabbage-pult"), null);
                addVasesToPool(pool, 4, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Default"));
                addVasesToPool(pool, 3, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Conehead Zombie"));
                addVasesToPool(pool, 1, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Buckethead Zombie"));
                addVasesToPool(pool, 1, VaseType.GARGANTUAR_VASE, null, zombies.findByDisplayName("gargantuar"));
                break;

            case 3:
                // stage 3 (hard) 25 vases col 4-8
                startCol = 4;
                addVasesToPool(pool, 10, VaseType.BASIC_VASE, plants.findByName("peashooter"), null);
                addVasesToPool(pool, 4, VaseType.PLANT_VASE, plants.findByName("squash"), null);
                addVasesToPool(pool, 5, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Conehead Zombie"));
                addVasesToPool(pool, 4, VaseType.BASIC_VASE, null, zombies.findByDisplayName("Buckethead Zombie"));
                addVasesToPool(pool, 2, VaseType.GARGANTUAR_VASE, null, zombies.findByDisplayName("gargantuar"));
                break;

            default:
                throw new IllegalArgumentException("Invalid Vasebreaker stage: " + stage);
        }


        Collections.shuffle(pool);


        int poolIndex = 0;
        int rows = getGameMap().getRows();
        for (int col = startCol; col <= endCol; col++) {
            for (int row = 1; row <= rows; row++) {
                if (poolIndex >= pool.size()) break;

                VaseContentHolder item = pool.get(poolIndex++);
                Vase vase = new Vase(col, row, item.type, item.plant, item.zombie);
                activeVases.add(vase);
                Tile tile = getGameMap().getTile(col, row); //
                if (tile != null) {
                    tile.setVase(vase);
                }
            }
        }
    }

    private void addVasesToPool(List<VaseContentHolder> pool, int count, VaseType type, PlantData plant, ZombieData zombie) {
        for (int i = 0; i < count; i++) {
            pool.add(new VaseContentHolder(type, plant, zombie));
        }
    }

    public String breakVaseAt(int x, int y) {
        Vase targetVase = getVaseAt(x, y);

        if (targetVase == null) {
            return "No vase exists at (" + x + ", " + y + ")!";
        }
        if (targetVase.isBroken()) {
            return "The vase at (" + x + ", " + y + ") is already broken!";
        }

        targetVase.setBroken(true);
        getGameMap().getTile(x,y).removeVase();

        if (targetVase.getZombieContent() != null) {
            Zombie zombie = new Zombie(targetVase.getZombieContent(), x, y);
            getActiveZombies().add(zombie);
            return "Vase broken! A " + zombie.getData().getDisplayName() + " emerged at (" + x + ", " + y + ")!";
        }


        if (targetVase.getPlantContent() != null) {
            String plantName = targetVase.getPlantContent().getDisplayName();
            collected.add(plantName.toLowerCase());
            return "Vase broken! You obtained a " + plantName + " seed packet!";
        }

        return "Vase broken! It was empty.";
    }

    public Vase getVaseAt(int x, int y) {
        for (Vase vase : activeVases) {
            if (vase.getX() == x && vase.getY() == y) {
                return vase;
            }
        }
        return null;
    }

    public boolean hasUnbrokenVaseAt(int x, int y) {
        Vase v = getVaseAt(x, y);
        return v != null && !v.isBroken();
    }

    public String pickUpSeedAt(int x, int y) {
        Iterator<DroppedSeedPacket> iterator = groundSeeds.iterator();
        while (iterator.hasNext()) {
            DroppedSeedPacket packet = iterator.next();

            if (packet.getX() == x && packet.getY() == y) {
                String seedName = packet.getPlantType().toLowerCase();
                collected.add(seedName);
                iterator.remove();
                return "Picked up " + packet.getPlantType() + " seed packet from (" + x + ", " + y + ")!";
            }
        }
        return "No seed packet on ground at (" + x + ", " + y + ")!";
    }

    public boolean consumeSeedPacket(String plantName) {
        return collected.remove(plantName.toLowerCase());
    }

    public List<DroppedSeedPacket> getAvailableSeedPackets() {
        return groundSeeds;
    }

    public void updateGroundSeeds() {
        Iterator<DroppedSeedPacket> iterator = groundSeeds.iterator();
        while (iterator.hasNext()) {
            DroppedSeedPacket packet = iterator.next();
            packet.updateTick();

            if (packet.isExpired()) {
                iterator.remove();
            }
        }
    }


    public boolean winConditionsChecked() {
        boolean allVasesBroken = true;
        for (Vase v : activeVases) {
            if (!v.isBroken()) {
                allVasesBroken = false;
                break;
            }
        }
        boolean allZombiesDead = getActiveZombies().isEmpty();

        return allVasesBroken && allZombiesDead;
    }


    public boolean hasCollectedSeed(String plantName) {
        if (plantName == null) return false;
        return collected.contains(plantName.toLowerCase());
    }

    public List<String> getCollectedSeedPackets() {
        return Collections.unmodifiableList(new ArrayList<>(collected));
    }

    public boolean hasZombieReachedHouse() {
        for (Zombie zombie : getActiveZombies()) {
            if (!zombie.isDead() && zombie.getX() <= 0) return true;
        }
        return false;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public List<Vase> getActiveVases() {
        return activeVases;
    }
}
