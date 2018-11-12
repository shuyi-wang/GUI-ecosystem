import javax.imageio.*;
import java.awt.*;
import java.io.*;
 
class Rabbit extends Organism
{
    /* Constructors */
 
    // Default
    public Rabbit(Grid grid, int row, int col)
    {
        // Call parent constructor
        super(grid, row, col);
 
        // Set properties
        movement = 1;
        hunger = 1;
        foodValue = 2;
        breedHunger = 3;
    }
 
    /* Methods */
 
    // Override the show method
    public void show(Graphics g, int x, int y)
    {
        // Draw img
        g.drawImage(RABBIT_IMG, x, y, null);
    }
 
    // Override the eats method
    protected boolean eats(Organism organism)
    {
        return organism instanceof Carrot;
    }
 
    // Override the isEatenBy method
    protected boolean isEatenBy(Organism organism)
    {
       return organism instanceof Snake;
    }
 
    // Override the reproduce method
    protected Organism reproduce(int row, int col)
    {
        return new Rabbit(grid, row, col);
    }
}