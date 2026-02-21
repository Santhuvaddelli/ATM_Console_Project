import java.sql.*;
import java.util.Scanner;

public class AdminPanel {

    public static void adminLogin() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Admin Username: ");
        String u = sc.next();
        System.out.print("Admin Password: ");
        String p = sc.next();

        Connection con = DBConnection.getConnection();
        PreparedStatement ps =
                con.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?");

        ps.setString(1, u);
        ps.setString(2, p);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            createUser();
        } else {
            System.out.println("Invalid Admin Credentials");
        }
    }

    private static void createUser() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Account Number: ");
        String acc = sc.next();
        System.out.print("PIN: ");
        int pin = sc.nextInt();
        System.out.print("Initial Balance: ");
        double bal = sc.nextDouble();

        Connection con = DBConnection.getConnection();
        PreparedStatement ps =
                con.prepareStatement("INSERT INTO users(account_number,pin,balance) VALUES(?,?,?)");

        ps.setString(1, acc);
        ps.setInt(2, pin);
        ps.setDouble(3, bal);

        ps.executeUpdate();
        System.out.println("User Created Successfully");
    }
}