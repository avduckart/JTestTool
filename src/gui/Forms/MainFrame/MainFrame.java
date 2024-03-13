package gui.Forms.MainFrame;

import gui.Forms.MainForm;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(String title, JPanel rootPanel) throws HeadlessException {
        super(title);
        setContentPane(rootPanel);
    }

    public static void main(String[] args) {
        MainForm mainForm = new MainForm();
        JFrame frame = new MainFrame("Test", mainForm.getRootPane());
        mainForm.addSearchTestButtonListener(frame);
        mainForm.addExecButtonListener(frame);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
