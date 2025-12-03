
package main;

import view.LoginView;

public class Main {
    public static void main(String[] args) {
        try { javax.swing.UIManager.setLookAndFeel(
            javax.swing.UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        new LoginView().setVisible(true);
    }
}
