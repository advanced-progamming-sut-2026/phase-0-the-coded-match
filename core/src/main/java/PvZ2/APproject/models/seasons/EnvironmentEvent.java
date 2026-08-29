package PvZ2.APproject.models.seasons;

public class EnvironmentEvent {
    public enum EnvironmentEventType {
        NONE,
        SANDSTORM,
        ICE_WIND,
        TIDE
    }
    private EnvironmentEventType type = EnvironmentEventType.NONE;
    private float timer = 0f;
    private float duration = 0f;

    public EnvironmentEvent(EnvironmentEventType type, float duration) {
        this.type = type;
        this.duration = duration;
    }

    public EnvironmentEventType getType() {
        return type;
    }

    public float getDuration() {
        return duration;
    }


//    public void playEffect(EnvironmentEventType effect, float duration) {
//        this.activeEffect = effect;
//        this.effectTimer = 0f;
//        this.effectDuration = duration;
//    }
}
