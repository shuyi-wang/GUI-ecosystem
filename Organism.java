import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.*;
import java.io.*;
 
abstract class Organism implements Serializable
{
    /* Static variables */
 
    protected static transient Image CARROT_IMG, RABBIT_IMG, SNAKE_IMG;
 
    /* Instance variables */
 
    protected Grid grid;
    protected int row, col, hunger;
    protected int movement, foodValue, breedHunger;
    protected boolean moved;
 
    /* Constructors */
 
    // Default
    public Organism(Grid grid, int row, int col)
    {
        // Set properties
        this.grid = grid;
        this.row = row;
        this.col = col;
        moved = true;
    }
 
    /* Static Methods */
 
    // Loads images of organisms
    public static void loadImages()
    {
        try {
            CARROT_IMG = ImageIO.read(new File("img/carrot.png"));
            RABBIT_IMG = ImageIO.read(new File("img/rabbit.png"));
            SNAKE_IMG = ImageIO.read(new File("img/snake.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /* Methods */
 
    // Allows the organism to move again
    public void activate()
    {
        // Reset moved
        moved = false;
    }
 
    // Returns the organism closest to this organism out of an ArrayList of organisms
    private Organism getClosest(ArrayList<Organism> list)
    {
        // Variables
        double closestDistance, currentDistance;
        Organism closest;
 
        // Shuffle list to ensure organisms in the same direction don't get preferrence in case of a tie
        Collections.shuffle(list);
 
        // Set closest to first organism
        closest = list.get(0);
        closestDistance = Grid.distance(this, closest);
 
        for (int i = 1; i < list.size(); i++) {
            // Repeat for each item in list afterwards
 
            // Calculate currentDistance
            currentDistance = Grid.distance(this, list.get(i));
 
            if (currentDistance < closestDistance) {
                // Current organism is closer than closest
 
                // Update closest
                closest = list.get(i);
                closestDistance = currentDistance;
            }
        }
 
        return closest;
    }
 
    // Returns row
    public int row()
    {
        return row;
    }
 
    // Returns col
    public int col()
    {
        return col;
    }
 
    // Returns vision
    public int vision()
    {
        // Vision is always 3 times movement
        return movement * 3;
    }
 
    // Displays the organism
    public abstract void show(Graphics g, int x, int y);
 
    // Returns whether the given organism is food of this organism
    protected abstract boolean eats(Organism organism);
 
    // Returns whether this organism is food of given organism
    protected abstract boolean isEatenBy(Organism organism);
 
    // Returns a new instance of this species
    protected abstract Organism reproduce(int row, int col);
 
    // Moves the organism to the given position
    public void moveTo(int row, int col)
    {
        // Remove organism from grid
        grid.remove(this.row, this.col);
 
        // Update properties
        this.row = row;
        this.col = col;
 
        // Add organism back to grid at new position
        grid.add(this);
    }
 
    // Moves the organism in the given direction
    public void moveTowards(int targetRow, int targetCol)
    {
        // Variable declarations
        int deltaRow, deltaCol, deltaRowSign, deltaColSign, verticalDistance, horizontalDistance, destinationRow, destinationCol;
        boolean[][] fieldOfView;
        double hypotenuse, ratio;
 
        // Get fieldOfView of organism
        fieldOfView = grid.getFieldOfView(this);
 
        // Calculate deltaRow, deltaCol and hypotenuse
        deltaRow = targetRow - row;
        deltaCol = targetCol - col;
        hypotenuse = Math.sqrt(Math.pow(deltaRow, 2) + Math.pow(deltaCol, 2));
 
        if (deltaRow < 0) {
            // DeltaRow is negative
            deltaRowSign = -1;
        }
        else {
            deltaRowSign = 1;
        }
 
        if (deltaCol < 0) {
            // DeltaCol is negative
            deltaColSign = -1;
        }
        else {
            deltaColSign = 1;
        }
 
        // Remove this organism from grid
        grid.remove(row, col);
 
        if (Math.abs(deltaRow) > Math.abs(deltaCol)) {
            // Target is farther away vertically than horizontally
 
            // Calculate ratio, verticalDistance, destinationRow and destinationCol
            ratio = Math.abs(deltaRow) / hypotenuse;
            verticalDistance = (int) Math.floor(ratio * movement);
            destinationRow = row + deltaRowSign * verticalDistance;
            destinationCol = Grid.columnIntersept(row, col, targetRow, targetCol, destinationRow);
 
            while (destinationRow < 0 || destinationRow >= 40 || destinationCol < 0 || destinationCol >= 40 || !fieldOfView[destinationRow][destinationCol] || grid.organismAt(destinationRow, destinationCol) != null) {
                // Repeat until a valid destination is found (within field of view and unoccupied)
 
                // Reduce verticalDistance
                verticalDistance--;
 
                // Recalculate destinationRow and destinationCol
                destinationRow = row + deltaRowSign * verticalDistance;
                destinationCol = Grid.columnIntersept(row, col, targetRow, targetCol, destinationRow);
            }
        }
        else {
            // Target is farther away horizontally than vertically
 
            // Calculate ratio, horizontalDistance, destinationCol and destinationRow
            ratio = Math.abs(deltaCol) / hypotenuse;
            horizontalDistance = (int) Math.floor(ratio * movement);
            destinationCol = col + deltaColSign * horizontalDistance;
            destinationRow = Grid.rowIntersept(row, col, targetRow, targetCol, destinationCol);
 
            while (destinationRow < 0 || destinationRow >= 40 || destinationCol < 0 || destinationCol >= 40 || !fieldOfView[destinationRow][destinationCol] || grid.organismAt(destinationRow, destinationCol) != null) {
                // Repeat until a valid destination is found (within field of view and unoccupied)
 
                // Reduce horizontalDistance
                horizontalDistance--;
 
                // Recalculate destinationCol and destinationRow
                destinationCol = col + deltaColSign * horizontalDistance;
                destinationRow = Grid.rowIntersept(row, col, targetRow, targetCol, destinationCol);
            }
        }
 
        // Move to destination
        moveTo(destinationRow, destinationCol);
    }
 
    // Moves away from given direction
    public void moveAwayFrom(int sourceRow, int sourceCol)
    {
        // Variable declarations
        int deltaRow, deltaCol, targetRow, targetCol;
 
        // Calculate deltaRow, deltaCol, targetRow, and targetCol
        deltaRow = row - sourceRow;
        deltaCol = col - sourceCol;
        targetRow = row + deltaRow;
        targetCol = col + deltaCol;
 
        // Move towards target
        moveTowards(targetRow, targetCol);
    }
 
    // Moves to a random tile as far away as possible
    public void wander()
    {
        // Variable declarations
        ArrayList<Tile> potentialMoves;
        boolean[][] fieldOfView;
        Tile destination;
 
        // Initialize potentialMoves
        potentialMoves = new ArrayList<Tile>();
 
        // Get field of view
        fieldOfView = grid.getFieldOfView(this);
 
        for (int distance = movement; distance >= 0 && potentialMoves.isEmpty(); distance--) {
            // Repeat from farthest to closest until at least one potential move is found
 
            // Reset potentialMoves
            potentialMoves = new ArrayList<Tile>();
 
            for (int i = 0; i < grid.rows(); i++) {
                for (int j = 0; j < grid.columns(); j++) {
                    // Repeat for each tile in grid
 
                    if (fieldOfView[i][j] && grid.organismAt(i, j) == null && Math.ceil(Grid.distance(row, col, i, j)) == distance) {
                        // This organism can see tile and tile is exactly distance away
 
                        // Add tile to potentialMoves
                        potentialMoves.add(new Tile(i, j));
                    }
                }
            }
        }
 
        if (!potentialMoves.isEmpty()) {
            // A potential move was found
 
            // Choose random destination
            destination = potentialMoves.get((int) (Math.random() * potentialMoves.size()));
 
            // Move to destination
            moveTo(destination.row(), destination.col());
        }
    }
 
    // Eats the given organism
    public void eat(Organism target)
    {
        // Move to target's position (replace it)
        moveTo(target.row(), target.col());
 
        // Update hunger
        hunger += target.foodValue;
    }
 
    // Performs this organism's actions for the movement phase
    public void move()
    {
        if (!moved) {
            // The organism can still move
 
            // Variable declarations
            boolean[][] fieldOfView;
            Organism target, closestFood, closestPredator;
            ArrayList<Organism> food, predators;
 
            // Initialize food and predators
            food = new ArrayList<Organism>();
            predators = new ArrayList<Organism>();
 
            // Determine fieldOfView
            fieldOfView = grid.getFieldOfView(this);
 
            for (int row = 0; row < grid.rows(); row++) {
                // Repeat for each row
 
                for (int col = 0; col < grid.columns(); col++) {
                    // Repeat for each column
 
                    if (fieldOfView[row][col]) {
                        // Organism can see this square
 
                        // Get organism on square
                        target = grid.organismAt(row, col);
 
                        if (target != null) {
                            // There is an organism on this square
 
                            if (eats(target)) {
                                // This organism eats target
 
                                // Add target to food list
                                food.add(target);
                            }
                            else if (isEatenBy(target)) {
                                // This organism is eaten by target
 
                                // Add target to predators list
                                predators.add(target);
                            }
                        }
                    }
                }
            }
 
            if (!food.isEmpty()) {
                // There is food in vision
 
                // Get closestFood
                closestFood = getClosest(food);
 
                if (Grid.distance(this, closestFood) > movement) {
                    // Can't reach closestFood in one turn
 
                    // moveTowards closestFood
                    moveTowards(closestFood.row(), closestFood.col());
                }
                else {
                    // Can reach closestFood in one turn
 
                    // Eat closestFood
                    eat(closestFood);
                }
            }
            else if (!predators.isEmpty()) {
                // There is predator in vision
 
                // Get closestPredator
                closestPredator = getClosest(predators);
 
                // Move away from closestPredator
                moveAwayFrom(closestPredator.row(), closestPredator.col());
            }
            else {
                // There is no particular movement goal
 
                // Wander around
                wander();
            }
 
            // The organism's move is used up for this turn
            moved = true;
        }
    }
 
    // Starve this organism, if hunger is lower than is required, kill it
    public void starve()
    {
        // Update hunger
        hunger--;
 
        if (hunger <= 0) {
            // Starved to death
 
            // Remove organism from grid
            grid.remove(row, col);
        }
    }
 
    // Performs this organism's breeding phase
    public void breed()
    {
        // Variable declarations
        boolean[][] fieldOfView;
        ArrayList<Tile> availableSpots;
        Tile babySpot;
        Organism baby;
 
        // Initialize availableSpots
        availableSpots = new ArrayList<Tile>();
 
        // Get field of view
        fieldOfView = grid.getFieldOfView(this);
 
        if (hunger >= breedHunger) {
            // Organism has enough hunger to breed
 
            for (int i = 0; i < grid.rows(); i++) {
                for (int j = 0; j < grid.columns(); j++) {
                    // Repeat for each tile in grid
 
                    if (fieldOfView[i][j] && grid.organismAt(i, j) == null && Math.ceil(Grid.distance(row, col, i, j)) == 1) {
                        // This organism can see tile and tile is next to organism
 
                        // Add tile to availableSpots
                        availableSpots.add(new Tile(i, j));
                    }
                }
            }
 
            if (!availableSpots.isEmpty()) {
                // There is available spot for baby
 
                // Choose random babySpot
                babySpot = availableSpots.get((int) (Math.random() * availableSpots.size()));
 
                // Spawn baby at spot
                baby = reproduce(babySpot.row(), babySpot.col());
                grid.add(baby);
 
                // Giving birth costs half of hunger
                hunger = (int) Math.round(hunger * 0.5);
            }
        }
    }
}