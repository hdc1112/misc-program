import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.LongStream;

/**
 * dimension: 1
 * k: 2
 */
public class SimD1K2 {
    // random point generator
    private final Random random = new Random();
    // total centroids
    private final int k = 2;
    // number of data points for a refresh
    private final int refreshThreshold = 10;
    // total simulated data points
    private final long totalPoints = 5000;
    // temporary update holder
    private Queue<Update> queue = new LinkedList<>();

    // centroid 0 truth
    private final double mu0Truth = 3d;
    private final double sigma0Truth = 1d;

    // centroid 0 estimate
    private double mu0 = 0d;
    private int points0 = 0;

    // centroid 1 truth
    private final double mu1Truth = 10d;
    private final double sigma1Truth = 3d;

    // centroid 1 estimate
    private double mu1 = 13d;
    private int points1 = 0;

    public void simulate() {
        System.out.println("before simulation");
        stateDump();
        LongStream.range(0, totalPoints).forEach(i -> simulateOnePoint());
        System.out.println("after simulation");
        stateDump();
    }

    private void stateDump() {
        System.out.println(String.format("mu0truth=%f, mu0=%f, points0=%d; mu1truth=%f, mu1=%f, points1=%d", mu0Truth,
                mu0, points0, mu1Truth, mu1, points1));
    }

    private void simulateOnePoint() {
        final double point;
        switch (random.nextInt(k)) {
            case 0:
                point = mu0Truth + random.nextGaussian() * sigma0Truth;
                break;
            case 1:
                point = mu1Truth + random.nextGaussian() * sigma1Truth;
                break;
            default:
                throw new RuntimeException("mismatch in k");
        }
        queue.offer(calculateUpdate(point));
        if (queue.size() >= refreshThreshold) {
            refresh(queue);
            queue.clear();
        }
    }

    private Update calculateUpdate(final double point) {
        // update step follows: dx.doi.org/10.13140/RG.2.1.1608.6240
        switch (closest(point)) {
            case 0:
                return new Update(0, mu0 + 1d / (points0 + 1) * (point - mu0));
            case 1:
                return new Update(1, mu1 + 1d / (points1 + 1) * (point - mu1));
            default:
                throw new RuntimeException("choice not valid");
        }
    }

    private void refresh(final Queue<Update> queue) {
        final List<Update> k0queue = new ArrayList<>();
        final List<Update> k1queue = new ArrayList<>();
        queue.forEach(update -> {
            switch (update.id) {
                case 0:
                    k0queue.add(update);
                    break;
                case 1:
                    k1queue.add(update);
                    break;
                default:
                    throw new RuntimeException("out of choice");
            }
        });
        // update centroid-0
        mu0 = k0queue.stream().mapToDouble(u -> u.muNew).average().orElse(mu0);
        points0 += k0queue.size();
        // update centroid-1
        mu1 = k1queue.stream().mapToDouble(u -> u.muNew).average().orElse(mu1);
        points1 += k1queue.size();
    }

    private int closest(final double point) {
        return Math.abs(point - mu0) < Math.abs(point - mu1) ? 0 : 1;
    }

    private static class Update {
        final int id;
        final double muNew;

        public Update(final int id, final double muNew) {
            this.id = id;
            this.muNew = muNew;
        }
    }

    public static void main(String[] args) {
        new SimD1K2().simulate();
    }
}
