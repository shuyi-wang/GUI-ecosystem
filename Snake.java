import javax.imageio.*;
import java.awt.*;
import java.io.*;
 
class Snake extends Organism
{
    /* Constructors */
 
    // Default
    public Snake(Grid grid, int row, int col)
    {
        // Call parent constructor
        super(grid, row, col);
 
        // Set properties
        movement = 2;
        hunger = 3;
        foodValue = 3;
        breedHunger = 9;
    }
 
    /* Methods */
 
    // Override the show method
    public void show(Graphics g, int x, int y)
    {
        // Draw img
        g.drawImage(SNAKE_IMG, x, y, null);
    }
 
    // Override the eats method
    protected boolean eats(Organism organism)
    {
        return organism instanceof Rabbit;
    }
 
    // Override the isEatenBy method
    protected boolean isEatenBy(Organism organism){
        return false;
    }
 
    // Override the reproduce method
    protected Organism reproduce(int row, int col)
    {
        return new Snake(grid, row, col);
    }
}