package models;

import enums.VaseContent;

public abstract class Vase {
    private String vaseType;
    private VaseContent contains;
    private boolean isBroken;

    private Zombie hiddenGhoul;
    private DroppedSeedPacket hiddenSeedPacket;

    public Vase(String vaseType, VaseContent contains){
        this.vaseType=vaseType;
        this.contains = contains;
    }

     public abstract void breakVase();



}
