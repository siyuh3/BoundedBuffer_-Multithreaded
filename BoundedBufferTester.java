import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoundedBufferTester {

    // Test buffer for sample implementation
    private static BoundedBuffer boundedBuffer = new BoundedBuffer(50, false);
    // Test buffer for semaphore implementation
    private static BoundedBuffer boundedBuffer2 = new BoundedBuffer(50, true);


    public static void main(String[] args) throws InterruptedException {

        // Sample implementation
        System.out.println("Using sample implementation buffer...");
        List<RandomNumberGenerator> randomNumberGenerators = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // Create a thread and start
            RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(1);
            randomNumberGenerators.add(randomNumberGenerator);
        }
        for (int i = 0; i < 3; i++) {
            RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(0);
            randomNumberGenerators.add(randomNumberGenerator);
        }
        for (RandomNumberGenerator randomNumberGenerator : randomNumberGenerators) {
            randomNumberGenerator.start();
        }

        // Wait for threads to finish work
        for (RandomNumberGenerator randomNumberGenerator : randomNumberGenerators) {
            randomNumberGenerator.join();
        }

        // Semaphore implementation test
        System.out.println("\nUsing buffer implemented with semaphores...");
        List<RandomNumberGeneratorSemaphore> randomNumberGeneratorsSemaphores = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // Create thread and start
            RandomNumberGeneratorSemaphore randomNumberGenerator = new RandomNumberGeneratorSemaphore(1);
            randomNumberGeneratorsSemaphores.add(randomNumberGenerator);
        }

        for (int i = 0; i < 3; i++) {
            RandomNumberGeneratorSemaphore randomNumberGeneratorSemaphore = new RandomNumberGeneratorSemaphore(0);
            randomNumberGeneratorsSemaphores.add(randomNumberGeneratorSemaphore);
        }

        for (RandomNumberGeneratorSemaphore randomNumberGenerator : randomNumberGeneratorsSemaphores) {
            randomNumberGenerator.start();
        }

        // Wait for threads to finish
        for (RandomNumberGeneratorSemaphore randomNumberGenerator : randomNumberGeneratorsSemaphores) {
            randomNumberGenerator.join();
        }
    }


    // Thread class to generate random numbers and insert to sample buffer
    private static class RandomNumberGenerator extends Thread{

        private Random random = new Random();
        private int id;

        public RandomNumberGenerator(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            for (int i = 0; i < 15; i++) {
                // Generate random number
                int randomNum = random.nextInt(Integer.MAX_VALUE);
                String randomStr = this.getId() + "-" + randomNum;
                try {
                    // Add to sample buffer
                    if (id == 1) {
                        boundedBuffer.deposit(randomStr);
                        System.out.println("Inserted to bounded buffer " + randomStr);
                    }
                    else {
                        System.out.println("Fetched: " + boundedBuffer.fetch());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    // Thread class to generate random numbers and insert to semaphore buffer
    private static class RandomNumberGeneratorSemaphore extends Thread{

        private Random random = new Random();
        private int id;

        public RandomNumberGeneratorSemaphore(int id) {
            this.id = id;
        }
        @Override
        public void run() {

            for (int i = 0; i < 15; i++) {
                // Generate random number
                int randomNum = random.nextInt(Integer.MAX_VALUE);
                String randomStr = this.getId() + "-" + randomNum;
                try {
                    // Add to semaphore  buffer
                    if (id == 1) {
                        boundedBuffer2.deposit2(randomStr);
                        System.out.println("Inserted to bounded buffer semaphore " + randomStr);
                    }
                    else {
                        System.out.println("Fetched: " + boundedBuffer2.fetch2());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
