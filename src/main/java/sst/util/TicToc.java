package sst.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicToc {
    public List<LocalDateTime> times = new ArrayList<>(10);

    public void tic() {
        LocalDateTime currentTime = LocalDateTime.now();
        times.add(currentTime);
    }

    public void toc(String message) {
        int timesSize = times.size();
        LocalDateTime lastTime = this.times.get(timesSize - 1);
        Duration difference = Duration.between(lastTime, LocalDateTime.now());
        System.out.println("took: " + difference.toString() + " - " + message);
        this.times.remove(timesSize - 1);
    }
}
