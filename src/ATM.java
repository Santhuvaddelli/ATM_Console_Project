import java.sql.*;
import java.util.Scanner;

public class ATM {

    public static void startATM() throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.print("Account Number: ");
        String acc = sc.next();

        try (Connection con = DBConnection.getConnection()) {

            if (!isAccountExists(con, acc)) {
                System.out.println("Account not found");
                return;
            }

            while (true) {
                System.out.println("\n1. Withdraw");
                System.out.println("2. Deposit");
                System.out.println("3. Mini Statement");
                System.out.println("4. Exit");
                System.out.print("Choose: ");

                int choice = sc.nextInt();

                if (choice == 4) {
                    System.out.println("Thanks for using our ATM service");
                    break;
                }

                if (choice < 1 || choice > 3) {
                    System.out.println("Invalid choice. Please select a valid option.");
                    continue;
                }

                System.out.print("Enter PIN: ");
                int pin = sc.nextInt();

                if (!isPinValid(con, acc, pin)) {
                    System.out.println("Invalid PIN");
                    continue;
                }

                switch (choice) {
                    case 1 -> withdraw(con, sc, acc);
                    case 2 -> deposit(con, sc, acc);
                    case 3 -> showMiniStatement(con, acc);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error occurred. Please try again later.");
        }
    }

    private static boolean isAccountExists(Connection con, String acc) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE account_number=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isPinValid(Connection con, String acc, int pin) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE account_number=? AND pin=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);
            ps.setInt(2, pin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static double getBalance(Connection con, String acc) throws SQLException {
        String sql = "SELECT balance FROM users WHERE account_number=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        return 0;
    }

    private static void withdraw(Connection con, Scanner sc, String acc) throws SQLException {

        double balance = getBalance(con, acc);

        System.out.print("Withdraw Amount: ");
        double amt = sc.nextDouble();

        if (amt > balance) {
            System.out.println("Insufficient balance");
            return;
        }

        balance -= amt;
        updateBalance(con, acc, balance);
        saveTransaction(con, acc, "WITHDRAW", amt, balance);

        System.out.println("Collect cash");
        System.out.println("Available Balance: ₹" + balance);
    }

    private static void deposit(Connection con, Scanner sc, String acc) throws SQLException {

        double balance = getBalance(con, acc);

        System.out.print("Deposit Amount: ");
        double amt = sc.nextDouble();

        balance += amt;
        updateBalance(con, acc, balance);
        saveTransaction(con, acc, "DEPOSIT", amt, balance);

        System.out.println("Amount deposited");
        System.out.println("Available Balance: ₹" + balance);
    }

    private static void updateBalance(Connection con, String acc, double bal) throws SQLException {
        String sql = "UPDATE users SET balance=? WHERE account_number=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, bal);
            ps.setString(2, acc);
            ps.executeUpdate();
        }
    }

    private static void saveTransaction(
            Connection con, String acc, String type, double amount, double balance
    ) throws SQLException {

        String sql =
                "INSERT INTO transactions(account_number, type, amount, balance_after) VALUES (?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balance);
            ps.executeUpdate();
        }
    }

    private static void showMiniStatement(Connection con, String acc) throws SQLException {

        String sql =
                "SELECT type, amount, balance_after, transaction_time " +
                        "FROM transactions WHERE account_number=? " +
                        "ORDER BY transaction_time DESC LIMIT 5";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- MINI STATEMENT ---");
                System.out.println("TYPE\tAMOUNT\tBALANCE\tDATE");

                while (rs.next()) {
                    System.out.println(
                            rs.getString("type") + "\t" +
                                    rs.getDouble("amount") + "\t" +
                                    rs.getDouble("balance_after") + "\t" +
                                    rs.getTimestamp("transaction_time")
                    );
                }
            }
        }
    }
}