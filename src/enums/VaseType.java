package enums;

import static enums.VaseContent.*;

public enum VaseType {
    BASIC_VASE("Basic", EMPTY),
    GARGANTUAR_VASE("Gargantuar", GARGANTUAR ),
    PLANT_VASE("Plant", SEED_PACKET);

    private final String type;
    private final VaseContent content;

    VaseType(String type, VaseContent content){
        this.type = type;
        this.content = content;
    }
}
