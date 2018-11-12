import javax.imageio.*;
import java.awt.*;
import java.io.*;
 
class Carrot extends Organism
{
    /* Constructors */
 
    // Default
    public Carrot(Grid grid, int row, int col)
    {
        // Call parent constructor
        super(grid, row, col);
 
        // Set properties
        movement = 0;
        hunger = 4;
        foodValue = 2;
        breedHunger = 0;
    }
 
    /* Methods */
 
    // Override the show method
    public void show(Graphics g, int x, int y)
    {
        // Draw img
        g.drawImage(CARROT_IMG, x, y, null);
    }
 
    // Override the eats method
    protected boolean eats(Organism organism)
    {
        return false;
    }
 
    // Override the isEatenBy method
    protected boolean isEatenBy(Organism organism)
    {
       return organism instanceof Rabbit;
    }
 
    // Override the reproduce method
    protected Organism reproduce(int row, int col)
    {
        return new Carrot(grid, row, col);
    }
 
    // Override the move methods so carrots don't move
    public void move()
    {
        // Nothing happens
    }
 
    // Override the breed method so carrots don't reproduce
    public void breed()
    {
        // Nothings happens
    }
}