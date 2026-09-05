package PvZ2.APproject.models;

public record LeaderboardEntry(
    int rank,
    String username,
    int seasonId,
    int levelNumber,
    int minigamesWon,
    int dailyQuests,
    int quests,
    Integer score,
    int gamesPlayed) {
}
