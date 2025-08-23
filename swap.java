import java.util.Scanner;

public class SwapNumbers {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number (a): ");
        int a = scanner.nextInt();

        System.out.print("Enter second number (b): ");
        int b = scanner.nextInt();

        System.out.println("\n--- Swapping using a third variable ---");
        int temp = a;
        a = b;
        b = temp;
        System.out.println("After swap: a = " + a + ", b = " + b);

        
        System.out.print("\nEnter first number again (a): ");
        a = scanner.nextInt();

        System.out.print("Enter second number again (b): ");
        b = scanner.nextInt();

        System.out.println("\n--- Swapping without using a third variable ---");
        a = a + b;
        b = a - b;
        a = a - b;
        System.out.println("After swap: a = " + a + ", b = " + b);

        scanner.close();
    }
}
