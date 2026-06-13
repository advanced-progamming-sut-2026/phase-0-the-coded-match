package enums;

public enum SunType {
    NORMAL {
        @Override
        public void explode() {

        }
    },
    SPECIAL {
        @Override
        public void explode() {

        }
    },
    RADIOACTIVE {
        @Override
        public void explode() {

        }
    };

    public abstract void explode();
}
