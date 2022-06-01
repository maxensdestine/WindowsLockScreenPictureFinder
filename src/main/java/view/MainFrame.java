package view;

import controller.ConfigurationsController;
import controller.ErrorLogController;
import controller.FileController;
import model.Configurations;
import net.miginfocom.swing.MigLayout;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainFrame extends JFrame {

    private final JPanel root = new JPanel();
    private final JPanel pnlConfig = new JPanel();
    private final JButton btnCopy = new JButton("Retrieve All");
    private final JLabel lblDestination = new JLabel("Output Directory");
    private final JFileChooser fcDestination = new JFileChooser();
    private final JButton btnBrowseFolder = new JButton();
    private final JTextField txtDestination = new JTextField(ConfigurationsController.getTargetDir());
    private final JLabel lblNamingScheme = new JLabel("Naming Scheme", SwingConstants.LEFT);
    private final JTextField txtNamingScheme = new JTextField();
    private final JLabel lblMinFileSize = new JLabel("Minimal Size (kB)", SwingConstants.LEFT);
    private final JTextField txtMinFileSize = new JTextField();
    private final JRadioButton optSplitLandscape = new JRadioButton("Split landscape and portrait images");
    private final JMenuBar menuBar = new JMenuBar();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JScrollPane errorScrollPane = new JScrollPane();
    private final JPanel errorPanel = new JPanel();
    private final GridLayout errorGridLayout = new GridLayout(1, 1);
    private final JProgressBar progressBar = new JProgressBar();
    private final JPanel pnlSouth = new JPanel();

    private static final Color COLOR_WRONG_INPUT = new Color(245, 122, 122);

    public MainFrame() {
        this.setVisible(false);
        this.setSize(new Dimension(464, 350));
        this.setTitle("Lock Screen Picture Retriever");
        this.setMinimumSize(new Dimension(334, 350));
        try (InputStream in = getClass().getResourceAsStream("/icon.png")) {
            BufferedImage image = ImageIO.read(in);
            this.setIconImage(image);
        } catch (IOException e) {
            ErrorLogController.addError(e.getMessage());
        }

        this.setJMenuBar(menuBar);
        prepareErrorPanel();
        prepareTabbedPane();
        prepareMenu();
        centreWindow(this);
        prepareRootPanel();
        fillTextFieldsAndOpt();
        this.add(tabbedPane);
        addCloseListener();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    private void prepareTabbedPane() {
        tabbedPane.add("Main", root);
        tabbedPane.add("Error Log", errorScrollPane);
    }

    private void prepareMenu() {
        JMenu menuHelp = new JMenu("Help");
        JMenu menuAbout = new JMenu("About");
        JMenuItem all = new JMenuItem("Info on all");
        JMenuItem namingScheme = new JMenuItem("Naming Scheme");
        JMenuItem minSize = new JMenuItem("Minimal Size");
        JMenuItem destination = new JMenuItem("Output Directory");
        JMenuItem splitLandscape = new JMenuItem("Split landscape from portrait");
        JMenuItem author = new JMenuItem("Author");
        JMenuItem technology = new JMenuItem("Technology");

        String msgNamingScheme = "The naming scheme determines the names to be given to the output files. " +
                "If your naming scheme is \"apple\", the files will be called apple1, apple2, etc. It is case sensitive.<br/> <br/>" +
                "Indexing always starts from the last index present in the output directory(for example if there is a file" +
                "called \"apple50\" in the output directory, the next file will be called \"apple51\").";
        String msgMinSize = "Mixed with the background pictures are other type of pictures. For example mobile ad pictures. " +
                "To minimize the chances of copying those files, we use a minimal file size of 400kB since the undesired pictures are often smaller than" +
                "the desired one.<br/> <br/> This is not a perfect solution so you can decrease it if you are not finding the picture you are looking for. " +
                "You can also increase it if undesired pictures are getting copied.";
        String msgDestination = "This is simply the directory where you would like your pictures to be copied.";
        String msgSplit = "Check the box if you want the app to split landscape and portrait pictures.";

        all.addActionListener((actionEvent) -> {
            String title = "Info on all fields";
            String message = "Naming Scheme<br/>" + msgNamingScheme + "<br/><br/>" +
                    "Minimal File Size<br/>" + msgMinSize + "<br/><br/>" +
                    "Output Directory<br/>" + msgDestination + "<br/><br/>" +
                    "Split Landscape and Portrait<br/>" + msgSplit + "<br/><br/>";
            infoBox(title, message, 400);
        });

        namingScheme.addActionListener((actionEvent) -> infoBox("Info on the Naming Scheme", msgNamingScheme));
        minSize.addActionListener((actionEvent) -> infoBox("Info on the Minimal Size", msgMinSize));
        destination.addActionListener((actionEvent) -> infoBox("Info on the Output Directory", msgDestination));
        splitLandscape.addActionListener((actionEvent) -> infoBox("Info on Split Landscape From Portrait", msgSplit));


        author.addActionListener((actionEvent) -> infoBox("About", "This app was developed by Maxens Destiné. Other projects can be found at " +
                "https://github.com/maxensdestine ."));

        technology.addActionListener((actionEvent) -> infoBox("Technology Used", "The icons used for this application and for the browse button can be " +
                "found at https://icons8.com/ ."));

        menuHelp.add(all);
        menuHelp.add(namingScheme);
        menuHelp.add(minSize);
        menuHelp.add(destination);
        menuHelp.add(splitLandscape);

        menuAbout.add(author);
        menuAbout.add(technology);

        menuBar.add(menuHelp);
        menuBar.add(menuAbout);
    }

    public static void infoBox(String titleBar, String infoMessage) {
        infoBox(titleBar, infoMessage, 200);
    }

    public static void infoBox(String titleBar, String infoMessage, int width) {
        JOptionPane pane = new JOptionPane();
        pane.setSize(new Dimension(200, 200));
        pane.setMaximumSize(new Dimension(464, 400));
        JOptionPane.showMessageDialog(null, "<html><body><p align='justify' style='width: " + width + "px;'>" + infoMessage + "</p></body></html>",
                titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    private void prepareRootPanel() {
        prepareConfigPanel();
        prepareSouthPanel();
        root.setLayout(new BorderLayout());
        root.add(pnlConfig, BorderLayout.CENTER);
        root.add(pnlSouth, BorderLayout.SOUTH);

    }

    private void prepareSouthPanel() {
        prepareBtnCopy();
        prepareProgressBar();
        pnlSouth.setLayout(new BorderLayout());
        pnlSouth.add(btnCopy, BorderLayout.SOUTH);
        pnlSouth.add(progressBar, BorderLayout.NORTH);
    }

    private void prepareProgressBar() {
        progressBar.setPreferredSize(new Dimension(464, 10));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
    }

    private void prepareBtnCopy() {
        btnCopy.setPreferredSize(new Dimension(464, 50));
        btnCopy.addActionListener((actionEvent) -> {
            progressBar.setVisible(true);
            btnCopy.setText("Wait...");
            Thread retrieveThread = new Thread(() -> {
                copyPasteAll();
                progressBar.setVisible(false);
                btnCopy.setText("Retrieve All");
                progressBar.setVisible(false);
                if(ErrorLogController.isHasError()){
                    btnCopy.setText("Failed, click to retrieve again");
                    ErrorLogController.setHasError(false);
                } else {
                    btnCopy.setText("Success, click to retrieve again");
                }
            });
            retrieveThread.start();

        });
    }

    private void prepareConfigPanel() {
        prepareFileChooser();
        prepareTextFields();
        pnlConfig.setPreferredSize(new Dimension(464, 260));
        pnlConfig.setLayout(new MigLayout("", "[][grow]", "[][][][][][]"));

        pnlConfig.add(lblNamingScheme, "width 108::108, height 25:25:25");
        pnlConfig.add(txtNamingScheme, "width 96:300:, height 25:25:25, wrap, gapleft push");

        pnlConfig.add(new JPanel(), "wrap");

        pnlConfig.add(lblMinFileSize, "width 108::108, height 25:25:25");
        pnlConfig.add(txtMinFileSize, "width 96:300:, height 25:25:25, gapleft push, wrap");

        pnlConfig.add(new JPanel(), "wrap");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 0));
        panel.add(btnBrowseFolder, BorderLayout.EAST);
        panel.add(txtDestination, BorderLayout.CENTER);

        pnlConfig.add(lblDestination, "width 108::108, height 30:30:30");
        pnlConfig.add(panel, "width 96:300:, height 25:25:25, wrap, gapleft push");

        pnlConfig.add(new JPanel(), "wrap");

        optSplitLandscape.setHorizontalTextPosition(SwingConstants.LEADING);
        pnlConfig.add(optSplitLandscape, " height 30:30:30, wrap, span");

        pnlConfig.setBorder(BorderFactory.createMatteBorder(
                5, 5, 5, 5, (Color) null));

    }

    private void prepareTextFields() {
        txtMinFileSize.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMinFileSize.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkMinSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkMinSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        txtNamingScheme.setHorizontalAlignment(SwingConstants.RIGHT);
        txtNamingScheme.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkNamingScheme();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkNamingScheme();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        txtDestination.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDestination.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkDestination();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkDestination();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private boolean checkNamingScheme() {
        boolean goodInput = true;
        try {
            Paths.get(txtNamingScheme.getText());
        } catch (Exception e) {
            goodInput = false;
        }

        if (goodInput) {
            txtNamingScheme.setBackground(Color.white);
            return true;
        } else {
            txtNamingScheme.setBackground(COLOR_WRONG_INPUT);
            return false;
        }
    }

    private boolean checkMinSize() {
        boolean goodInput;
        try {
            long nb = Long.parseLong(txtMinFileSize.getText());
            goodInput = nb >= 0;
        } catch (NumberFormatException e) {
            goodInput = false;
        }

        if (goodInput) {
            txtMinFileSize.setBackground(Color.white);
            return true;
        } else {
            txtMinFileSize.setBackground(COLOR_WRONG_INPUT);
            return false;
        }
    }

    private void prepareFileChooser() {
        try (InputStream in = getClass().getResourceAsStream("/browseFolderIcon.png")) {
            ImageIcon image = new ImageIcon(ImageIO.read(in));
            btnBrowseFolder.setIcon(image);
        } catch (IOException e) {
            ErrorLogController.addError(e.getMessage());
        }
        btnBrowseFolder.setPreferredSize(new Dimension(32, 32));

        btnBrowseFolder.addActionListener((actionEvent) -> {
            int returnVal = fcDestination.showOpenDialog(root);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                txtDestination.setText(fcDestination.getSelectedFile().getAbsolutePath());
            }
            checkDestination();
        });

        fcDestination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fcDestination.setMultiSelectionEnabled(false);
    }


    private boolean checkDestination() {
        boolean exists = false;
        try {
            if (Files.exists(Paths.get(txtDestination.getText()))) {
                exists = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exists) {
            txtDestination.setBackground(Color.white);
            return true;
        } else {
            txtDestination.setBackground(COLOR_WRONG_INPUT);
            return false;
        }
    }

    private void fillTextFieldsAndOpt() {
        txtNamingScheme.setText(ConfigurationsController.getNamingScheme());
        txtMinFileSize.setText(ConfigurationsController.getMinImageSize() / 1000 + "");
        txtDestination.setText(ConfigurationsController.getTargetDir());
        optSplitLandscape.setSelected(ConfigurationsController.getSplitLandscape());
    }

    private void saveConfig() {
        if (checkNamingScheme()) {
            ConfigurationsController.changeNamingScheme(txtNamingScheme.getText());
        }
        if (checkMinSize()) {
            long val = 0;
            try {
                val = Long.parseLong(txtMinFileSize.getText());
            } catch (NumberFormatException e) {
                ErrorLogController.addError(e.getMessage());
            }
            ConfigurationsController.changeMinImageSize(val);
        }

        if (checkDestination()) {
            ConfigurationsController.changeTargetDir(txtDestination.getText());
        }

        ConfigurationsController.changeSplitLandscape(optSplitLandscape.isSelected());
    }

    private void addCloseListener() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveConfig();
                ConfigurationsController.storeLocationInformation(Configurations.getSaveDir());
            }
        });
    }

    /**
     * This function was taken from https://stackoverflow.com/questions/144892/how-to-center-a-window-in-java
     *
     * @author Dónal
     */
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    private void prepareErrorPanel() {
        errorPanel.setLayout(errorGridLayout);
        errorScrollPane.getVerticalScrollBar().setBackground(Color.LIGHT_GRAY);
        errorScrollPane.setViewportView(errorPanel);
    }

    public void addError(String message) {
        errorPanel.add(getErrorPanel(message));
        System.out.println("YEP COCK");
    }

    private JPanel getErrorPanel(String message) {
        MigLayout migLayout = new MigLayout("", "[grow][32]", "");
        JPanel panel = new JPanel(migLayout);
        JTextPane pane = new JTextPane();
        pane.setContentType("text/html"); // let the text pane know this is what you want
        pane.setEditable(false); // as before
        pane.setBackground(null); // this is the same as a JLabel
        pane.setBorder(null); // remove the border
        pane.setText(message);
        JButton btnDelete = new JButton("X");
        btnDelete.setHorizontalAlignment(SwingConstants.CENTER);
        btnDelete.setVerticalAlignment(SwingConstants.CENTER);

        btnDelete.addActionListener((actionEvent) -> {
            errorPanel.remove(panel);
            errorGridLayout.setRows(errorGridLayout.getRows() - 1);
            this.revalidate();
            this.repaint();
        });
        errorGridLayout.setRows(errorGridLayout.getRows() + 1);
        panel.add(pane);
        panel.add(btnDelete);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));

        return panel;
    }

    private void copyPasteAll() {
        if (checkNamingScheme() && checkDestination() && checkMinSize()) {
            saveConfig();
            FileController.copyPasteAllFiles();
        }
    }
}
