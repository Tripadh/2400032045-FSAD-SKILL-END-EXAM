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
 * Demonstrates Insert and Update operations on Supplier entity using Hibernate
 */
public class ClientDemo {

    private static SessionFactory sessionFactory;

    // Initialize SessionFactory
    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Supplier.class)
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
        
        printHeader("FSAD HIBERNATE SUPPLIER MANAGEMENT SYSTEM");

        try {
            boolean running = true;
            
            while (running) {
                printSection("MAIN MENU");
                System.out.println("\n  1. View All Suppliers");
                System.out.println("  2. Insert New Supplier");
                System.out.println("  3. Update Supplier Name");
                System.out.println("  4. Update Supplier Status");
                System.out.println("  5. Exit");
                System.out.print("\n  Enter your choice (1-5): ");
                
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        // View All Suppliers
                        printSection("ALL SUPPLIERS IN DATABASE");
                        demo.displayAllSuppliers();
                        break;
                        
                    case "2":
                        // Insert New Supplier
                        printSection("INSERT NEW SUPPLIER");
                        System.out.println("\n🔹 Enter Supplier Details:");
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
                        
                        int supplierId = demo.insertSupplier(name, description, date, status, email, phone, address);
                        System.out.println("\n✓ Supplier inserted successfully with ID: " + supplierId);
                        break;
                        
                    case "3":
                        // Update Supplier Name
                        printSection("UPDATE SUPPLIER NAME");
                        System.out.print("\nEnter Supplier ID: ");
                        int updateId1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter New Name: ");
                        String newName = scanner.nextLine();
                        demo.updateSupplierName(updateId1, newName);
                        System.out.println("\n✓ Supplier Name updated successfully!");
                        break;
                        
                    case "4":
                        // Update Supplier Status
                        printSection("UPDATE SUPPLIER STATUS");
                        System.out.print("\nEnter Supplier ID: ");
                        int updateId2 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter New Status (Active/Pending/Inactive): ");
                        String newStatus = scanner.nextLine();
                        demo.updateSupplierStatus(updateId2, newStatus);
                        System.out.println("\n✓ Supplier Status updated successfully!");
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
     * Insert a new Supplier record into the database
     * @return the generated supplier ID
     */
    public int insertSupplier(String name, String description, Date date, 
                               String status, String email, String phone, String address) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        int supplierId = 0;

        try {
            transaction = session.beginTransaction();

            // Create a new Supplier object
            Supplier supplier = new Supplier(name, description, date, status, email, phone, address);

            // Save the supplier (ID will be auto-generated)
            supplierId = (int) session.save(supplier);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error inserting supplier: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }

        return supplierId;
    }

    /**
     * Update Supplier Name based on ID
     */
    public void updateSupplierName(int supplierId, String newName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Retrieve the supplier by ID
            Supplier supplier = session.get(Supplier.class, supplierId);

            if (supplier != null) {
                String oldName = supplier.getName();
                // Update the name
                supplier.setName(newName);
                session.update(supplier);
                System.out.println("  Old Name: " + oldName);
                System.out.println("  New Name: " + newName);
            } else {
                System.out.println("  ✗ Supplier not found with ID: " + supplierId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating supplier name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Update Supplier Status based on ID
     */
    public void updateSupplierStatus(int supplierId, String newStatus) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Retrieve the supplier by ID
            Supplier supplier = session.get(Supplier.class, supplierId);

            if (supplier != null) {
                String oldStatus = supplier.getStatus();
                // Update the status
                supplier.setStatus(newStatus);
                session.update(supplier);
                System.out.println("  Old Status: " + oldStatus);
                System.out.println("  New Status: " + newStatus);
            } else {
                System.out.println("  ✗ Supplier not found with ID: " + supplierId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating supplier status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Display all Suppliers from the database
     */
    public void displayAllSuppliers() {
        Session session = sessionFactory.openSession();

        try {
            @SuppressWarnings("unchecked")
            java.util.List<Supplier> suppliers = session.createQuery("FROM Supplier").list();

            if (suppliers.isEmpty()) {
                System.out.println("\n  No suppliers found in database.");
            } else {
                System.out.println("\n  Total Suppliers: " + suppliers.size());
                System.out.println("\n" + " ".repeat(2) + String.format("%-5s %-25s %-20s %-15s", "ID", "NAME", "EMAIL", "STATUS"));
                System.out.println(" ".repeat(2) + "-".repeat(65));
                
                for (Supplier supplier : suppliers) {
                    System.out.println(" ".repeat(2) + String.format("%-5d %-25s %-20s %-15s", 
                        supplier.getId(), 
                        truncate(supplier.getName(), 25),
                        truncate(supplier.getEmail(), 20),
                        supplier.getStatus()));
                }
                
                System.out.println("\n  Detailed Information:");
                System.out.println(" ".repeat(2) + "-".repeat(65));
                for (int i = 0; i < suppliers.size(); i++) {
                    Supplier s = suppliers.get(i);
                    System.out.println("\n  [" + (i + 1) + "] Supplier Details:");
                    System.out.println("      ID          : " + s.getId());
                    System.out.println("      Name        : " + s.getName());
                    System.out.println("      Description : " + s.getDescription());
                    System.out.println("      Email       : " + s.getEmail());
                    System.out.println("      Phone       : " + s.getPhone());
                    System.out.println("      Address     : " + s.getAddress());
                    System.out.println("      Status      : " + s.getStatus());
                    System.out.println("      Date        : " + s.getDate());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
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
