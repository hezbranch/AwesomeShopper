import java.util.Arrays;
import java.util.ArrayList;
import com.supermarket.*;

// Edited by Hezekiah Branch and Michael LoTurco

public class Agent extends SupermarketComponentImpl
{
    // Class constructor
    public Agent()
    {
        super();
        shouldRunExecutionLoop = true;
        log.info("In Agent constructor.");
        Goal plan = new Goal(PLAN, NOWHERE);
        goals.add(plan);
    }
    // Goal States
    static String PLAN = "plan";
    static double[] NOWHERE = {-1, -1};
    
    // Author-defined boolean variable
    boolean firsttime = true;
    ArrayList<Goal> goals = new ArrayList<Goal>();
    // Function: grabCartGoNorth
    // Purpose: Move agent to cart area and bring
    //          it up back north to register area
    // Input: An observation state (i.e. Observation)
    // Returns: None (i.e. void)
    // Effect(s): External timing, infinite looping
    protected void grabCartGoNorth(Observation obs)
    {
        // Stop agent from going south and prevent
        // a possible collision due to cases where:
        // 1. Player can interact with cart
        // 2. Player does not have a cart
        // Effect(s): If #2 is not checked, endless #1.
        //            Executed every 100ms externally.
        if (obs.cartReturns[0].canInteract(obs.players[0]) && obs.players[0].curr_cart != 0) {
            nop();
            interactWithObject();
            nop();
            // Case where agent has obtained a cart
            // and needs to go north
        } else if (obs.players[0].curr_cart == 0) {
            // Prevent endless collisions with an
            // infinite loop of no movement here
            // * Feels unethical to stop agent
            //  movement infinitely like this :( *
            if (obs.players[0].position[1] < 13) {
                while (true) {
                    nop(); // Stop agent movement
                }
            }
            // Go up to the register
            goNorth();
            // Case where agent has no cart
            // Starting point of agent movement
        } else {
            // Go down to the cart area
            goSouth();
        }
    }

    protected void planGoals(Observation obs)
    {
        if(goals.size() > 0 && goals.get(0).name == PLAN){
            System.out.println("consider goals");

            // consider basket vs cart
            // pickup basket or cart
             addGoal("cart_return", obs.cartReturns[0].position);

            // plan aisle items
            for(Observation.Shelf shelf: obs.shelves){
                System.out.println("consider shelf: "+ shelf.food);
                if(Arrays.asList(obs.players[0].shopping_list).contains(shelf.food)
                    && !inGoals(shelf.food)
                ){
                    addGoal(shelf.food, shelf.position);
                }
            }
            // plan counter items
            for(Observation.Counter counter: obs.counters){
                System.out.println("consider shelf: "+ counter.food);
                if(Arrays.asList(obs.players[0].shopping_list).contains(counter.food)
                    && !inGoals(counter.food)){
                    addGoal(counter.food, counter.position);
                }
            }
            // sort these items
            // sort goals by y,
            // sort goals by x with inverting each aisle alternatingly


            // choose a register
            Observation.Register chosenRegister = obs.registers[0];
            // add a register
            addGoal("register", chosenRegister.position);

            goals.remove(0); // Drop the initial Plan goal
            nop();
        } else {
            System.out.println("already planned goals");
            goEast(); 
            nop();
        }
    }

    protected void addGoal(String name, double[] position){
        System.out.println("added Goal " + name);
        Goal newGoal = new Goal(name, position);
        goals.add(newGoal);
    }

    protected boolean inGoals(String foodItem){
        for(Goal goal: goals){
            if(goal.name.equals(foodItem)){
                   return true;
            }
        }
        return false;
    }

    // movement layer
    protected boolean movement(Observation obs, String goal) 
    {
        // Subsumption layer for movement
        // Stop for walls
        if (obs.players[0].position[0] < 18 == false) {
            goWest();
            nop();
            return false;
        }
        // Baskets: 5
        // Check cart return (1)
        if (obs.eastOf(obs.players[0], obs.cartReturns[0]) && goal != "cart return") {
            goEast(); return true;
        } else if (obs.westOf(obs.players[0], obs.cartReturns[0]) && goal != "cart return") {
            goWest(); return true;
        } else if (obs.northOf(obs.players[0], obs.cartReturns[0]) && goal != "cart return") {
            goSouth(); return true;
        }  else if (obs.northOf(obs.players[0], obs.cartReturns[0]) && goal != "cart return") {
            goNorth(); return true;
        }
        return false;
    }

