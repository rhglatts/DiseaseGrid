package asdf.griddisease;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class Homework {

    public static void main(String[] args) throws IOException {

        Scanner scnr = new Scanner(System.in);
        String again = "";
        while (true) {
            //calls simulation and assigns its return value to be used for 
            //delete() method which deletes files
            int timeSteps = simulation();
            System.out.println("Run the simulation again? yes/no");
            again = scnr.next();
            delete(timeSteps);
            if (again.equals("no")) {
                break;
            }
        }
        scnr.close();
    }
    public static void delete(int timeSteps) throws IOException {
        String fileDelete;
            Path path = Paths.get("step0.txt");
            Files.delete(path);
            for (int i = 1; i < timeSteps; ++i) {
            fileDelete = "step" + i + ".txt";
            path = Paths.get(fileDelete);
            Files.delete(path);
        }
    }
    public static int simulation() throws IOException {
           
        int rows = -1;
        int columns = -1;
        int patient0row = -1;
        int patient0column = -1;
        double infectionRate = -1;
        double recoverRate = -1;
        int timeSteps = -1;
        Scanner scanner = new Scanner(System.in);
        
        
        //Gets the number of rows and columns for the grid
        System.out.println("\n--------");
        System.out.println("Enter rows and columns, seperated by a space:");
        try {
            rows = scanner.nextInt();
            columns = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Error entering colums and rows");
        }
        //Gets the location of patient 0 and sends error if it is not a valid value
        while (true) {
            System.out.println("Location of patient zero row and column:");

            try {
                patient0row = scanner.nextInt();
                patient0column = scanner.nextInt();
                if (patient0column >= 0 && patient0column <= columns
                        && patient0row >= 0 && patient0row <= rows) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Not a valid value for patient zero");
                break;
            }
        }

        //Gets infection rate only if it is between 0 and 1
        while (true) {
            System.out.println("Infection rate: (0-1)");
            infectionRate = scanner.nextDouble();
            if (infectionRate >= 0 && infectionRate <= 1) {
                break;
            }
        }
        //Gets recovery rate if it is between 0 and 1
        while (true) {
            System.out.println("Recover rate: (0-1)");
            recoverRate = scanner.nextDouble();
            if (recoverRate >= 0 && recoverRate <= 1) {
                break;
            }
        }
        //Gets time steps if it is over 0
        while (true) {
            System.out.println("Number of time steps: (use an integer greater than 0)");
            timeSteps = scanner.nextInt();
            if (timeSteps > 0) {
                break;
            }
        }
        
        //Simulation starts
        //creating files base off previous file
        int size = (columns * rows);
        for (int i = 0; i < timeSteps; i++) {
            String letter = "";
            int infected = 0;
            int recovered = 0;
            int infectedNeighbors = 0;
            int suceptible = 0;
            
            if (i == 0) {
                File current = new File("step" + i + ".txt");
                FileWriter writer = new FileWriter(current);
                for (int z = 0; z < rows * columns; z++) {
                    if (z == (patient0row - 1) * rows + (patient0column - 1)) {
                        letter = "I";
                    } else {
                        letter = "S";
                    }
                    writer.append(letter);
                }
                writer.close();
                infected = 1;
                suceptible = (size) - 1;
            } 
            else {
            	 File current = new File("step"+ i + ".txt");
                 FileWriter writer = new FileWriter(current);
                 File previous = new File("step"+ (i-1) + ".txt");
                 Scanner filescanner = new Scanner(previous);
                 String filetext = filescanner.nextLine();
                    
                    for (int z = 0; z < (size); z++) {
                        char character1 = filetext.charAt(z);
                        //checks to see if potential neighbor is in grid and then
                        //if they are infected
                        if (character1 == 'S') {
                            if (z-columns >= 0) {
                            char up = filetext.charAt(z-columns);
                            if (up == 'I') {
                                    infectedNeighbors++;
                                }
                        }
                        if (z+columns < size) {
                            char down = filetext.charAt(z+columns);
                            if (down == 'I') {
                                    infectedNeighbors++;
                                }
                        }
                        if (z-1 >= 0) {
                            char left = filetext.charAt(z-1);
                            if (left == 'I') {
                                    infectedNeighbors++;
                                }
                        }
                        if (z+1 < size) {
                            char right = filetext.charAt(z+1);
                            if (right == 'I') {
                                    infectedNeighbors++;
                                }
                        }
                            if (becomeInfected(infectionRate, infectedNeighbors)) {
                                writer.append("I");
                                infected++;
                            } else {
                                writer.append("S");
                                suceptible++;
                            }
                            
                            infectedNeighbors = 0;
                        }
                        //If becomeCured is true person is recovered, else they 
                        //are infected
                        if (character1 == 'I') {
                            if (becomeCured(recoverRate)) {
                                writer.append("R");
                                recovered++;
                            } else {
                                writer.append("I");
                                infected++;
                            } 
                        }
                        //Add recovered to count and add to next file
                        if (character1 == 'R') {
                        	writer.append("R");
                                recovered++;
                        }
                    }
                    filescanner.close();
                    writer.close();
        }
            System.out.println("\n--------");
            System.out.println("Step " + (i+1));
            System.out.println("Number of suceptible: " + suceptible);
            System.out.println("Number of infected: " + infected);
            System.out.println("Number of recovered: " + recovered);
            System.out.println("Ratio of infected: " + ratioOfInfected(size, infected));
        }
        return timeSteps;
}

//returns true if the random number generated is less than or equal to the 
//infection rate, and false if it is greater than
    public static boolean becomeInfected(double infectionRate, int infectedNeighbors) {
        double rate = infectionRate * infectedNeighbors;
        if (rate > 1) {
            rate = 1;
        }
        return Math.random() <= rate;
    }

    //returns true if the random number generated is less than or equal to the 
    //infection rate, and false if it is greater than
    public static boolean becomeCured(double recoverRate) {
        return Math.random() <= recoverRate;
    }

    //returns the ratio of infected by dividing infected by the size of the grid
    public static double ratioOfInfected(int size, double infected) {
        return infected / (size);
    }
}
