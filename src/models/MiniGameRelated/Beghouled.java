package models.MiniGameRelated;

import models.LevelData;
import models.plants.Plant;
import java.util.Random;

public class Beghouled extends MiniGame {
    private Plant[][] grid;
    private int matchCount;
    private int targetMatches;
    private transient final Random random = new Random();
    private boolean[][] craters;

    public Beghouled(LevelData data) {
        super(data);
        this.grid = new Plant[5][9];
        this.craters = new boolean[5][9];
        this.targetMatches = 10;
        this.matchCount = 0;
    }

    public void initializeStage() {
        resetGrid();
        matchCount = 0;
        isGameOver = false;
    }


    public void processInteraction() {
        // Interaction is handled through swapPlants(row,col,row,col).
        // This method keeps compatibility with the MiniGame architecture.
    }

    public boolean swapPlants(int r1, int c1, int r2, int c2) {
        if (!valid(r1,c1) || !valid(r2,c2)) return false;
        if (Math.abs(r1-r2)+Math.abs(c1-c2) != 1) return false;
        Plant temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
        if (hasMatch()) {
            removeMatches();
            return true;
        }
        temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
        return false;
    }

    public void checkRules() {
        if (matchCount >= targetMatches) {
            WinGame();
        }
    }

    private boolean hasMatch() {
        for (int r=0;r<5;r++) {
            for (int c=0;c<9;c++) {
                if (countLine(r,c,1,0)>=3 || countLine(r,c,0,1)>=3)
                    return true;
            }
        }
        return false;
    }

    private int countLine(int r,int c,int dr,int dc){
        if(grid[r][c]==null) return 0;
        int count=1;
        for(int i=1;i<9;i++){
            int nr=r+dr*i,nc=c+dc*i;
            if(!valid(nr,nc)||grid[nr][nc]==null) break;
            if(grid[nr][nc].getClass()!=grid[r][c].getClass()) break;
            count++;
        }
        return count;
    }

    private void removeMatches(){
        boolean[][] remove=new boolean[5][9];
        for(int r=0;r<5;r++){
            for(int c=0;c<9;c++){
                if(countLine(r,c,1,0)>=3||countLine(r,c,0,1)>=3)
                    remove[r][c]=true;
            }
        }
        for(int r=0;r<5;r++){
            for(int c=0;c<9;c++){
                if(remove[r][c]){
                    grid[r][c]=null;
                    matchCount++;
                }
            }
        }
        collapse();
    }

    private void collapse(){
        for(int c=0;c<9;c++){
            int write=4;
            for(int r=4;r>=0;r--){
                if(grid[r][c]!=null) grid[write--][c]=grid[r][c];
            }
            while(write>=0) grid[write--][c]=null;
        }
    }

    private boolean valid(int r,int c){
        return r>=0&&r<5&&c>=0&&c<9;
    }

    void resetGrid(){
        for(int r=0;r<5;r++)
            for(int c=0;c<9;c++)
                grid[r][c]=null;
    }

    void replaceTileWithCrater(){
        for(int r=0;r<5;r++)
            for(int c=0;c<9;c++)
                if(grid[r][c]==null) craters[r][c]=true;
    }

    void WinGame(){
        isGameOver=true;
    }
}
