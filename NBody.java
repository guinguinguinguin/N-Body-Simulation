/**
 * NBody.java
 *
 * Compilation: javac NBody.java
 * Sample execution: java NBody 157788000.0 25000.0 < inputs/planets.txt
 * Dependencies: StdDraw.java StdAudio.java
 * Input files: ./inputs/*.txt
 *
 * @author chindesaurus
 * @version 1.00
 */

public class NBody {

    // time delay for animation in milliseconds
    private static final int FRAME_DELAY = 10;
        
    // gravitational constant in N*m^2 / kg^2
    private static final double G = 6.67e-11;  

    // background music file
    private static final String music = "./2001.mid";


    /**
     * Performs a brute-force N-body simulation using a 
     * leapfrog finite difference approximation.
     *
     * @param argv  the command-line arguments
     */
    public static void main(String[] argv) {
        
        // Scanner object for reading from stdin
        java.util.Scanner console = new java.util.Scanner(System.in);

        // ensure proper usage
        if (argv.length != 2) {
            System.err.println("Usage: java NBody T delta_t");
            System.exit(1);
        }

        // store the command-line arguments
        double T = Double.parseDouble(argv[0]);
        double delta_t = Double.parseDouble(argv[1]);

        // number of particles in the universe
        int N = console.nextInt();

        // radius of the universe
        double R = console.nextDouble();

        // parallel arrays
        double[] p_x = new double[N];   // x positions
        double[] p_y = new double[N];   // y positions
        double[] v_x = new double[N];   // x velocities
        double[] v_y = new double[N];   // y velocities
        double[] mass = new double[N];  // particle masses 
        String[] image = new String[N]; // path to image files 
        double[] f_x = new double[N];   // net forces in x direction
        double[] f_y = new double[N];   // net forces in y direction

    
        // read in initial state for all N bodies
        for (int i = 0; i < N; i++) {
            p_x[i] = console.nextDouble();
            p_y[i] = console.nextDouble(); 
            v_x[i] = console.nextDouble();
            v_y[i] = console.nextDouble(); 
            mass[i] = console.nextDouble(); 
            image[i] = "./images/" + console.next();
        }

        // scale the drawing window using the radius of the universe
        StdDraw.setXscale(-R, +R);
        StdDraw.setYscale(-R, +R);

        // play music
        StdAudio.play(music);
            
        // perform the brute-force simulation
        for (double t = 0.0; t < T; t += delta_t) {

            // draw the background, centered at (0, 0)
            StdDraw.picture(0, 0, "./images/starfield.jpg");

            // calculate the net force exerted by body j on body i
            for (int i = 0; i < N; i++) {
                
                // reset forces to zero 
                f_x[i] = 0.0;
                f_y[i] = 0.0;
                
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        double dx = p_x[j] - p_x[i]; 
                        double dy = p_y[j] - p_y[i]; 
                        double dist = distance(p_x[j], p_y[j], p_x[i], p_y[i]);
                        double F_net = force(mass[i], mass[j], dist);
                       
                        f_x[i] += F_net * dx / dist;
                        f_y[i] += F_net * dy / dist; 
                    }
                } 
            }

            // acceleration in x and y directions
            double a_x = 0.0;
            double a_y = 0.0;

            // calculate the new acceleration, velocity, and position for each body
            for (int i = 0; i < N; i++) {
                a_x = f_x[i] / mass[i];
                a_y = f_y[i] / mass[i];

                v_x[i] += delta_t * a_x;
                v_y[i] += delta_t * a_y;

                p_x[i] += delta_t * v_x[i];
                p_y[i] += delta_t * v_y[i];
               
                // draw the bodies 
                StdDraw.picture(p_x[i], p_y[i], image[i]); 
            }

            // animate!
            StdDraw.show(FRAME_DELAY);
        }  

        // print the state of the universe at the end of the simulation
        System.out.printf("%d\n", N);
        System.out.printf("%.2e\n", R);
        for (int i = 0; i < N; i++) {
            System.out.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                   p_x[i], p_y[i], v_x[i], v_y[i], mass[i], image[i]);
        }
    }


    /**
     * Returns the Euclidean distance between points (x1, y1) and (x2, y2).
     *
     * @param x1  x-coordinate of point 1
     * @param y1  y-coordinate of point 1
     * @param x2  x-coordinate of point 2
     * @param y2  y-coordinate of point 2
     * @return    Euclidean distance between (x1, y1) and (x2, y2)
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        
        double dx = x1 - x2;
        double dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy);
    }


    /**
     * Returns the magnitude of the gravitational force between two bodies
     * of mass m1 and m2 that are distance r apart.
     *
     * @param m1  mass of the first body
     * @param m2  mass of the second body
     * @param r   the distance between the bodies of masses m1 and m2
     * @return    the pairwise force between the two bodies
     */
    private static double force(double m1, double m2, double r) {
        return (G * m1 * m2) / (r * r);
    }
}
