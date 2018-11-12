class Stats
{
    /* Instance variables */
 
    private int plains, deserts, mountains, carrots, rabbits, snakes, animals, organisms;
 
    /* Constructors */
 
    // Given all the stats
    public Stats(int plains, int deserts, int mountains, int carrots, int rabbits, int snakes, int animals, int organisms)
    {
        this.plains = plains;
        this.deserts = deserts;
        this.mountains = mountains;
        this.carrots = carrots;
        this.rabbits = rabbits;
        this.snakes = snakes;
        this.animals = animals;
        this.organisms = organisms;
    }
 
    /* Methods */
 
    // Returns plains
    public int plains()
    {
        return plains;
    }
 
    // Returns deserts
    public int deserts()
    {
        return deserts;
    }
 
    // Returns mountains
    public int mountains()
    {
        return mountains;
    }
 
    // Returns carrots
    public int carrots()
    {
        return carrots;
    }
 
    // Returns rabbits
    public int rabbits()
    {
        return rabbits;
    }
 
    // Returns snakes
    public int snakes()
    {
        return snakes;
    }
 
    // Returns animals
    public int animals()
    {
        return animals;
    }
 
    // Returns organisms
    public int organisms()
    {
        return organisms;
    }
}
