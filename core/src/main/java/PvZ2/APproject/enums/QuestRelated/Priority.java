package enums.QuestRelated;

public enum Priority{
    CRITICAL(1),
    HIGH(2),
    MEDIUM(3),
    LOW(4);

    private final int rank;

    Priority(int rank){
        this.rank = rank;
    }

    public int getRank(){
        return rank;
    }


}
