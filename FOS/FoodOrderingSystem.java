import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FoodOrderingSystem {

static Scanner sc = new Scanner(System.in);

public static void main(String[] args) {

    int choice = 0;

    do {
        System.out.println("\n===== FOOD ORDERING SYSTEM =====");
        System.out.println("1. View Food Menu");
        System.out.println("2. Order Food");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            sc.next();
            continue;
        }

        choice = sc.nextInt();

        switch (choice) {

            case 1:
                displayMenu();
                break;

            case 2:
                orderFood();
                break;

            case 3:
                System.out.println("Thank you!");
                break;

            default:
                System.out.println("Invalid choice!");
        }

    } while (choice != 3);

    sc.close();
}

static void displayMenu() {

    String sql = "SELECT food_id, food_name, category, price "
               + "FROM food_items WHERE available = TRUE";

    System.out.println("\n----------- FOOD MENU -----------");

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            System.out.println(
                rs.getInt("food_id") + ". "
                + rs.getString("food_name")
                + " (" + rs.getString("category") + ")"
                + " - Rs. " + rs.getBigDecimal("price")
            );
        }

    } catch (SQLException e) {
        System.out.println("Could not load the food menu.");
        System.out.println(e.getMessage());
    }
}

static void orderFood() {

    displayMenu();

    System.out.print("\nEnter food ID: ");

    if (!sc.hasNextInt()) {
        System.out.println("Please enter a valid food ID.");
        sc.next();
        return;
    }

    int foodId = sc.nextInt();

    System.out.print("Enter quantity: ");

    if (!sc.hasNextInt()) {
        System.out.println("Please enter a valid quantity.");
        sc.next();
        return;
    }

    int quantity = sc.nextInt();

    if (quantity <= 0) {
        System.out.println("Quantity must be greater than zero.");
        return;
    }

    String sql = "SELECT food_name, price FROM food_items "
               + "WHERE food_id = ? AND available = TRUE";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, foodId);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                String foodName = rs.getString("food_name");
                java.math.BigDecimal price = rs.getBigDecimal("price");
                java.math.BigDecimal total =
                    price.multiply(java.math.BigDecimal.valueOf(quantity));

                System.out.println("\n========== YOUR BILL ==========");
                System.out.println("Food     : " + foodName);
                System.out.println("Price    : Rs. " + price);
                System.out.println("Quantity : " + quantity);
                System.out.println("Total    : Rs. " + total);
                System.out.println("===============================");

                System.out.println(
                    "Bill calculated successfully!"
                );

            } else {
                System.out.println("Food item not found or unavailable.");
            }
        }

    } catch (SQLException e) {
        System.out.println("Could not process your order.");
        System.out.println(e.getMessage());
    }
}


}
