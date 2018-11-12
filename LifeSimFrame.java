import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
 
class LifeSimFrame extends JFrame
{
    /* Constants */
 
    public static final String PLAY_PAUSE = "play/pause";
    public static final String STEP = "step";
    public static final String GENERATE_TERRAIN = "generate terrain";
    public static final String GENERATE_ORGANISMS = "generate organisms";
    public static final String RESET_TERRAIN = "reset terrain";
    public static final String RESET_ORGANISMS = "reset organisms";
    public static final String RESET_GROWTH = "reset growth";
    public static final String SAVE_FILE = "save file";
    public static final String LOAD_FILE = "load file";
 
    public static final double STARTING_PLAIN_VALUE = 0.9;
    public static final double STARTING_DESERT_VALUE = 0.1;
    public static final double STARTING_MOUNTAIN_VALUE = 0.1;
    public static final double STARTING_DENSITY_VALUE = 0.25;
    public static final double STARTING_GROWTH_VALUE = 0.25;
    public static final double STARTING_RABBIT_VALUE = 1.0;
    public static final double STARTING_SNAKE_VALUE = 0.1;
 
    public static final String ORGANISMS = "organisms";
    public static final String FEATURES = "features";
 
    public static final int CARROT = 0;
    public static final int RABBIT = 1;
    public static final int SNAKE = 2;
    public static final int MOUNTAIN = 2;
 
    /* Instance variables */
 
    private Grid grid;
    private GridPanel gridPanel;
    private JButton playPauseButton;
    private JSlider speedSlider, plainSlider, desertSlider, mountainSlider, densitySlider, rabbitSlider, snakeSlider, growthSlider;
    private JComboBox<String> populateComboBox, landscapeComboBox;
    private JRadioButton noneButton, eradicateButton, populateButton, landscapeButton;
    private JLabel plainsLabel, desertsLabel, mountainsLabel, carrotsLabel, rabbitsLabel, snakesLabel, animalsLabel, organismsLabel;
 
    private JPanel specificationPanel;
    private CardLayout specificationPanelManager;
 
    private Timer timer;
    private JFileChooser chooser;
 
    /* Inner classes */
 
    // Panel on which to draw the grid
    class GridPanel extends JPanel
    {
        /* Constructors */
 
        // Given dimensions
        public GridPanel(int width, int height)
        {
            // Set preferred size and minimum size
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
        }
 
        // Default
        public GridPanel()
        {
            // Default size to 640px by 640px
            this(640, 640);
        }
 
        /* Methods */
 
        // Override the paintComponent method
        public void paintComponent(Graphics g)
        {
            // Show the colony
            grid.show(g);
        }
    }
 
