import java.util.Scanner;

public class MeterToFeetConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter distance in meters: ");
        double meters = scanner.nextDouble();

        double feet = meters * 3.28084;

        System.out.println(meters + " meters is equal to " + feet + " feet.");

        scanner.close();
    }
}
