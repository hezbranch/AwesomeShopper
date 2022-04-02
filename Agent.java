import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;
import com.supermarket.*;


// Edited by Hezekiah Branch, Matthew Ebisu, and Michael LoTurco

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

    //Function PayForItems
    //In its most basic form, the only thing you need to do after collecting all your items
    //is [ interactWithObject() ].  As long as you have all the items inside of your 
    //cart at the time, you can pay for all your itmes at once.
    //Pre-Requisites: 
        //Player must be facing the counter at the time
        //Player must have previously dropped his cart
        //Player needs  to have cart dropped off
    //Notes;
        //Player can pay for all items at once
        //Player just needs to be at counter in order to pay
        //Player will also end up paying for items that aren't on the shopping list
        //How do I execute one program after another?  Should I have a global flag for checking if items are paidFor
            //inList, atRegister, etc.(?)
    
    //first function --> Stop the player at specific (x,y) cooridate on map near register
    //second function --> Check that we currently have everything on our shopping list
    //Third function --> Record last known location (with shopping cart) and toggle shopping cart
    //Fourth function --> Pay for items at register (InteractWithObject)
    //Fifth function --> walk back down to cart and grab it.

    //Do I have all of my items?  If so, go to the register.
    public void GotAllItems(Observation obs) {
        //compare the items on the shopping list to items on the arraylist
        //check that alll items exist within the array --> for loop?  string contains?
        //return a True statement if it does, return a false statement if it doesn't
        //use a variable called AllIn
    }

    //Send Agent to the register with cart in hand
    public void goToRegisters(Observation obs){    
        //go to specific (xy) coordinate near register
        //Use Michael's script for recording last known location
            //toggle shopping cart
        //GoNorth towardsregister 
        //return cart last location (x, y)
        //call payForItems()
    }

    public void payForItems(Observation obs){
        //Agent interacts with register, pays for items 
        //interactWithObject()
    }
    public void leaveWithCart (Observation obs){
        //agent then returns to (xy) coordinate of cart
        //agent grabs cart with toggleShoppingCart()
        //agent leaves through exit
    }
    

    // Function: grabCartGoNorth
    // Purpose: Move agent to cart area and bring
    //          it up back north to register area
    // Input: An observation state (i.e. Observation)
    // Returns: None (i.e. void)
    // Effect(s): External timing, infinite looping
    // Author: Branch, H.
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
            toggleShoppingCart();       //changed from interactWithObject()
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
    // Author: Branch, H.
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

    //Function PayForItems
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

    // Function: returnToXY
    // Purpose: Move agent from current location to parametized target (X, Y)
    // Input: An observation state (i.e. Observation)
    //        Position X of intended coordinate (X, Y) as type double
    //        Position Y of intended coordinate (X, Y) as type double
    // Returns: Boolean
    // Effect(s): External timing, assumes agent does NOT have a cart
    // Author: Branch, H.
    protected boolean returnToXY(Observation obs, double target_x, double target_y) 
    {
        // Initialze starter variables for movement
        double agent_current_x_coord = obs.players[0].position[0]; 
        double agent_current_y_coord = obs.players[0].position[1]; 
        final double relative_error = 0.2;
        final double x_lower_bound = 4.3;
        final double x_upper_bound = 15;

        // Check if agent has arrived at intended position
        if (Math.abs(agent_current_x_coord - target_x) < relative_error
        && Math.abs(agent_current_y_coord - target_y) < relative_error) {
            nop();
            System.out.println("Agent returned to target location (X, Y): " 
                              + "(" + target_x  + ", " + target_y + ").");
            return true;
        }

        // Otherwise, return agent to location
        //Matt's Edit: This executes first in the program.  
        //I changed part of it so that it favors 
        /*
        if (agent_current_x_coord < x_lower_bound){
            goEast();
            goEast();
            goNorth();
            returnFalse();
        }
        */
        if (agent_current_x_coord < x_lower_bound) {
            if (obs.players[0].curr_cart<0){
               goEast(); 
               goEast();
               goNorth();
               System.out.println("Agent's (X, Y) value: "               //DELETE THIS 
                              + "(" + target_x  + ", " + target_y + ").");
            }
            return false;
        }

        // Move vertically toward target coordinate
        // Check if any obstacles are blocking path on Y-axis
        double y_stop = Math.abs(agent_current_y_coord - target_y);
        System.out.println("Agent's (X, Y) value: "                     //DELETE THIS
                              + "(" + target_x  + ", " + target_y + ").");

        // Orient agent to face the correct Y-axis direction before moving
        if (agent_current_y_coord > target_y && obs.players[0].direction == 1) {
            goNorth(); return false;
        } else if (agent_current_y_coord < target_y && obs.players[0].direction == 0) {
            goSouth(); return false;
        }

        // If no collision possible in the Y-axis, start returning to location
        // Move vertically toward goal position (0: north, 1: south)
        if (y_stop > relative_error && obs.players[0].direction == 3) {
            // Ensure movement adjustment doesn't run endlessly
            // by bringing agent to the outside LEFT of the aisle
            if (agent_current_x_coord > x_lower_bound) {
                goWest();
            }
        } else if (y_stop > relative_error && obs.players[0].direction == 4) {
            // Ensure movement adjustment doesn't run endlessly
            // by bringing agent to the outside RIGHT of the aisle
            if (agent_current_x_coord < x_upper_bound) {
                goEast();
            }
        } else if (y_stop > relative_error && obs.players[0].direction == 0){
            // Send agent north toward goal if goal is in the north direction
            goNorth();
        } else if (y_stop > relative_error && obs.players[0].direction == 1) {
            // Send agent south toward goal if goal is in the south direction
            goSouth();
        } else {
            // If object between current position and vertical position of target,
            // (e.g. aisle or cart blocking path to target coordinate)
            // move to open area, move to Y target, and adjust on X
            double x_stop = Math.abs(obs.players[0].position[0] - target_x);

            // If no collision possible on the X-axis, start returning to location
            if (x_stop > relative_error) {
                // Move horiztonally toward goal position (2 : east, 3: west)
                System.out.println("Distance Error: " + x_stop);
                if (agent_current_x_coord < target_x) {
                    goEast();
                } else if (agent_current_x_coord > target_x) {
                    goWest();
                }
            }
        }
        // Will return false if agent not currently at target location
        // and 'return false' is at the end of the function call
        // since this function is being called in a time loop
        // and we want to return to multiple locations in a queue.
        System.out.println("Agent still traveling to target location.");
        return false;   
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
    // Author: Branch, H.
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

            goals.remove(0); // Remove goal from list

            // Return to cart location at Line 90 & 91 and place item in cart
            returnToXY(obs, target_x, target_y);
            interactWithObject(); // Put item from hand into cart

            // Return to original start position at Line 84 & 85
            // to facilitate movement layer and resume A* path-finding
            returnToXY(obs, agent_start_x, agent_start_y);
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
    // Author: Branch, H.
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
                movementPhase = 6;
            }
        } if(movementPhase == 6) {
            if(obs.belowAisle(0, 4)){
                goNorth();
            }
            else{
                movementPhase = 7;
            }
        } if(movementPhase == 7) {
            if(obs.players[0].position[0] >= 17.5){
                // obs.besideCounters(0)){
                movementPhase = 8;
            } else { 
                goEast();
            }
        }
          if(movementPhase == 8) {
            if(obs.belowAisle(0, 3) ){
                goNorth();
            } else { 
                movementPhase = 9;
            }
        } if(movementPhase == 9) {
            if(!obs.inAisleHub(0)){
                goWest();
            }
            else{
                movementPhase = 10;
            }
        } if(movementPhase == 10) {
            if(obs.belowAisle(0, 2)){
                goNorth();
            }
            else{
                movementPhase = 11;
            }
        } if(movementPhase == 11) {
            if(obs.players[0].position[0] >= 17.5){
                // obs.besideCounters(0)
                movementPhase = 12;
            } else { 
                goEast();
            }
        }
          if(movementPhase == 12) {
            if(obs.belowAisle(0, 1) ){
                goNorth();
            } else { 
                movementPhase = 13;
            }
        } if(movementPhase == 13) {
            if(obs.players[0].position[0] > 3.5){
                // !obs.inAisleHub(0)
                goWest();
            }
            else{
                movementPhase = 14;
            }
        } if(movementPhase == 14) {
            if(obs.players[0].position[1] < 7.5){
                goSouth();
            }
            else{
                movementPhase = 15;
            }
        } 
        if(movementPhase == 15) {
            // if(!obs.inRearAisleHub(0)){
            goWest();
            // }
            // else{
            //    movementPhase = 16;
            // }
        }  
        // if(movementPhase == 8) {
        //     if(obs.belowAisle(0, 4)){
        //         goNorth();
        //     }
        //     else{
        //         movementPhase = 9;
        //     }
        // } if(movementPhase == 9) {
        //     if(!obs.inRearAisleHub(0)){
        //         goEast();
        //     }
        //     else{
        //        movementPhase = 10;
        //     }
        // }   


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
        // print out the shopping list
        // System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
        // call function to grab cart and go north
        // grabCartGoNorth(obs);

        // move agent to specified goal
        //System.out.println("Player currently at coordinate (X,Y): (" + obs.players[0].position[0] + ", "  + obs.players[0].position[1] + ").");

        returnToXY(obs, 11.05, 4.6);
    }
}