    // ActionListener for buttons
    class ButtonListener implements ActionListener
    {
        // Implement actionPerformed
        public void actionPerformed(ActionEvent e)
        {
            // Variable declarations
            String actionCommand;
 
            // Get actionCommand
            actionCommand = e.getActionCommand();
 
            if (actionCommand.equals(PLAY_PAUSE)) {
                // Play/pause the simulation
 
                if (timer.isRunning()) {
                    // Simulation is currently playing
 
                    // Pause simulation
                    pause();
                }
                else {
                    // Simulation is not playing
 
                    // Play simulation
                    play();
                }
            }
            else if (actionCommand.equals(STEP)) {
                // Advance the grid step by step
 
                if (timer.isRunning()) {
                    // Simulation is currently playing
 
                    // Pause simulation
                    pause();
                }
 
                // Advance grid to next turn
                nextTurn();
            }
            else if (actionCommand.equals(GENERATE_TERRAIN)) {
                // Generate new terrain based on sliders
 
                if (timer.isRunning()) {
                    // Simulation is currently playing
 
                    // Pause simulation
                    pause();
                }
 
                // Variable declarations
                double plainValue, desertValue, mountainDensity;
 
                // Calculate plainValue, desertValue, mountainDensity
                plainValue = plainSlider.getValue() * 0.01;
                desertValue = desertSlider.getValue() * 0.01;
                mountainDensity = mountainSlider.getValue() * 0.01;
 
                // Create new grid
                grid = new Grid(plainValue, desertValue, mountainDensity);
 
                // Repaint gridPanel
                update();
            }
            else if (actionCommand.equals(GENERATE_ORGANISMS)) {
                // Generate organisms on grid
 
                if (timer.isRunning()) {
                    // Simulation is currently playing
 
                    // Pause simulation
                    pause();
                }
 
                // Variable declarations
                double density, growth, rabbitValue, snakeValue;
 
                // Calculate values
                density = densitySlider.getValue() * 0.01;
                growth = growthSlider.getValue() * 0.01;
                rabbitValue = rabbitSlider.getValue() * 0.01;
                snakeValue = snakeSlider.getValue() * 0.01;
 
                // Generate organisms
                grid.generateOrganisms(density, growth, rabbitValue, snakeValue);
 
                // Repaint gridPanel
                update();
            }
            else if (actionCommand.equals(RESET_TERRAIN)) {
                // Reset the terrain generation configuration
 
                // Set sliders
                plainSlider.setValue((int) (STARTING_PLAIN_VALUE * 100));
                desertSlider.setValue((int) (STARTING_DESERT_VALUE * 100));
                mountainSlider.setValue((int) (STARTING_MOUNTAIN_VALUE * 100));
 
            }
            else if (actionCommand.equals(RESET_ORGANISMS)) {
                // Resets parameters for organisms generation
 
                // Reset sliders
                densitySlider.setValue((int) (STARTING_DENSITY_VALUE * 100));
                rabbitSlider.setValue((int) (STARTING_RABBIT_VALUE * 100));
                snakeSlider.setValue((int) (STARTING_SNAKE_VALUE * 100));
            }
            else if (actionCommand.equals(RESET_GROWTH)) {
                // Resets growth parameter
 
                // Reset slider
                growthSlider.setValue((int) (STARTING_GROWTH_VALUE * 100));
            }
            else if (actionCommand.equals(SAVE_FILE)) {
                // Save the grid to a file
 
                // Variable declarations
                int returnVal;
                boolean interrupted;
                String pathname, extension;
                File destination;
                FileOutputStream fileOut;
                ObjectOutputStream objOut;
 
                if (timer.isRunning()) {
                    // Simulation is currently running
 
                    // Update interrupted
                    interrupted = true;
 
                    // Pause simulation
                    pause();
                }
                else {
                    // Simulation is not running
                    interrupted = false;
                }
 
                // Show dialogue to allow user to choose file
                returnVal = chooser.showSaveDialog(LifeSimFrame.this);
 
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // A file is chosen
 
                    // Get chosen file
                    destination = chooser.getSelectedFile();
 
                    // Get extension of chosen file
                    pathname = destination.getPath();
                    extension = pathname.substring(pathname.length() - 3);
 
                    if (!extension.equals("sim")) {
                        // The user did not use the proper ".sim" extension
 
                        // Append ".sim" extension to destination
                        destination = new File(pathname + ".sim");
                    }
 
                    try {
                        // Initialize fileOut and objOut
                        fileOut = new FileOutputStream(destination);
                        objOut = new ObjectOutputStream(fileOut);
 
                        // Serialize grid to selected file
                        objOut.writeObject(grid);
 
                        // Close streams
                        objOut.close();
                        fileOut.close();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
 
                if (interrupted) {
                    // Simulation was interrupted by saving
 
                    // Resume simulation
                    play();
                }
            }
            else if (actionCommand.equals(LOAD_FILE)) {
                // Loads a grid from file
 
                // Variable declarations
                int returnVal;
                boolean interrupted;
                FileInputStream fileIn;
                ObjectInputStream objIn;
 
                if (timer.isRunning()) {
                    // Simulation is currently running
 
                    // Update interrupted
                    interrupted = true;
 
                    // Pause simulation
                    pause();
                }
                else {
                    // Simulation is not running
                    interrupted = false;
                }
 
                // Show dialogue to allow user to choose file
                returnVal = chooser.showOpenDialog(LifeSimFrame.this);
 
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // A file is chosen
 
                    try {
                        // Initialize fileOut and objOut
                        fileIn = new FileInputStream(chooser.getSelectedFile());
                        objIn = new ObjectInputStream(fileIn);
 
                        // Deserialize grid from chosen file
                        grid = (Grid) objIn.readObject();
 
                        // Close streams
                        objIn.close();
                        fileIn.close();
 
                        // Update UI
                        update();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    // File loading is cancelled
 
                    if (interrupted) {
                        // Simulation was interrupted by loading
 
                        // Resume simulation
                        play();
                    }
                }
            }
        }
    }
 