    // Helper perception function
    // Return objects if interactions possible
    protected boolean objectCheck(Observation obs)
    {
        // Can interact
        if (obs.cartReturns[0].canInteract(obs.players[0]) && 
        obs.players[0].curr_cart != 0) {
            return true;
        } else if (obs.shelves[0].canInteract(obs.players[0])) {
            return true;
        } else if (obs.registers[0].canInteract(obs.players[0])) {
            return true;
        } else if (obs.carts[0].canInteract(obs.players[0])) {
            return true;
        } else if (obs.counters[0].canInteract(obs.players[0])) {
            return true;
        }
        // Cannot interact
        return false;
    }

    // Helper aisle function
    // Return the aisle number that the player is currently in
    protected int getCurrentAisle(Observation obs, int playerIndex) {
        int current = -1;
        for (int i = 0; i < obs.shelves.length; i++) {
            System.out.println("Food at shelf: " + i + " " + obs.shelves[i].food);
            System.out.println("At position: X: " + obs.shelves[i].position[0] + " , Y: " + obs.shelves[i].position[1]);
            //if (obs.belowAisle(playerIndex, i) == obs.aboveAisle(playerIndex, i + 1)) {
                //current = i;
            //}
        }
        //System.out.println("Food at current shelf: " + obs.shelves[current].food);
        return current;
    }

    // Subsumption layer for agent perception (goal-driven)
    protected boolean perception(Observation obs, String goal) 
    {
        // Get original location of player on map
        double anchor_x = obs.players[0].position[0];
        double anchor_y = obs.players[0].position[1];

        // Check if object is interactive
        boolean stop = objectCheck(obs);

        // Check if goal has been found
        boolean target = false;

        // Check north and south for object
        if (obs.players[0].direction == 2 || obs.players[0].direction == 3) {
            while (stop == false) {
                goNorth();
                stop = objectCheck(obs);
            } 
            // Check if goal object perceived
            interactWithObject();
            if (obs.players[0].holding_food == goal) {
                target = true;
            } else {
                cancelInteraction();
            }
            // Check otherside
            while (obs.players[0].position[1] != anchor_y) {
                goSouth();
                stop = objectCheck(obs);
            }
        }
        // Check east and west for object
        if (obs.players[0].direction == 1 || obs.players[0].direction == 4) {
            while (stop == false) {
                goEast();
                stop = objectCheck(obs);
            }
            // Check if goal object perceived
            interactWithObject();
            if (obs.players[0].holding_food == goal) {
                target = true;
            } else {
                cancelInteraction();
            }
            // Check other side
            while (obs.players[0].position[1] != anchor_x) {
                goWest();
                stop = objectCheck(obs);
            }
        }
        // Otherwise
        return target;
    }

    // interaction layer
    protected void interact(Observation obs, String goal) 
    {
        // Subsumption layer for goal interaction
        // Cart return: 1
        // Counters: 2
        // Registers: 3
        // Shelves: 4
        
        // Check all six aisles
        // FIGURE OUT IF WAY TO KNOW # of aisles
        // COMPUTATIONALLY WITHOUT HARDCODING
        if (obs.inAisle(0, 0) || obs.inAisle(0, 1)
        || obs.inAisle(0, 2) || obs.inAisle(0, 3)
        || obs.inAisle(0, 4) || obs.inAisle(0, 5) 
        || obs.inAisle(0, 6)) {
            boolean found = perception(obs, goal);
            if(found) {
                System.out.println("Found goal: " + goal);
            } else {
                System.out.println("Searching for goal: " + goal);
            }
        }
    }

    // subsumption architecture layer
    protected void goalSearch(Observation obs, String goal) 
    {
        // Inhibit and exhibit layers
        movement(obs, goal);
        interact(obs, goal);
    }
    
    // Author-defined execution loop
    // Modifies agent state every 100ms
    @Override
    protected void executionLoop()
    {
        // this is called every 100ms
        // put your code in here
        Observation obs = getLastObservation();
        // System.out.println(obs.players.length + " players");
        // System.out.println(obs.carts.length + " carts");
        // System.out.println(obs.shelves.length + " shelves");
        // System.out.println(obs.counters.length + " counters");
        // System.out.println(obs.registers.length + " registers");
        // System.out.println(obs.cartReturns.length + " cartReturns");


        // print out the shopping list
        // System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
        // call function to grab cart and go north
        // grabCartGoNorth(obs);

        // move agent to specified goal
        String goalLocation = "apples";

        System.out.println("goals len:" + goals.size() + "  shopping len: " + obs.players[0].shopping_list.length);
        // System.out.println("goals" + goals.toString());
        planGoals(obs);
        // System.out.println("after plan");

        // Check which aisle the agent is closest to
        // int current = getCurrentAisle(obs, 0);

        // System.out.println("Player currently by aisle: " + current);
        //goalSearch(obs, goalLocation);
    }
}
