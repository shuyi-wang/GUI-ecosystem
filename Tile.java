class Tile
{
    /* Instance variables */
 
    private int row, col;
 
    /* Constructors */
 
    // Given row and column
    public Tile(int row, int col)
    {
        // Set properties
        this.row = row;
        this.col = col;
    }
 
    /* Methods */
 
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
}