    // ActionListener for radio buttons
    class RadioButtonListener implements ActionListener
    {
        // Implement the actionPerformed method
        public void actionPerformed(ActionEvent e)
        {
            if (populateButton.isSelected()) {
                // Populate option
 
                // Show populateComboBox
                specificationPanel.setVisible(true);
                specificationPanelManager.show(specificationPanel, ORGANISMS);
            }
            else if (landscapeButton.isSelected()) {
                // Landscape option
 
                // Show landscapeComboBox
                specificationPanel.setVisible(true);
                specificationPanelManager.show(specificationPanel, FEATURES);
            }
            else {
                // Hide specificationPanel
                specificationPanel.setVisible(false);
            }
        }
    }
 
    // MouseListener for user interactions
    class InteractionListener implements MouseListener
    {
        // Implement methods
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
 
        // Implement mouseClicked method
        public void mouseClicked(MouseEvent e)
        {
            // Variable declarations
            int row, col, selectedIndex;
 
            if (e.getButton() == MouseEvent.BUTTON1) {
                // Left click
 
                // Determine coordinates of click
                row = e.getY() / 16;
                col = e.getX() / 16;
 
                if (eradicateButton.isSelected()) {
                    // Eradicate option
 
                    // Remove organism at click coordinates
                    grid.remove(row, col);
                }
                else if (populateButton.isSelected()) {
                    // Populate option
 
                    // Get selectedIndex
                    selectedIndex = populateComboBox.getSelectedIndex();
 
                    if (selectedIndex == CARROT) {
                        // Spawn a carrot
                        grid.add(new Carrot(grid, row, col));
                    }
                    else if (selectedIndex == RABBIT) {
                        // Spawn a rabbit
                        grid.add(new Rabbit(grid, row, col));
                    }
                    else if (selectedIndex == SNAKE) {
                        // Spawn a snake
                        grid.add(new Snake(grid, row, col));
                    }
                }
                else if (landscapeButton.isSelected()) {
                    // Landscape option
 
                    // Get selectedIndex
                    selectedIndex = landscapeComboBox.getSelectedIndex();
 
                    if (selectedIndex == MOUNTAIN) {
                        // Change tile to a mountain
                        grid.setMountain(true, row, col);
                    }
                    else {
                        // Remove mountain from tile
                        grid.setMountain(false, row, col);
 
                        // Set tile to selected terrain feature
                        grid.setFeature(selectedIndex, row, col);
                    }
                }
 
                // Update UI
                update();
            }
        }
    }
 
    // ActionListener for the timer
    class TimerListener implements ActionListener
    {
        // Implement the actionPerformed method
        public void actionPerformed(ActionEvent e)
        {
            // Advance grid to next turn
            nextTurn();
        }
    }
 
    // ChangeListener for speedSlider
    class SpeedSliderListener implements ChangeListener
    {
        // Implement the stateChanged method
        public void stateChanged(ChangeEvent e)
        {
            // Update timer delay values
            timer.setDelay(400 - 4 * speedSlider.getValue());
            timer.setInitialDelay(timer.getDelay());
        }
    }
 
    /* Constructors */
 
