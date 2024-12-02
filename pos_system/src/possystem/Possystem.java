package possystem;

import javax.swing.*;

public class Possystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
