package com.klef.fsad.exam;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * ClientDemo Class
 * Demonstrates insert and update operations on Inventory entity using Hibernate
 */
public class ClientDemo {

    private static SessionFactory sessionFactory;

    // Initialize SessionFactory
    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Inventory.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            System.err.println("SessionFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) {
        ClientDemo demo = new ClientDemo();
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        printHeader("FSAD HIBERNATE INVENTORY MANAGEMENT SYSTEM");

        try {
            boolean running = true;
            
            while (running) {
                printSection("MAIN MENU");
                System.out.println("\n  1. View All Inventory Items");
                System.out.println("  2. Insert New Inventory Item");
                System.out.println("  3. Update Inventory Name");
                System.out.println("  4. Update Inventory Status");
                System.out.println("  5. Exit");
                System.out.print("\n  Enter your choice (1-5): ");
                
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        // View All Inventory Items
                        printSection("ALL INVENTORY ITEMS IN DATABASE");
                        demo.displayAllInventory();
                        break;
                        
                    case "2":
                        // Insert New Inventory Item
                        printSection("INSERT NEW INVENTORY ITEM");
                        System.out.println("\n🔹 Enter Inventory Details:");
                        System.out.print("  Name: ");
                        String name = scanner.nextLine();
                        
                        System.out.print("  Description: ");
                        String description = scanner.nextLine();
                        
                        System.out.print("  Date (DD/MM/YYYY) [Press Enter for today's date]: ");
                        String dateInput = scanner.nextLine();
                        Date date = dateInput.trim().isEmpty() ? new Date() : dateFormat.parse(dateInput);
                        
                        System.out.print("  Status (Active/Pending/Inactive): ");
                        String status = scanner.nextLine();
                        
                        System.out.print("  Email: ");
                        String email = scanner.nextLine();
                        
                        System.out.print("  Phone: ");
                        String phone = scanner.nextLine();
                        
                        System.out.print("  Address: ");
                        String address = scanner.nextLine();
                        
                        int inventoryId = demo.insertInventory(name, description, date, status, email, phone, address);
                        System.out.println("\n✓ Inventory item inserted successfully with ID: " + inventoryId);
                        break;
                        
                    case "3":
                        // Update Inventory Name
                        printSection("UPDATE INVENTORY NAME");
                        System.out.print("\nEnter Inventory ID: ");
                        int updateId1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter New Name: ");
                        String newName = scanner.nextLine();
                        demo.updateInventoryName(updateId1, newName);
                        System.out.println("\n✓ Inventory name updated successfully!");
                        break;
                        
                    case "4":
                        // Update Inventory Status
                        printSection("UPDATE INVENTORY STATUS");
                        System.out.print("\nEnter Inventory ID: ");
                        int updateId2 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter New Status (Active/Pending/Inactive): ");
                        String newStatus = scanner.nextLine();
                        demo.updateInventoryStatus(updateId2, newStatus);
                        System.out.println("\n✓ Inventory status updated successfully!");
                        break;
                        
                    case "5":
                        // Exit
                        System.out.println("\n✓ Exiting system...");
                        running = false;
                        break;
                        
                    default:
                        System.out.println("\n✗ Invalid choice! Please enter 1-5.");
                        break;
                }
                
                if (running) {
                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                }
            }

        } catch (ParseException e) {
            System.err.println("Error parsing date. Please use DD/MM/YYYY format.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error: Please enter a valid number for ID.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            // Close the SessionFactory
            printSeparator();
            closeSessionFactory();
            printFooter();
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" ".repeat((70 - title.length()) / 2) + title);
        System.out.println("=".repeat(70) + "\n");
    }

    private static void printSection(String section) {
        System.out.println("\n" + "-".repeat(70));
        System.out.println("  " + section);
        System.out.println("-".repeat(70));
    }

    private static void printSeparator() {
        System.out.println("\n" + "=".repeat(70));
    }

    private static void printFooter() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" ".repeat(25) + "OPERATIONS COMPLETED");
        System.out.println("=".repeat(70) + "\n");
    }

    /**
     * Insert a new inventory record into the database
     * @return the generated inventory ID
     */
    public int insertInventory(String name, String description, Date date,
                               String status, String email, String phone, String address) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        int inventoryId = 0;

        try {
            transaction = session.beginTransaction();

            Inventory inventory = new Inventory(name, description, date, status, email, phone, address);

            // Save the inventory item (ID will be auto-generated)
            inventoryId = (int) session.save(inventory);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error inserting inventory item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }

        return inventoryId;
    }

    /**
    * Update inventory name based on ID
     */
    public void updateInventoryName(int inventoryId, String newName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Retrieve inventory by ID
            Inventory inventory = session.get(Inventory.class, inventoryId);

            if (inventory != null) {
                String oldName = inventory.getName();
                // Update the name
                inventory.setName(newName);
                session.update(inventory);
                System.out.println("  Old Name: " + oldName);
                System.out.println("  New Name: " + newName);
            } else {
                System.out.println("  ✗ Inventory item not found with ID: " + inventoryId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating inventory name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
    * Update inventory status based on ID
     */
    public void updateInventoryStatus(int inventoryId, String newStatus) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Retrieve inventory by ID
            Inventory inventory = session.get(Inventory.class, inventoryId);

            if (inventory != null) {
                String oldStatus = inventory.getStatus();
                // Update the status
                inventory.setStatus(newStatus);
                session.update(inventory);
                System.out.println("  Old Status: " + oldStatus);
                System.out.println("  New Status: " + newStatus);
            } else {
                System.out.println("  ✗ Inventory item not found with ID: " + inventoryId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating inventory status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Display all inventory items from the database
     */
    public void displayAllInventory() {
        Session session = sessionFactory.openSession();

        try {
            @SuppressWarnings("unchecked")
            java.util.List<Inventory> inventoryItems = session.createQuery("FROM Inventory").list();

            if (inventoryItems.isEmpty()) {
                System.out.println("\n  No inventory items found in database.");
            } else {
                System.out.println("\n  Total Inventory Items: " + inventoryItems.size());
                System.out.println("\n" + " ".repeat(2) + String.format("%-5s %-25s %-20s %-15s", "ID", "NAME", "EMAIL", "STATUS"));
                System.out.println(" ".repeat(2) + "-".repeat(65));
                
                for (Inventory inventory : inventoryItems) {
                    System.out.println(" ".repeat(2) + String.format("%-5d %-25s %-20s %-15s", 
                        inventory.getId(),
                        truncate(inventory.getName(), 25),
                        truncate(inventory.getEmail(), 20),
                        inventory.getStatus()));
                }
                
                System.out.println("\n  Detailed Information:");
                System.out.println(" ".repeat(2) + "-".repeat(65));
                for (int i = 0; i < inventoryItems.size(); i++) {
                    Inventory item = inventoryItems.get(i);
                    System.out.println("\n  [" + (i + 1) + "] Inventory Details:");
                    System.out.println("      ID          : " + item.getId());
                    System.out.println("      Name        : " + item.getName());
                    System.out.println("      Description : " + item.getDescription());
                    System.out.println("      Email       : " + item.getEmail());
                    System.out.println("      Phone       : " + item.getPhone());
                    System.out.println("      Address     : " + item.getAddress());
                    System.out.println("      Status      : " + item.getStatus());
                    System.out.println("      Date        : " + item.getDate());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching inventory items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Truncate string to specified length
     */
    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    /**
     * Close the SessionFactory
     */
    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("\n✓ SessionFactory closed successfully.");
        }
    }
}
