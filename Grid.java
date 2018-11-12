import java.awt.*;
import javax.imageio.*;
import java.io.*;
 
class Grid implements Serializable
{
    /* Constants */
 
    protected final static int PLAIN = 0;
    protected final static int DESERT = 1;
 
    /* Static Variables */
 
    protected static transient Image PLAIN_IMG, DESERT_IMG, MOUNTAIN_IMG;
    protected static transient Image[] FEATURE_IMG;
 
    /* Instance variables */
 
    protected Organism[][] organisms;
    protected int[][] features;
    protected boolean[][] mountains;
 
    /* Constructors */
 
    // Generates a new grid based on the parameters
    public Grid(double plainValue, double desertValue, double mountainDensity)
    {
        // Variable declarations
        double plainDensity;
 
        // Initialize grids
        organisms = new Organism[40][40];
        features = new int[40][40];
        mountains = new boolean[40][40];
 
        // Calculate plainDensity
        plainDensity = plainValue / (plainValue + desertValue);
 
        for (int i = 0; i < features.length; i++) {
            // Repeat for each row
 
            for (int j = 0; j < features[0].length; j++) {
                // Repeat for each column
 
                if (Math.random() < plainDensity) {
                    // The probability of the tile being a plain is decided by plainDensity
 
                    // Tile is plain
                    features[i][j] = PLAIN;
                }
                else {
                    // Tile is desert
                    features[i][j] = DESERT;
                }
 
                if (Math.random() < mountainDensity) {
                    // The probability of the tile being a mountain is decided by mountainDensity
 
                    // Tile is mountain
                    mountains[i][j] = true;
                }
            }
        }
    }
 
    /* Static methods */
 
