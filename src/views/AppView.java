package views;

import enums.Menu;
import models.App;

import java.util.Scanner;

public class AppView {
    public static void run() {
        Scanner scanner = new Scanner(System.in);
        if (App.getCurrentMenu() == Menu.SIGNUP_MENU) {
            SignupMenu.check(scanner);
        }
    }
}