    // Default
    public LifeSimFrame()
    {
        // Variable declarations
        String[] organisms, features;
 
        // Load images
        Grid.loadImages();
        Organism.loadImages();
 
        // Create grid
        grid = new Grid(STARTING_PLAIN_VALUE, STARTING_DESERT_VALUE, STARTING_MOUNTAIN_VALUE);
        grid.generateOrganisms(STARTING_DENSITY_VALUE, STARTING_GROWTH_VALUE, STARTING_RABBIT_VALUE, STARTING_SNAKE_VALUE);
 
        // Set up arrays
        organisms = new String[]{"Carrot", "Rabbit", "Snake"};
        features = new String[]{"Plain", "Desert", "Mountain"};
 
        // Create listeners
        ButtonListener buttonListener = new ButtonListener();
        RadioButtonListener radioButtonListener = new RadioButtonListener();
 
        // Create/initialize components
        playPauseButton = new JButton("Play");
        playPauseButton.addActionListener(buttonListener);
        playPauseButton.setActionCommand(PLAY_PAUSE);
        JButton stepButton = new JButton("Step");
        stepButton.setActionCommand(STEP);
        stepButton.addActionListener(buttonListener);
        JButton generateTerrainButton = new JButton("Generate");
        generateTerrainButton.setActionCommand(GENERATE_TERRAIN);
        generateTerrainButton.addActionListener(buttonListener);
        JButton generateOrganismsButton = new JButton("Generate");
        generateOrganismsButton.setActionCommand(GENERATE_ORGANISMS);
        generateOrganismsButton.addActionListener(buttonListener);
        JButton resetTerrainButton = new JButton("Reset Terrain");
        resetTerrainButton.setActionCommand(RESET_TERRAIN);
        resetTerrainButton.addActionListener(buttonListener);
        JButton resetOrganismsButton = new JButton("Reset Organisms");
        resetOrganismsButton.setActionCommand(RESET_ORGANISMS);
        resetOrganismsButton.addActionListener(buttonListener);
        JButton resetGrowthButton = new JButton("Reset Growth");
        resetGrowthButton.setActionCommand(RESET_GROWTH);
        resetGrowthButton.addActionListener(buttonListener);
        JButton saveFileButton = new JButton("Save");
        saveFileButton.setActionCommand(SAVE_FILE);
        saveFileButton.addActionListener(buttonListener);
        JButton loadFileButton = new JButton("Load");
        loadFileButton.setActionCommand(LOAD_FILE);
        loadFileButton.addActionListener(buttonListener);
        speedSlider = new JSlider();
        speedSlider.addChangeListener(new SpeedSliderListener());
        plainSlider = new JSlider();
        plainSlider.setValue((int)(STARTING_PLAIN_VALUE * 100));
        desertSlider = new JSlider();
        desertSlider.setValue((int)(STARTING_DESERT_VALUE * 100));
        mountainSlider = new JSlider();
        mountainSlider.setValue((int)(STARTING_MOUNTAIN_VALUE * 100));
        densitySlider = new JSlider();
        densitySlider.setValue((int)(STARTING_DENSITY_VALUE * 100));
        rabbitSlider = new JSlider();
        rabbitSlider.setValue((int)(STARTING_RABBIT_VALUE * 100));
        snakeSlider = new JSlider();
        snakeSlider.setValue((int)(STARTING_SNAKE_VALUE * 100));
        growthSlider = new JSlider();
        growthSlider.setValue((int)(STARTING_GROWTH_VALUE * 100));
        noneButton = new JRadioButton("None");
        noneButton.setSelected(true);
        noneButton.addActionListener(radioButtonListener);
        eradicateButton = new JRadioButton("Eradicate");
        eradicateButton.addActionListener(radioButtonListener);
        populateButton = new JRadioButton("Populate");
        populateButton.addActionListener(radioButtonListener);
        landscapeButton = new JRadioButton("Landscape");
        landscapeButton.addActionListener(radioButtonListener);
        plainsLabel = new JLabel("0");
        desertsLabel = new JLabel("0");
        mountainsLabel = new JLabel("0");
        carrotsLabel = new JLabel("0");
        rabbitsLabel = new JLabel("0");
        snakesLabel = new JLabel("0");
        animalsLabel = new JLabel("0");
        organismsLabel = new JLabel("0");
        populateComboBox = new JComboBox<String>(organisms);
        landscapeComboBox = new JComboBox<String>(features);
 
        // Create gridPanel
        gridPanel = new GridPanel();
        gridPanel.addMouseListener(new InteractionListener());
 
        // Create button group
        ButtonGroup interactionOptions = new ButtonGroup();
        interactionOptions.add(noneButton);
        interactionOptions.add(eradicateButton);
        interactionOptions.add(populateButton);
        interactionOptions.add(landscapeButton);
 
        // Create specificationPanelManager
        specificationPanelManager = new CardLayout();
 
        // Create GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
 
        // Create panels
        JPanel playbackButtonsPanel = new JPanel();
        playbackButtonsPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        playbackButtonsPanel.add(playPauseButton, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        playbackButtonsPanel.add(stepButton, gbc);
        JPanel playbackPanel = new JPanel();
        playbackPanel.setBorder(BorderFactory.createTitledBorder("Playback"));
        playbackPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        playbackPanel.add(new JLabel("Speed"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        playbackPanel.add(speedSlider, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        playbackPanel.add(playbackButtonsPanel, gbc);
        JPanel parametersPanel = new JPanel();
        parametersPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
        parametersPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        parametersPanel.add(new JLabel("Growth"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        parametersPanel.add(growthSlider, gbc);
        specificationPanel = new JPanel();
        specificationPanel.setLayout(specificationPanelManager);
        specificationPanel.add(populateComboBox, ORGANISMS);
        specificationPanel.add(landscapeComboBox, FEATURES);
        specificationPanel.setVisible(false);
        JPanel interactionPanel = new JPanel();
        interactionPanel.setBorder(BorderFactory.createTitledBorder("Interaction"));
        interactionPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        interactionPanel.add(noneButton, gbc);
        gbc.gridy = 1;
        interactionPanel.add(eradicateButton, gbc);
        gbc.gridy = 2;
        interactionPanel.add(populateButton, gbc);
        gbc.gridy = 3;
        interactionPanel.add(landscapeButton, gbc);
        gbc.gridy = 4;
        interactionPanel.add(specificationPanel, gbc);
        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        statsPanel.add(new JLabel("Plains"), gbc);
        gbc.gridy = 1;
        statsPanel.add(new JLabel("Deserts"), gbc);
        gbc.gridy = 2;
        statsPanel.add(new JLabel("Mountains"), gbc);
        gbc.gridy = 3;
        statsPanel.add(new JLabel("Carrots"), gbc);
        gbc.gridy = 4;
        statsPanel.add(new JLabel("Rabbits"), gbc);
        gbc.gridy = 5;
        statsPanel.add(new JLabel("Snakes"), gbc);
        gbc.gridy = 6;
        statsPanel.add(new JLabel("Animals"), gbc);
        gbc.gridy = 7;
        statsPanel.add(new JLabel("Organisms"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        statsPanel.add(new JLabel("  "), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        statsPanel.add(plainsLabel, gbc);
        gbc.gridy = 1;
        statsPanel.add(desertsLabel, gbc);
        gbc.gridy = 2;
        statsPanel.add(mountainsLabel, gbc);
        gbc.gridy = 3;
        statsPanel.add(carrotsLabel, gbc);
        gbc.gridy = 4;
        statsPanel.add(rabbitsLabel, gbc);
        gbc.gridy = 5;
        statsPanel.add(snakesLabel, gbc);
        gbc.gridy = 6;
        statsPanel.add(animalsLabel, gbc);
        gbc.gridy = 7;
        statsPanel.add(organismsLabel, gbc);
        JPanel terrainPanel = new JPanel();
        terrainPanel.setBorder(BorderFactory.createTitledBorder("Terrain"));
        terrainPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        terrainPanel.add(new JLabel("Plains"), gbc);
        gbc.gridy = 1;
        terrainPanel.add(new JLabel("Deserts"), gbc);
        gbc.gridy = 2;
        terrainPanel.add(new JLabel("Mountains"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        terrainPanel.add(plainSlider, gbc);
        gbc.gridy = 1;
        terrainPanel.add(desertSlider, gbc);
        gbc.gridy = 2;
        terrainPanel.add(mountainSlider, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        terrainPanel.add(generateTerrainButton, gbc);
        JPanel organismsPanel = new JPanel();
        organismsPanel.setBorder(BorderFactory.createTitledBorder("Organisms"));
        organismsPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        organismsPanel.add(new JLabel("Density"), gbc);
        gbc.gridy = 1;
        organismsPanel.add(new JLabel("Rabbits"), gbc);
        gbc.gridy = 2;
        organismsPanel.add(new JLabel("Snakes"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        organismsPanel.add(densitySlider, gbc);
        gbc.gridy = 1;
        organismsPanel.add(rabbitSlider, gbc);
        gbc.gridy = 2;
        organismsPanel.add(snakeSlider, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        organismsPanel.add(generateOrganismsButton, gbc);
        JPanel resetPanel = new JPanel();
        resetPanel.setBorder(BorderFactory.createTitledBorder("Reset"));
        resetPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        resetPanel.add(resetTerrainButton, gbc);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        resetPanel.add(resetOrganismsButton, gbc);
        gbc.gridy = 2;
        resetPanel.add(resetGrowthButton, gbc);
        JPanel saveLoadPanel = new JPanel();
        saveLoadPanel.setBorder(BorderFactory.createTitledBorder("Save/Load"));
        saveLoadPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        saveLoadPanel.add(saveFileButton, gbc);
        gbc.gridx = 1;
        saveLoadPanel.add(loadFileButton, gbc);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(playbackPanel, gbc);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(parametersPanel, gbc);
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(interactionPanel, gbc);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(statsPanel, gbc);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(terrainPanel, gbc);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(organismsPanel, gbc);
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(resetPanel, gbc);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(saveLoadPanel, gbc);
 
        // Create contentPane
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        content.add(leftPanel, gbc);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        content.add(rightPanel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(gridPanel, gbc);
 
        // Update statistics
        updateStats();
 
        // Set up timer
        timer = new Timer(400 - 4 * speedSlider.getValue(), new TimerListener());
        timer.setInitialDelay(timer.getDelay());
 
        // Set up chooser
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Life Simulation Files (.sim)", "sim");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
 
        // Set window properties
        setContentPane(content);
        setTitle("Life Simulation");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
 
    /* Methods */
 
    // Stops the timer and updates playPauseButton
    private void pause()
    {
        // Stop timer
        timer.stop();
 
        // Set playPauseButton text to "play"
        playPauseButton.setText("Play");
    }
 
    // Restarts the timer and updates playPauseButton
    private void play()
    {
        // Restart timer
        timer.restart();
 
        // Set playPauseButton text to "pause"
        playPauseButton.setText("Pause");
    }
 
    // Updates stats on the UI
    public void updateStats()
    {
        // Variable declarations
        Stats statistics;
 
        // Get stats
        statistics = grid.getStats();
 
        // Update stats on UI
        plainsLabel.setText("" + statistics.plains());
        desertsLabel.setText("" + statistics.deserts());
        mountainsLabel.setText("" + statistics.mountains());
        carrotsLabel.setText("" + statistics.carrots());
        rabbitsLabel.setText("" + statistics.rabbits());
        snakesLabel.setText("" + statistics.snakes());
        animalsLabel.setText("" + statistics.animals());
        organismsLabel.setText("" + statistics.organisms());
    }
 
    // Updates the UI with regards to grid
    public void update()
    {
        // Update stats
        updateStats();
 
        // Repaint gridPanel
        gridPanel.repaint();
    }
 
    // Advances grid to the next turn
    public void nextTurn()
    {
        // Variable declarations
        double growth;
 
        // Get growth
        growth = growthSlider.getValue() * 0.01;
 
        // Advance grid
        grid.advance(growth);
 
        // Update UI
        update();
    }
 
    // Main method
    public static void main(String[] args)
    {
        // Set up a new LifeSimFrame
        LifeSimFrame lifeSimFrame = new LifeSimFrame();
        lifeSimFrame.setVisible(true);
 
        // Set minimum size of lifeSimFrame
        lifeSimFrame.setMinimumSize(lifeSimFrame.getPreferredSize());
    }
}