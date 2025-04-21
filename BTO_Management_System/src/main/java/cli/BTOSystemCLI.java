package cli;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BTOSystemCLI {
    private static Scanner scanner = new Scanner(System.in);
    private static List<User> users = new ArrayList<>();
    private static List<Project> projects = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the BTO Management System (CLI)");
        loginMenu();
    }

    private static void loginMenu() {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();
        System.out.print("Enter password: ");
        String pwd = scanner.nextLine();

        Optional<User> userOpt = users.stream()
                .filter(u -> u.getNric().equalsIgnoreCase(nric) && u.checkPassword(pwd))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user instanceof Manager) {
                System.out.println("Logged in as HDB Manager");
            } else if (user instanceof Officer) {
                System.out.println("Logged in as HDB Officer");
            } else {
                System.out.println("Logged in as Applicant");
            }
        } else {
            System.out.println("Invalid credentials.");
        }
    }
}
