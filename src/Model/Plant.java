package Model;

import Enums.PlantCategory;
import Enums.PlantTag;
import Enums.PlantType;

import java.util.List;

    public abstract class Plant {

        protected PlantType type;
        protected String name;

        protected PlantCategory category;
        protected List<PlantTag> tags;

        protected int cost;

        protected int maxHealth;
        protected int currentHealth;

        protected int damage;

        protected int level;

        protected double rechargeTime;
        protected double remainingCooldown;

        protected String baseAbility;
        protected String plantFoodEffect;

        public Plant(PlantType type,
                     String name,
                     PlantCategory category,
                     List<PlantTag> tags,
                     int cost,
                     int maxHealth,
                     int damage,
                     double rechargeTime,
                     String baseAbility,
                     String plantFoodEffect) {

            this.type = type;
            this.name = name;
            this.category = category;
            this.tags = tags;

            this.cost = cost;

            this.maxHealth = maxHealth;
            this.currentHealth = maxHealth;

            this.damage = damage;

            this.level = 1;

            this.rechargeTime = rechargeTime;
            this.remainingCooldown = 0;

            this.baseAbility = baseAbility;
            this.plantFoodEffect = plantFoodEffect;
        }

        public abstract void performAction();

        public abstract void activatePlantFood();

        public void takeDamage(int damage) {
            currentHealth = Math.max(0, currentHealth - damage);
        }

        public boolean isDead() {
            return currentHealth <= 0;
        }

        public boolean isReadyToPlant() {
            return remainingCooldown <= 0;
        }

        public void startCooldown() {
            remainingCooldown = rechargeTime;
        }

        public void reduceCooldown(double time) {
            remainingCooldown = Math.max(0, remainingCooldown - time);
        }

        public void levelUp() {
            level++;
        }

        public PlantType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public PlantCategory getCategory() {
            return category;
        }

        public List<PlantTag> getTags() {
            return tags;
        }

        public int getCost() {
            return cost;
        }

        public int getCurrentHealth() {
            return currentHealth;
        }

        public int getDamage() {
            return damage;
        }

        public int getLevel() {
            return level;
        }
    }