    // Loads images of terrain features
    public static void loadImages()
    {
        try {
            PLAIN_IMG = ImageIO.read(new File("img/plain.png"));
            DESERT_IMG = ImageIO.read(new File("img/desert.png"));
            MOUNTAIN_IMG = ImageIO.read(new File("img/mountain.png"));
            FEATURE_IMG = new Image[]{PLAIN_IMG, DESERT_IMG};
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    // Returns the distance between two squares
    public static double distance(int row1, int col1, int row2, int col2)
    {
        // Variable declarations
        double distance;
 
        // Calculate distance
        distance = Math.sqrt(Math.pow(row2 - row1, 2) + Math.pow(col2 - col1, 2));
 
        return distance;
    }
 
    // Returns the distance between two organisms
    public static double distance(Organism organism1, Organism organism2)
    {
        return distance(organism1.row(), organism1.col(), organism2.row(), organism2.col());
    }
 
    // Returns the column that the given line of sight intersects at given row
    public static int columnIntersept(int row1, int col1, int row2, int col2, int row)
    {
        // Variable declarations
        double ratio;
        int deltaRow, deltaCol, horizontalDisplacement, verticalDisplacement, col;
 
        // Calculate deltaRow, deltaCol, and ratio
        deltaRow = row2 - row1;
        deltaCol = col2 - col1;
        ratio = deltaCol * 1.0 / deltaRow;
 
        if (deltaCol == 0) {
            // The two tiles are on the same column
            col = col1;
        }
        else {
            // Calculate horizontalDisplacement, verticalDisplacement, and col
            verticalDisplacement = row - row1;
            horizontalDisplacement = (int) Math.round(ratio * verticalDisplacement);
            col = col1 + horizontalDisplacement;
        }
 
        return col;
    }
 
    // Returns the row that the given line of sight intersects at given column
    public static int rowIntersept(int row1, int col1, int row2, int col2, int col)
    {
        // Variable declarations
        double ratio;
        int deltaRow, deltaCol, horizontalDisplacement, verticalDisplacement, row;
 
        // Calculate deltaRow, deltaCol, and ratio
        deltaRow = row2 - row1;
        deltaCol = col2 - col1;
        ratio = deltaRow * 1.0 / deltaCol;
 
        if (deltaRow == 0) {
            // The two tiles are on the same row
            row = row1;
        }
        else {
            // Calculate horizontalDisplacement, verticalDisplacement, and col
            horizontalDisplacement = col - col1;
            verticalDisplacement = (int) Math.round(ratio * horizontalDisplacement);
            row = row1 + verticalDisplacement;
        }
 
        return row;
    }
 
    /* Methods */
 
    // Returns number of rows
    public int rows()
    {
        return organisms.length;
    }
 
    // Returns number of columns
    public int columns()
    {
        return organisms[0].length;
    }
 
    // Returns the organism at given coordinates
    public Organism organismAt(int row, int col)
    {
        return organisms[row][col];
    }
 
    // Removes organism at (row, col) from the grid
    public void remove(int row, int col)
    {
        // Set organism at (x, y) to null
        organisms[row][col] = null;
    }
 
    // Puts the given organism at (x, y)
    public void add(Organism organism)
    {
        if (!mountains[organism.row()][organism.col()]) {
            // Tile isn't occupied by mountain
 
            // Set organism at (x, y) to organism
            organisms[organism.row()][organism.col()] = organism;
        }
    }
 
    // Changes given tile to given feature
    public void setFeature(int feature, int row, int col)
    {
        if (feature >= 0 && feature <= 1) {
            // Acceptable values are between 0 and 1
 
            // Set feature at coordinates
            features[row][col] = feature;
        }
    }
 
    // Changes mountain state at given tile
    public void setMountain(boolean mountain, int row, int col)
    {
        // Set mountain at coordinates
        mountains[row][col] = mountain;
    }
 
    // Generates organisms on the grid based on parameters
    public void generateOrganisms(double density, double growth, double rabbitValue, double snakeValue)
    {
        // Variable declarations
        double rabbitDensity, random;
 
        // Calculate rabbitDensity
        rabbitDensity = rabbitValue / (rabbitValue + snakeValue);
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                // Clear previous organism
                organisms[i][j] = null;
 
                if (!mountains[i][j]) {
                    // Tile is not occupied by mountain
 
                    if (Math.random() < density) {
                        // Density determines chance of spawning animal
 
                        // Generate random number
                        random = Math.random();
 
                        if (random < rabbitDensity) {
                            // Spawn a rabbit
                            add(new Rabbit(this, i, j));
                        }
                        else {
                            // Spawn a snake
                            add(new Snake(this, i, j));
                        }
                    }
                    else {
                        if (features[i][j] == PLAIN) {
                            // Carrots only grow on plains
 
                            if (Math.random() < growth) {
                                // Growth determines chance of growing carrot on empty plain
 
                                // Grow carrot
                                add(new Carrot(this, i, j));
                            }
                        }
                    }
                }
            }
        }
    }
 
