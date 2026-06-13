package models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

    public class ZombieRepository {
        private final List<ZombieData> zombies;

        public ZombieRepository(String jsonPath) {
            this.zombies = loadZombies(jsonPath);
        }

        private List<ZombieData> loadZombies(String jsonPath) {
            //TODO
            return null;
        }

        public List<ZombieData> getAllZombies() {
            return zombies;
        }

        public ZombieData findByAlias(String alias) {
            //TODO
            return null;
        }

        public ZombieData findById(String id) {
          //TODO
            return null;
        }

        public List<ZombieData> findBySeason(String season) {
            //TODO
            return null;
        }
    }

