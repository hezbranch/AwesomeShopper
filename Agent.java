import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
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
    int movementPhase = 0;
    
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

    // Function: noCollision
    // Purpose: Avoid colliding into known 
    //          Interactive Objects for movement
    // Input: An observation state (i.e. Observation)
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity, constant space
    // Notes: <Hezekiah> There's probably a FAR better way of 
    //                   doing this but it is what it is
    protected boolean noCollision(Observation obs) {
        // Set boolean to pass test cases
        boolean success = true;
        // Check for shelf collisions
        for (int i = 0; i < obs.shelves.length; i++) {
            if (obs.shelves[i].canInteract(obs.players[0])) {
                success = false;
                return success;
            }
        }
        // Check for counter collisions
        for (int i = 0; i < obs.counters.length; i++) {
            if (obs.counters[i].canInteract(obs.players[0])) {
                success = false;
                return success;
            }
        }
        // Check for register collisions
        for (int i = 0; i < obs.registers.length; i++) {
            if (obs.registers[i].canInteract(obs.players[0])) {
                success = false;
                return success;
            }
        }
        // Check for cart return collisions
        for (int i = 0; i < obs.cartReturns.length; i++) {
            if (obs.cartReturns[i].canInteract(obs.players[0])) {
                success = false;
                return success;
            }
        }
        // Check for cart collisions
        for (int i = 0; i < obs.carts.length; i++) {
            if (obs.carts[i].canInteract(obs.players[0])) {
                success = false;
                return success;
            }
        }
        return success;
    }

    protected boolean goalInteractable(Observation obs) {
        // Set boolean to pass test cases
        boolean success = false;
        // Edge Case where above cart return
        if (obs.atCartReturn(0)) {
            success = true;
            return success;
        }
        // Check for shelf collisions
        for (int i = 0; i < obs.shelves.length; i++) {
            if (obs.shelves[i].canInteract(obs.players[0]) && obs.shelves[i].food == goals.get(0).name) {
                success = true;
                return success;
            }
        }
        // Check for counter collisions
        for (int i = 0; i < obs.counters.length; i++) {
            if (obs.counters[i].canInteract(obs.players[0]) && obs.counters[i].food ==  goals.get(0).name) {
                success = true;
                return success;
            }
        }
        // Check for register collisions
        for (int i = 0; i < obs.registers.length; i++) {
            if (obs.registers[i].canInteract(obs.players[0]) && "register" == goals.get(0).name) {
                success = true;
                return success;
            }
        }
        // Check for cart return collisions
        for (int i = 0; i < obs.cartReturns.length; i++) {
            if (obs.cartReturns[i].canInteract(obs.players[0]) && "cart_return" == goals.get(0).name) {
                success = true;
                return success;
            }
        }
        // Check for cart collisions
        // for (int i = 0; i < obs.carts.length; i++) {
        //     if (obs.carts[i].canInteract(obs.players[0])) {
        //         success = false;
        //         return success;
        //     }
        // }
        return success;
    }

    // Function: returnToLocation
    // Purpose: Move agent from current location to parametized target (X, Y)
    // Input: An observation state (i.e. Observation)
    //        Position X of intended coordinate (X, Y) as type double
    //        Position Y of intended coordinate (X, Y) as type double
    // Returns: None (i.e. void)
    // Effect(s): External timing, assumes agent does NOT have a cart
    protected void returnToLocation(Observation obs, double target_x, double target_y) 
    {
        // Check if agent has arrived at intended position
        double agent_current_x_coord = obs.players[0].position[0]; 
        double agent_current_y_coord = obs.players[0].position[1]; 
        boolean stop = agent_current_x_coord == target_x && agent_current_y_coord == target_y;

        // If no collision possible, start returning to location
        if (stop == false && noCollision(obs) == true) {
            // Move horiztonally toward goal position (2 : east, 3: west)
            if (agent_current_x_coord < target_x) {
                goWest();
            } else if (agent_current_x_coord > target_x) {
                goEast();
            }
            // Move vertically toward goal position (0: north, 1: south)
            if (agent_current_y_coord < target_y) 
            {
                goNorth();
            } else if (agent_current_y_coord > target_y) {
                goSouth();
            }
            // Recur until agent arives at target location
             returnToLocation(obs, target_x, target_y);
        }
        // Case where recursion no longer running
        nop();
    }

    
    // Function: agentInteraction
    // Purpose: Interaction Layer (Subsumption Architecture)
    //          Grab the item (if location has any available).
    //          Add item to cart (or basket if being used).
    //          Mark location as visited (shelf or counter).
    //          Continue following path.
    // Input: An observation state (i.e. Observation)
    //        Position X of intended goal InteractiveObject (shelf, counter, etc.)
    //        Position Y of intended goal InteractiveObject (shelf, counter, etc.)
    // Returns: None (i.e. void)
    // Effect(s): External timing, infinite looping, assumes agent has cart when called
    protected void agentInteraction(Observation obs, double target_x, double target_y)
    {
        // Get the agent's currrent location so we can return
        // back to that spot once we get what we need here
        // and continue along intended path from movement layer
        double agent_start_x = obs.players[0].position[0];
        double agent_start_y = obs.players[0].position[1];

        // Handle interacting with cart for grocery shopping
        if (obs.atCartReturn(0)) {
            // Case where path is cart return and agent has NOT obtained a cart
            // and needs to interact with cart return to get a cart
            // to begin securing items from the shopping list.
            grabCartGoNorth(obs);
            goNorth();
            return;
        } else if (obs.players[0].curr_cart == 0 && obs.atCartReturn(0) == false) {
            // Case where agent has shopping cart in-hand
            // and needs to let it go to move to the shelf
            // and interact to get items off shopping list.

            // Get the agent's cart's currrent location so we can return
            // back to that spot once we get what we need here
            // and continue along intended path from movement layer
            double agent_cart_start_x = obs.carts[obs.players[0].curr_cart].position[0];
            double agent_cart_start_y = obs.carts[obs.players[0].curr_cart].position[1];

            // Unhand the shopping cart
            toggleShoppingCart();

            // Assuming agent is now at the correct shelf/counter/location
            // and facing the shelf, move forward toward the shelf
            goNorth();
            while (obs.players[0].position[1] < target_y) {
                goNorth();
            }

            interactWithObject(); // Now holding the item in hand.

            // Return to cart location at Line 90 & 91 and place item in cart
            returnToLocation(obs, agent_cart_start_x, agent_cart_start_y);
            interactWithObject(); // Put item from hand into cart

            // Return to original start position at Line 84 & 85
            // to facilitate movement layer and resume A* path-finding
            returnToLocation(obs, agent_start_x, agent_start_y);
            toggleShoppingCart(); //  Grab cart again for more item grabs
        }
    }

    protected void planGoals(Observation obs)
    {
        if(goals.size() > 0 && goals.get(0).name == PLAN){
            System.out.println("consider goals");

            goals.remove(0); // Drop the initial Plan goal

            if(obs.players[0].shopping_list.length > 6)
            {
                addGoal("cart_return", obs.cartReturns[0].position);
            }
            // consider basket vs cart
            // pickup basket or cart
         
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

            Collections.sort(goals);
            // sort these items
            // sort goals by y,
            // sort goals by x with inverting each aisle alternatingly

            // choose a register
            Observation.Register chosenRegister = obs.registers[0];
            // add a register
            addGoal("register", chosenRegister.position);

            nop();
        } else {
            System.out.println("already planned goals");
            nop();
        }
    }

    // Note, should only be used to sort goals when only food items (counters and shelves) are in goals
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

    // subsumption architecture layer
    protected void goalSearch(Observation obs, String goal) 
    {
        // Inhibit and exhibit layers
        planGoals(obs);
        if(goalInteractable(obs)){
            agentInteraction(obs, goals.get(0).position[0], goals.get(0).position[1]);
            setMovement(obs);
        }
        else {
            setMovement(obs);
        }
        // movement(obs, goal);
        // interact(obs, goal);
    }

    protected void setMovement(Observation obs){
        if(movementPhase == 0){
            if(obs.atCartReturn(0)){
                movementPhase = 1;
            }
            else {
                goSouth();
            }
        } if (movementPhase == 1) {
            if(obs.inAisleHub(0) && obs.players[0].position[0] > 3.8){
                movementPhase = 2;
                // System.out.println("POSITION" + obs.players[0].position[0]);
            } else { 
                goEast();
            }
        } if(movementPhase == 2) {
            if(obs.belowAisle(0, 6)){
                movementPhase = 3;
            } else { 
                //   System.out.println("POSITION" + obs.players[0].position[0])
                goSouth();
            }
        } if(movementPhase == 3) {
            if(obs.inRearAisleHub(0)){
                movementPhase = 4;
            } else { 
                goEast();
            }
        }
          if(movementPhase == 4) {
            if(obs.belowAisle(0, 5) ){
                goNorth();
            } else { 
                movementPhase = 5;
            }
        } if(movementPhase == 5) {
            if(!obs.inAisleHub(0)){
                goWest();
            }
            else{
                movementPhase = 8;
            }
        } if(movementPhase == 5) {
            if(!obs.inAisleHub(0)){
                goWest();
            }
            else{
                movementPhase = 8;
            }
        }


        // if(movementPhase == 4) {
        //     if(obs.belowAisle(0, 4)){
        //         goNorth();
        //     } else { 
        //        movementPhase = 5;
        //     }
        // }
        // if(movementPhase == 5) {
        //     if(obs.inAisleHub(0)){
        //         movementPhase = 5;
        //     } else { 
        //         goWest();
        //     }
        
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

        if (obs.atCartReturn(0)) {
            System.out.println("GEREFEFFEFEFFEFEFEFEFE");
        }


        // print out the shopping list
        // System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
        // call function to grab cart and go north
        // grabCartGoNorth(obs);

        // move agent to specified goal

        String goalLocation = "apples";

        System.out.println("goals len:" + goals.size() + "  shopping len: " + obs.players[0].shopping_list.length);
        // planGoals(obs);


        for(Goal goal: goals){
            System.out.println("goals are:" + goal.name);
        }
        
        System.out.println("cart Returns are:" + obs.cartReturns.length);
        // System.out.println("basket? Returns are:" + obs.basketReturn.length);

        shouldRunExecutionLoop = false;

        boolean actionChosen = false;

        // Check which aisle the agent is closest to
        // int current = getCurrentAisle(obs, 0);

        // System.out.println("Player currently by aisle: " + current);
        // actionChosen = 
        goalSearch(obs, goalLocation);
        // if(!actionChosen){
        //     actionChosen = interactWithStuff(obs);
        // }
        // if(!actionChosen){
        //     actionChosen = moveAround(obs);
        // }
    }
}