    // Returns stats about terrain and population
    public Stats getStats()
    {
        // Variable declarations
        int plains = 0;
        int deserts = 0;
        int mountains = 0;
        int carrots = 0;
        int rabbits = 0;
        int snakes = 0;
        int animals = 0;
        int organisms = 0;
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile on grid
 
                if (features[i][j] == PLAIN) {
                    // Tile is plain
 
                    // Update counter
                    plains++;
                }
                else {
                    // Tile is desert
 
                    // Update counter
                    deserts++;
                }
 
                if (this.mountains[i][j]) {
                    // Tile is mountain
 
                    // Update counter
                    mountains++;
                }
 
                if (this.organisms[i][j] != null) {
                    // There is organism on tile
 
                    if (this.organisms[i][j] instanceof Carrot) {
                        // Organism is carrot
 
                        // Update counter
                        carrots++;
                    }
                    else {
                        // Organism is animal
 
                        if (this.organisms[i][j] instanceof Rabbit) {
                            // Organism is rabbit
 
                            // Update counter
                            rabbits++;
                        }
                        else if (this.organisms[i][j] instanceof Snake) {
                            // Organism is snake
 
                            // Update counter
                            snakes++;
                        }
 
                        // Update counter
                        animals++;
                    }
 
                    // Update counter
                    organisms++;
                }
            }
        }
 
        return new Stats(plains, deserts, mountains, carrots, rabbits, snakes, animals, organisms);
    }
 
    // Returns a 2D boolean array the size of the grid, where the squares that the organism can see are "true" and squares that the organism can't see are "false"
    public boolean[][] getFieldOfView(Organism organism)
    {
        // Variable declarations
        int row1, col1;
        boolean[][] fieldOfView;
        boolean visionBlocked;
 
        // Initialize fieldOfView
        fieldOfView = new boolean[organisms.length][organisms[0].length];
 
        // Get row1 and col1
        row1 = organism.row();
        col1 = organism.col();
 
        for (int row2 = 0; row2 < fieldOfView.length; row2++) {
            // Repeat for each row
 
            for (int col2 = 0; col2 < fieldOfView[0].length; col2++) {
                // Repeat for each column
 
                if (!(row1 == row2 && col1 == col2)) {
                    // Don't check for the square that the organism is currently on
 
                    if (distance(row1, col1, row2, col2) < organism.vision()) {
                        // Square is within vision of organism
 
                        // Reset visionBlocked
                        visionBlocked = false;
 
                        for (int row = row1; row != row2; row = (row2 > row1) ? row + 1 : row - 1) {
                            // Repeat for each row between row1 and row2
 
                            if (mountains[row][columnIntersept(row1, col1, row2, col2, row)]) {
                                // Mountain blocking line of sight
                                visionBlocked = true;
                            }
                        }
 
                        for (int col = col1; col != col2; col = (col2 > col1) ? col + 1 : col - 1) {
                            // Repeat for each column between col1 and col2
 
                            if (mountains[rowIntersept(row1, col1, row2, col2, col)][col]) {
                                // Mountain blocking line of sight
                                visionBlocked = true;
                            }
                        }
 
                        if (mountains[row2][col2]) {
                            // Current square is a mountain
                            visionBlocked = true;
                        }
 
                        if (!visionBlocked) {
                            // Organism can see square
                            fieldOfView[row2][col2] = true;
                        }
                    }
                }
                else {
                    // The organism can see itself
                    fieldOfView[row2][col2] = true;
                }
            }
        }
 
        return fieldOfView;
    }
 
    // Advance the grid one turn
    public void advance(double growth)
    {
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                if (!mountains[i][j]) {
                    // Tile is not occupied by mountain
 
                    if (features[i][j] == PLAIN) {
                        // Carrots only grow on plains
 
                        if (organisms[i][j] == null) {
                            // There is no organism on tile
 
                            if (Math.random() < growth) {
                                // Growth determines chance of growing carrot on empty plain
 
                                // Grow carrot
                                add(new Carrot(this, i, j));
                            }
                        }
                    }
                }
            }
        }
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                if (organisms[i][j] != null) {
                    // There is organism on tile
 
                    // Activate organism
                    organisms[i][j].activate();
                }
            }
        }
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                if (organisms[i][j] != null) {
                    // There is organism on tile
 
                    // Conduct organism's movement phase
                    organisms[i][j].move();
                }
            }
        }
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                if (organisms[i][j] != null) {
                    // There is organism on tile
 
                    // Conduct organism's starvation phase
                    organisms[i][j].starve();
                }
            }
        }
 
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < columns(); j++) {
                // Repeat for each tile in grid
 
                if (organisms[i][j] != null) {
                    // There is organism on tile
 
                    // Conduct organism's procreation phase
                    organisms[i][j].breed();
                }
            }
        }
    }
 
    // Displays the grid
    public void show(Graphics g)
    {
        for (int i = 0; i < organisms.length; i++) {
            // Repeat for each row
 
            for (int j = 0; j < organisms[0].length; j++) {
                // Repeat for each column
 
                // Draw terrain
                g.drawImage(FEATURE_IMG[features[i][j]], j * 16, i * 16, null);
 
                if (mountains[i][j]) {
                    // There is mountain on this tile
 
                    // Draw mountain
                    g.drawImage(MOUNTAIN_IMG, j * 16, i * 16, null);
                }
 
                if (organisms[i][j] != null) {
                    // There is organism on this tile
 
                    // Show organism
                    organisms[i][j].show(g, j * 16, i * 16);
                }
            }
        }
    }
}