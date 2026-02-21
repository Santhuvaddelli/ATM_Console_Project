import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.println("1. Admin Login");
        System.out.println("2. ATM User Login");
        System.out.print("Choose: ");

        int choice = sc.nextInt();

        if (choice == 1) {
            AdminPanel.adminLogin();
        } else if (choice == 2) {
            ATM.startATM();
        } else {
            System.out.println("Invalid Choice");
        }
    }
}