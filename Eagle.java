// // Shuyi Wang
// // Eagle class
// // Jan. 16th, 2017
// // ICS 4U1
//  
// import javax.imageio.*;
// import java.awt.*;
// import java.io.*;
//  
// class Eagle extends Organism
// {
//     /* Constructors */
//  
//     // Default
//     public Eagle(Grid grid, int row, int col)
//     {
//         // Call parent constructor
//         super(grid, row, col);
//  
//         // Set properties
//         vision = 20;
//         movement = 10;
//         hunger = 4;
//         foodValue = 4;
//         requiredHunger = hunger +1;
//  
//         try {
//             // Load image
//             img = ImageIO.read(new File("img/eagle.png"));
//         }
//         catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
//  
//     /* Methods */
//  
//     // Override the eats method
//     protected boolean eats(Organism organism)
//     {
//         return organism instanceof Snake;
//     }
//  
//     // Override the isEatenBy method
//     protected boolean isEatenBy(Organism organism)
//     {
//        return false;
//     }
//  
//     // Override the reproduce method
//     protected Organism reproduce(int row, int col)
//     {
//         return new Eagle(grid, row, col);
//     }
// }
