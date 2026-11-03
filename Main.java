import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            showMenu();
            int choice = readInteger("Choose an option: ");

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewAllBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    deleteBook();
                    break;
                case 5:
                    issueBook();
                    break;
                case 6:
                    returnBook();
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Please choose a number from 0 to 6.");
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n=== Library Management System ===");
        System.out.println("1. Add a book");
        System.out.println("2. View all books");
        System.out.println("3. Search for a book by ID");
        System.out.println("4. Delete a book");
        System.out.println("5. Issue a book");
        System.out.println("6. Return a book");
        System.out.println("0. Exit");
    }

    private static void addBook() {
        int id = readPositiveInteger("Book ID: ");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()) {
            System.out.println("Title and author cannot be empty.");
            return;
        }

        String sql = "INSERT INTO books (id, title, author) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.out.println("Could not add the book: " + e.getMessage());
        }
    }

    private static void viewAllBooks() {
        String sql = "SELECT id, title, author, issued FROM books ORDER BY id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            if (!results.isBeforeFirst()) {
                System.out.println("No books found.");
                return;
            }

            System.out.println("\nID | Title | Author | Status");
            System.out.println("----------------------------------------");
            while (results.next()) {
                printBook(readBook(results));
            }
        } catch (SQLException e) {
            System.out.println("Could not view books: " + e.getMessage());
        }
    }

    private static void searchBook() {
        int id = readPositiveInteger("Enter the book ID: ");
        String sql = "SELECT id, title, author, issued FROM books WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    printBook(readBook(results));
                } else {
                    System.out.println("Book not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not search for the book: " + e.getMessage());
        }
    }

    private static void deleteBook() {
        int id = readPositiveInteger("Enter the book ID to delete: ");
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Could not delete the book: " + e.getMessage());
        }
    }

    private static void issueBook() {
        updateBookStatus("issue");
    }

    private static void returnBook() {
        updateBookStatus("return");
    }

    private static void updateBookStatus(String action) {
        int id = readPositiveInteger("Enter the book ID to " + action + ": ");
        boolean issuing = action.equals("issue");
        String sql = "UPDATE books SET issued = ? WHERE id = ? AND issued = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, issuing);
            statement.setInt(2, id);
            statement.setBoolean(3, !issuing);

            if (statement.executeUpdate() > 0) {
                System.out.println(issuing
                        ? "Book issued successfully."
                        : "Book returned successfully.");
            } else {
                System.out.println("Book not found or it is already in that status.");
            }
        } catch (SQLException e) {
            System.out.println("Could not " + action + " the book: " + e.getMessage());
        }
    }

    private static Book readBook(ResultSet results) throws SQLException {
        return new Book(
                results.getInt("id"),
                results.getString("title"),
                results.getString("author"),
                results.getBoolean("issued"));
    }

    private static void printBook(Book book) {
        String status = book.isIssued() ? "Issued" : "Available";
        System.out.println(book.getId() + " | " + book.getTitle()
                + " | " + book.getAuthor() + " | " + status);
    }

    private static int readPositiveInteger(String prompt) {
        while (true) {
            int value = readInteger(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Please enter a number greater than zero.");
        }
    }

    private static int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
