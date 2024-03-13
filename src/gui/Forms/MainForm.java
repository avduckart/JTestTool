package gui.Forms;

import Model.Loader;
import Model.Logger;
import org.bouncycastle.crypto.CryptoException;

import javax.smartcardio.CardException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainForm {
    private JTextField logPathField;
    private JTextField scriptPathField;
    private JButton searchLogButton;
    private JButton searchTestButton;
    private JButton execButton;
    private JLabel scriptLabel;
    private JLabel logLabel;
    private JScrollPane scriptPane;
    private JScrollPane logPane;
    private JTextPane scriptTextPane;
    private JTextPane logTextPane;
    private JPanel rootPane;
    private File script;

    public MainForm() {

    }

    public JPanel getRootPane() {
        return rootPane;
    }

    public void addExecButtonListener(JFrame frame){
        execButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File log = new File(String.format("%s %s",
                        new SimpleDateFormat("MM-dd-yyyy hh-mm-ss").format(new Date()),
                        script.getPath().substring(script.getPath().lastIndexOf('\\') + 1)));
                Logger logger = null;
                try {
                    logger = new Logger(log);
                    Loader.scriptExecute(script.getPath(), logger);
                    String text = FileUtils.read(log);
                    logTextPane.setText(text);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                catch (CardException e1) {
                    e1.printStackTrace();
                } catch (CryptoException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void addSearchTestButtonListener(JFrame frame){
        searchTestButton.addActionListener(e -> {
            JFileChooser dialog = new JFileChooser();
            if (dialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                script = dialog.getSelectedFile();
                String text = FileUtils.read(script);
                scriptTextPane.setText(text);
            }
        });
    }

    static class FileUtils {

        public static String read(File file) {
            StringBuilder sb = new StringBuilder();
            if (!file.exists())
                throw new RuntimeException("File not found!");
            try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            return sb.toString();
        }
    }
}
