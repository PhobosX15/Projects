package com.mygdx.game.bot;

import com.mygdx.physics.Vector2d;
import com.mygdx.physics.EulerSolver;
import com.mygdx.physics.FunctionReader;


public class GA_bot
{
    public String formula="";
    public double [][]friction;
    public double mass;
    public double gravityConstant;
    public double step_size;
    public int max_gen=150;
    public Vector2d hole_pos;
    public static int pop_size= 200;
    public double mutation_rate=0.01;
    public double max_fit;
    public Vector2d initial_pos;
    public Vector2d initial_vel;
    //private VerletSolver vs;
    private EulerSolver es;
    private double distanceFromHole;
    private double[] fitness = new double[pop_size];
    private int best_elm;
    private static double[][] initial_pop= new double[pop_size][2];
    public double[][] children_pop= new double[pop_size][2];
    public GA_bot(Vector2d initial_pos, Vector2d holepos, EulerSolver es, String formula,double step_size,double mass, double gravityConstant)
    {
        this.initial_pos=initial_pos;
        this.hole_pos= holepos;
        this.formula = formula;
        this.es = es;
        this.step_size= step_size;
        this.mass= mass;
        this.gravityConstant= gravityConstant;

    }
    public void population()
    {
        for(int i=0;i<pop_size;i++)
        {
            initial_pop[i][0]= Math.random() * 3;
            initial_pop[i][1]= (double)Math.random()*3;
        }
    }

    public int setFitness()
    {
        max_fit=0;
        
        for (int i=0;i<initial_pop.length;i++ )
        {
            //Vector2d ball_pos= new Vector2d(x, y)
            //Vector2d final_pos=es.position(initial_pos,new Vector2d(initial_pop[i][0],initial_pop[i][1]));
            boolean running = true;
            Vector2d newPosition = new Vector2d(initial_pos.get_x(), initial_pos.get_y());
            int k = 0;
            Vector2d finalPosition = new Vector2d(initial_pos.get_x(), initial_pos.get_y());
            initial_vel= new Vector2d(initial_pop[i][0], initial_pop[i][1]);
            // hit ball once with default velocity
            while (running) {
                k++;
                newPosition = throwBall(newPosition);
                // Move the ball every 20 steps, to prevent game from lagging
                if (k % 20 == 0) {
                    finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());

                }
                if (Math.abs(initial_vel.get_y()) < 1 && Math.abs(initial_vel.get_x()) < 1) {
                    finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());
                    running = false;
                }

            }
            
            double final_x= finalPosition.get_x();
            double final_y= finalPosition.get_y();
            distanceFromHole= Math.sqrt(Math.pow((hole_pos.get_x()-final_x),2)+ Math.pow((hole_pos.get_y()-final_y),2));
            fitness[i]= Math.pow((1/ distanceFromHole),2);

            if(distanceFromHole<= 0.02)
            {
                return i;
            }
            if(fitness[i]> max_fit)
            {
                best_elm=i;
                max_fit= fitness[i];
            }
        }
        return -1;

    }

    public Vector2d crossover(Vector2d velocity1, Vector2d velocity2)
    {
        Vector2d new_velocity= new Vector2d();
        new_velocity.setX((velocity1.get_x()+ velocity2.get_x())/2);
        new_velocity.setY((velocity1.get_y()+ velocity2.get_y())/2);
        return new_velocity;
    }
    public Vector2d mutate(Vector2d child)
    {
        if((double)(Math.random())<mutation_rate)
        {
            if(child.get_x()< (3/1.5))
            {
                child.setX(((child.get_x())*1.5));
            }
            else
            {
                child.setX(((child.get_x())*.5));   
            }
        }
    return child;
    }
    public void selection()
    {
        int n=0;
        while(n< pop_size)
        {
            int rand_index1=(int)(Math.random()*pop_size);
            double rand_fitness1= (Math.random()*max_fit);
            int rand_index2 = (int)(Math.random()*pop_size);
            double rand_fitness2= (Math.random()*max_fit);
            if(fitness[rand_index1]>= rand_fitness1 && fitness[rand_index2]>= rand_fitness2)
            {
                Vector2d parent1= new Vector2d(initial_pop[rand_index1][0], initial_pop[rand_index1][1]);
                Vector2d parent2= new Vector2d(initial_pop[rand_index2][0], initial_pop[rand_index2][1]);
                Vector2d child = crossover(parent1, parent2);
                child=mutate(child);
                children_pop[n][0]= child.get_x();
                children_pop[n][1]= child.get_y();
                n++;
            }
        }
        for(int i =0;i<pop_size;i++)
        {
            initial_pop[i][0]= children_pop[i][0];
            initial_pop[i][1]= children_pop[i][1];
        }

    }
    private Vector2d throwBall(Vector2d initialPosition) {
        // Read the mathematical formula
        FunctionReader reader = new FunctionReader(formula);
        // Get initial position
        // Compute angles for x and y axis
        double angleX = reader.derivativeX(initialPosition);
        double angleY = reader.derivativeY(initialPosition);
        double frictionValue = friction[(int) initialPosition.get_x()][(int) initialPosition.get_y()];
        EulerSolver eulerSolver = new EulerSolver(step_size, mass,gravityConstant,frictionValue);
        // Compute velocity after a step of time
        Vector2d vector2d = hitWall(initial_vel, initialPosition);
        initial_vel = eulerSolver.velocity(vector2d, angleX, angleY);
        // Compute position after a step of time
        return eulerSolver.position(initialPosition, initial_vel);
    }
    private Vector2d hitWall(Vector2d initialVelocity, Vector2d position) {
        Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(), initialVelocity.get_y());
        // Check which wall did the ball hit
        int width = 640 * 8 / 3;
        if (position.get_x() <= 0 || position.get_x() >= width) {
            velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
        }
        int height = 640 * 2;
        if (position.get_y() <= 0 || position.get_y() >= height) {
            velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
        }

        return velocityAfterCollision;
    }
    public Vector2d run_GA()
    {
        population();
        int gen=0;
        int check=-1;
        int best=0;
        boolean flag = true;
        while(gen< max_gen)
        {
            check = setFitness();
            if(check!=-1)
            {
                best = check;
                flag= false;
            }
            if(flag)
            {
                selection();
                gen++;
            }
           else
            {
                break;
            }
        }
        if(flag)
        {
            best= best_elm;
        }
        Vector2d shot = new Vector2d(initial_pop[best][0], initial_pop[best][1]);
        return shot;
    }
    /*public static void main(String[] args) {
        
        
        Vector2d hole_pos= new Vector2d(0, 10);
        String filename = "D:\\DKE-(19-22)\\Project1-2\\test\\core\\src\\com\\mygdx\\physics\\testerForSolvers.txt";
        //Course course = new Course(filename);
        
        PuttingCourse pt=new PuttingCourse();
        pt.readFile("D:\\DKE-(19-22)\\Project1-2\\test\\core\\src\\com\\mygdx\\physics\\testerForSolvers.txt");
        VerletSolver vs = new VerletSolver(0.01,pt);
        //Vector2d position = course.get_ball_pos();
        Vector2d position = new Vector2d(0.0, 0.0);
        EulerSolver es = new EulerSolver(0.01, 45.93, 9.81, 0.131);
        GA_bot test = new GA_bot(position, hole_pos, vs);
        test.population();
        System.out.println( initial_pop[0][1]);
        Vector2d best_shot= test.run_GA();
        System.out.println(best_shot.get_x());
        System.out.println(best_shot.get_y());

    }*/

}

        