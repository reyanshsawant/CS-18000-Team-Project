import java.io.*;
import java.util.ArrayList;

/**
 * Rating Manager
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Nikhil
 * @version April 17th, 2025
 */


public class RatingManager {
    private final String ratingsFile = "ratings.txt";

    public RatingManager() {
        // Ensures the file exists on startup
        File file = new File(ratingsFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating ratings file.");
            e.printStackTrace();
        }
    }

    // Adds a rating for a seller
    public synchronized void addRating(String sellerUsername, int rating) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ratingsFile, true))) {
            writer.write(sellerUsername + "," + rating);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing rating.");
            e.printStackTrace();
        }
    }

    // Gets all ratings for a specific seller
    public ArrayList<Integer> getRatingsForSeller(String sellerUsername) {
        ArrayList<Integer> ratings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ratingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(sellerUsername)) {
                    ratings.add(Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading ratings.");
            e.printStackTrace();
        }
        return ratings;
    }

    // Computes the average rating
    public double getAverageRating(String sellerUsername) {
        ArrayList<Integer> ratings = getRatingsForSeller(sellerUsername);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (int r : ratings) sum += r;
        return (double) sum / ratings.size();
    }
}
