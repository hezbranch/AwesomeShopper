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
        Goal plan = new Goal(PLAN, NOWHERE, PLAN);
        goals.add(plan);
    }
    // Goal States
    static String PLAN = "plan";
    static double[] NOWHERE = {-1, -1};
    double[] drop_location = NOWHERE;
    int drop_direction = -1;
    int my_cart_index = -1;
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
    protected Boolean grabCartGoNorth(Observation obs)
    {
        // Stop agent from going south and prevent
        // a possible collision due to cases where:
        // 1. Player can interact with cart
        // 2. Player does not have a cart
        // Effect(s): If #2 is not checked, endless #1.
        //            Executed every 100ms externally.
        if(!goals.get(0).name.equals("cart_return")){
            return false;
        }
        if (obs.cartReturns[0].canInteract(obs.players[0]) && obs.players[0].curr_cart != 0) {
            // nop();
            // toggleShoppingCart();   
            interactWithObject();    //changed from interactWithObject()
            goals.remove(0);
            my_cart_index = obs.players[0].curr_cart;

            // nop();
            return true;
            // Case where agent has obtained a cart
            // and needs to go north
        // } else if (obs.players[0].curr_cart != -1) {
        //     // Prevent endless collisions with an
        //     // infinite loop of no movement here
        //     // * Feels unethical to stop agent
        //     //  movement infinitely like this :( *
        //     // if (obs.players[0].position[1] < 13) {
        //     //     while (true) {
        //     //         nop(); // Stop agent movement
        //     //     }
        //     // }
        //     // // Go up to the register
        //     // goNorth();
        //     return false;
        //     // Case where agent has no cart
        //     // Starting point of agent movement
        } else {
            // Go down to the cart area
            goSouth();
            return true;
        }
    }

    // Function: noCollision
    // Purpose: Avoid colliding into known 
    //          Interactive Objects for movement
    // Input: An observation state (i.e. Observation)
    // Returns: Integer if collision imminent (user's current direction),
    // otherwise returns -1 if no collision detected.
    // Effect(s): External timing, no movement involved
    // Complexity: Linear time complexity, constant space
    // Notes: <Hezekiah> There's probably a FAR better way of 
    //                   doing this but it is what it is
    // Author: Branch, H.
    protected int noCollision(Observation obs) {
        // Set boolean to pass test cases
        int success = -1;
        double tail = 0.0; // Upper Bound of Distance to Potential Collision
        double lower = 0.0; // Lower Bound of Distance to Potential Collision
        // Check for counter collisions
        for (int i = 0; i < obs.counters.length; i++) {
            if ( obs.players[0].collision(obs.counters[i], obs.counters[i].position[0], obs.counters[i].position[1] - tail) 
            || obs.players[0].collision(obs.counters[i], obs.counters[i].position[0], obs.counters[i].position[1] + lower)) {
                System.out.println("COLLISION FUNCTION WORKS on Y AS EXPECTED!!!");
                return obs.players[0].direction;
            }
            if ( obs.players[0].collision(obs.counters[i], obs.counters[i].position[0] - tail, obs.counters[i].position[1]) 
            || obs.players[0].collision(obs.counters[i], obs.counters[i].position[0] + lower, obs.counters[i].position[1])) {
                System.out.println("COLLISION FUNCTION WORKS on X AS EXPECTED!!!");
                return obs.players[0].direction;
            }
        }
        // Check for register collisions
        for (int i = 0; i < obs.registers.length; i++) {
            if ( obs.players[0].collision(obs.registers[i], obs.registers[i].position[0], obs.registers[i].position[1] - tail) 
            || obs.players[0].collision(obs.registers[i], obs.registers[i].position[0], obs.registers[i].position[1] + lower)) {
                System.out.println("COLLISION FUNCTION WORKS on Y AS EXPECTED!!!");
                return obs.players[0].direction;
            }
            if ( obs.players[0].collision(obs.registers[i], obs.registers[i].position[0] + lower, obs.registers[i].position[1]) 
            || obs.players[0].collision(obs.registers[i], obs.registers[i].position[0] - tail, obs.registers[i].position[1])) {
                System.out.println("COLLISION FUNCTION WORKS on X AS EXPECTED!!!");
                return obs.players[0].direction;
            }
        }
        // Check for cart return collisions
        for (int i = 0; i < obs.cartReturns.length; i++) {
            if ( obs.players[0].collision(obs.cartReturns[i], obs.cartReturns[i].position[0], obs.cartReturns[i].position[1] - tail) 
            || obs.players[0].collision(obs.cartReturns[i], obs.cartReturns[i].position[0], obs.cartReturns[i].position[1] + lower)) {
                System.out.println("COLLISION FUNCTION WORKS on Y AS EXPECTED!!!");
                return obs.players[0].direction;
            }
            if ( obs.players[0].collision(obs.cartReturns[i], obs.cartReturns[i].position[0] + lower, obs.cartReturns[i].position[1]) 
            || obs.players[0].collision(obs.cartReturns[i], obs.cartReturns[i].position[0] - tail, obs.cartReturns[i].position[1])) {
                System.out.println("COLLISION FUNCTION WORKS on X AS EXPECTED!!!");
                return obs.players[0].direction;
            }
        }
        // Check for cart collisions
        for (int i = 0; i < obs.carts.length; i++) {
            if ( obs.players[0].collision(obs.carts[i], obs.carts[i].position[0], obs.carts[i].position[1] - tail) 
            || obs.players[0].collision(obs.carts[i], obs.shelves[i].position[0], obs.carts[i].position[1] + lower)) {
                System.out.println("COLLISION FUNCTION WORKS on Y AS EXPECTED!!!");
                return obs.players[0].direction;
            }
            if ( obs.players[0].collision(obs.carts[i], obs.carts[i].position[0] + lower, obs.carts[i].position[1]) 
            || obs.players[0].collision(obs.carts[i], obs.carts[i].position[0] - tail, obs.carts[i].position[1])) {
                System.out.println("COLLISION FUNCTION WORKS on X AS EXPECTED!!!");
                return obs.players[0].direction;
            }
        }
        // Check for shelf collisions
        for (int i = 0; i < obs.shelves.length; i++) {
            if ( obs.players[0].collision(obs.shelves[i], obs.shelves[i].position[0], obs.shelves[i].position[1] - 1) 
            || obs.players[0].collision(obs.shelves[i], obs.shelves[i].position[0], obs.shelves[i].position[1] + 1)) {
                System.out.println("COLLISION FUNCTION WORKS on Y AS EXPECTED!!!");
                return obs.players[0].direction;
            }
            if ( obs.players[0].collision(obs.shelves[i], obs.shelves[i].position[0] + 1, obs.shelves[i].position[1]) 
            || obs.players[0].collision(obs.shelves[i], obs.shelves[i].position[0] - 1, obs.shelves[i].position[1])) {
                System.out.println("COLLISION FUNCTION WORKS on X AS EXPECTED!!!");
                return obs.players[0].direction;
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
          for (int i = 0; i < obs.carts.length; i++) {
            if (obs.carts[i].canInteract(obs.players[0]) && "cart_return" == goals.get(0).name) {
                success = true;
                return success;
            }
        }
        return success;
    }

    protected boolean withinMarginOfLocation(double[] player_pos, double loc_x, double loc_y){
        final double relative_error = 0.2;

        if (Math.abs(player_pos[0] - loc_x) < relative_error
            && Math.abs(player_pos[1] - loc_y) < relative_error) {
            System.out.println("Agent within margin of target location (X, Y): " 
                              + "(" + loc_x  + ", " + loc_y + ").");
            return true;
        }
        return false;
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
        final double x_lower_bound = 3.9;
        final double x_upper_bound = 15;

        // Collision check to avoid collisions
        // If about go collide, go in the opposite direction
        int collision_check = noCollision(obs);
        if (collision_check != -1) {
            nop();
            goNorth();
            return false;
        }

        // Check if agent has arrived at intended position
        // if (Math.abs(agent_current_x_coord - target_x) < relative_error
        // && Math.abs(agent_current_y_coord - target_y) < relative_error) {
        if (withinMarginOfLocation(obs.players[0].position, target_x, target_y)){
            nop();
            System.out.println("Agent returned to target location (X, Y): " 
                              + "(" + target_x  + ", " + target_y + ").");
            return true;
        }

        // Otherwise, return agent to location
        if (agent_current_x_coord < x_lower_bound) {
            System.out.println("In edgecase");
            goEast(); 
            //    goEast();
            //    goNorth();
            return false;
        }

        // Move vertically toward target coordinate
        // Check if any obstacles are blocking path on Y-axis
        double y_stop = Math.abs(agent_current_y_coord - target_y);

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

            // if(obs.players[0].shopping_list.length > 6)
            // {
            addGoal("cart_return", obs.cartReturns[0].position, "cart_return");
            // }
            // consider basket vs cart
            // pickup basket or cart
         
            // plan aisle items
            for(Observation.Shelf shelf: obs.shelves){
                System.out.println("consider shelf: "+ shelf.food);
                if(Arrays.asList(obs.players[0].shopping_list).contains(shelf.food)
                    && !inGoals(shelf.food)
                ){
                    addGoal(shelf.food, shelf.position, "shelf");
                }
            }
            // plan counter items
            for(Observation.Counter counter: obs.counters){
                System.out.println("consider shelf: "+ counter.food);
                if(Arrays.asList(obs.players[0].shopping_list).contains(counter.food)
                    && !inGoals(counter.food)){
                    addGoal(counter.food, counter.position, "counter");
                }
            }

            Collections.sort(goals);
            // sort these items
            // sort goals by y,
            // sort goals by x with inverting each aisle alternatingly

            // choose a register
            Observation.Register chosenRegister = obs.registers[0];
            // add a register
            addGoal("register", chosenRegister.position, "register");

            // nop();
        }
        //  else {
        //     // System.out.println("already planned goals");
        //     nop();
        // }
    }

    // Note, should only be used to sort goals when only food items (counters and shelves) are in goals
    protected void addGoal(String name, double[] position, String type){
        System.out.println("added Goal " + name);
        Goal newGoal = new Goal(name, position, type);
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
    protected void subsumption(Observation obs) 
    {
        Boolean actionChosen = false;
        // Inhibit and exhibit layers
        planGoals(obs);
        System.out.println("Current Goal: " + goals.get(0).name + " " + goals.get(0).position[0] + " " + goals.get(0).position[1]);


        actionChosen = interactWithGoal(obs);
        if(!actionChosen){
            System.out.println("Grabbing cart going north");
            actionChosen = grabCartGoNorth(obs);
        }
        if(!actionChosen){
            System.out.println("Return to xy " + goals.get(0).name + " " + goals.get(0).position[0] + " " + goals.get(0).position[1]);
            actionChosen = returnToXY(obs, goals.get(0).position[0], goals.get(0).position[1]);
        }
    }


    protected boolean interactWithGoal(Observation obs){
        Goal goal = goals.get(0);
        if(withinMarginOfLocation(obs.players[0].position, goal.position[0], goal.position[1])){
            System.out.println("Interacting with goal");
            if(goal.type.equals("shelf")){
                return interactWithShelf(obs);
            }
            if(goal.type.equals("counter")){
                return interactWithCounter(obs);
            }
            if(goal.type.equals("register")){
                return interactWithRegister(obs);
            }
        }
        System.out.println("Not interacting with goal");
        return false;
    }

    protected boolean interactWithRegister(Observation obs){
        System.out.println("ready to interact with register");
        return false;
    }

    protected int findShelf(Observation obs){
        for(int i = 0; i < obs.shelves.length; i++){
            System.out.println("checking shelf i" + i + " " + obs.shelves[i].food.equals(goals.get(0).name));
            // System.out.println(obs.shelves[i].position[0].equals(goals.get(0).position)+ " " 
            //     + obs.shelves[i].position[0] + " " + obs.shelves[i].position[1]
            //     + goals.get(0).position[0] + " " + goals.get(0).position[1]);
            //+ " " + obs.shelves[i].position.equals(goals.get(0).position));
            if(obs.shelves[i].food.equals(goals.get(0).name)
                && obs.shelves[i].position[0] == goals.get(0).position[0]
                && obs.shelves[i].position[1] == goals.get(0).position[1]){
                    System.out.println("found i " + i);
                    return i;
            }
        }
        System.out.println("shelf not found");
        return -1;
    }

    protected boolean interactWithShelf(Observation obs){
        // System.out.println("starting IWS");
        Boolean holding_cart = obs.players[0].curr_cart != -1;
        Boolean holding_food = obs.players[0].holding_food != null;
        int shelf_index = findShelf(obs);
        Boolean can_interact_with_proper_shelf = obs.shelves[shelf_index].canInteract(obs.players[0]);
  
        // System.out.println("IWS cart: " + holding_cart + "  food: " + holding_food + "  can inter: "+ can_interact_with_proper_shelf);
        if(holding_cart){
            // System.out.println("toggle " + obs.players[0].curr_cart);
            my_cart_index = obs.players[0].curr_cart;
            // Let go of cart
            drop_location[0] = obs.players[0].position[0];
            drop_location[1] = obs.players[0].position[1];
            drop_direction = obs.players[0].direction;
            toggleShoppingCart();
            return true;
        }
        // can interact with shelf and not holding food and 
        if( can_interact_with_proper_shelf && !holding_food){
            //  System.out.println("toggle");
            //  interact with shelf and take food
            interactWithObject();
            return true;
        } else if(!holding_food){
            //   System.out.println("not holding food");
              goNorth();
            // returnToXY(obs, obs.shelves[shelf_index].position[0], obs.shelves[shelf_index].position[1]);
            return true;
        } else {
            // System.out.println("holding food");
            if(obs.players[0].position[0] == drop_location[0] 
                && obs.players[0].position[1] == drop_location[1]){
                if(drop_direction == 2){
                    drop_direction = -1;
                    goEast();
                }
                if(drop_direction == 3){
                    drop_direction = -1;
                    goWest();
                }
                // System.out.println("   interact");
                interactWithObject();
                interactWithObject();
                // System.out.println("   toggle");
                toggleShoppingCart();
                goals.remove(0);
                return true;
                
            }
            else{
                // System.out.println("holding food going to cart");
                goSouth();
                return true;
            }
        }
    }

    protected int findCounter(Observation obs){
        for(int i = 0; i < obs.counters.length; i++){
            System.out.println("checking counter i" + i + " " + obs.shelves[i].food.equals(goals.get(0).name));
            // System.out.println(obs.shelves[i].position[0].equals(goals.get(0).position)+ " " 
            //     + obs.shelves[i].position[0] + " " + obs.shelves[i].position[1]
            //     + goals.get(0).position[0] + " " + goals.get(0).position[1]);
            //+ " " + obs.shelves[i].position.equals(goals.get(0).position));
            if(obs.shelves[i].food.equals(goals.get(0).name)
                && obs.shelves[i].position[0] == goals.get(0).position[0]
                && obs.shelves[i].position[1] == goals.get(0).position[1]){
                    System.out.println("found i " + i);
                    return i;
            }
        }
        System.out.println("didnt find");
        return -1;
    }

    protected boolean interactWithCounter(Observation obs){
        // System.out.println("starting IWS");
        Boolean holding_cart = obs.players[0].curr_cart != -1;
        Boolean holding_food = obs.players[0].holding_food != null;
        int counter_index = findCounter(obs);
        Boolean can_interact_with_proper_counter = obs.counters[counter_index].canInteract(obs.players[0]);
  
        // System.out.println("IWS cart: " + holding_cart + "  food: " + holding_food + "  can inter: "+ can_interact_with_proper_shelf);
        if(holding_cart){
            // System.out.println("toggle " + obs.players[0].curr_cart);
            my_cart_index = obs.players[0].curr_cart;
            // Let go of cart
            drop_location[0] = obs.players[0].position[0];
            drop_location[1] = obs.players[0].position[1];
            drop_direction = obs.players[0].direction;
            toggleShoppingCart();
            return true;
        }
        // can interact with shelf and not holding food and 
        if( can_interact_with_proper_counter && !holding_food){
            //  System.out.println("toggle");
            //  interact with shelf and take food
            interactWithObject();
            return true;
        } else if(!holding_food){
            //   System.out.println("not holding food");
              goNorth();
            // returnToXY(obs, obs.shelves[shelf_index].position[0], obs.shelves[shelf_index].position[1]);
            return true;
        } else {
            // System.out.println("holding food");
            if(obs.players[0].position[0] == drop_location[0] 
                && obs.players[0].position[1] == drop_location[1]){
                if(drop_direction == 0){
                    drop_direction = -1;
                    goNorth();
                }
                if(drop_direction == 1){
                    drop_direction = -1;
                    goSouth();
                }
                // System.out.println("   interact");
                interactWithObject();
                interactWithObject();
                // System.out.println("   toggle");
                toggleShoppingCart();
                goals.remove(0);
                return true;
                
            }
            else{
                // System.out.println("holding food going to cart");
                goSouth();
                // returnToXY(obs, drop_location[0], drop_location[1]);
                return true;
            }
        }
        //      
     
        // if holding food 
        //    if at cart
        //        
                // toggleShoppingCart()
                // return true
        //     else not at cart
        //        returnToXY(drop location)
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
        System.out.println("Player currently at coordinate (X,Y): (" + obs.players[0].position[0] + ", "  + obs.players[0].position[1] + ").");
        // grabCartGoNorth(obs)
        // returnToXY(obs, 22.0, 6.0);
        // if(firsttime){
        //     planGoals(obs);
        //     System.out.println(goals.get(1).name+" "+goals.get(1).position[0]+ " " + goals.get(1).position[1]);
        // }
        // if(firsttime){
        //     goals.remove(0);
        //     double[] shelf_test_pos = {5.5, 21.5};
        //     Goal shelf_test = new Goal("avocado", shelf_test_pos);
        //     goals.add(shelf_test);
        //     System.out.println("topgoal"+ goals.get(0).name);
        // }
        subsumption(obs);
        String collision_check = "";
        if (noCollision(obs) == -1) {
            collision_check += "No collision detected.";
        } else {
            collision_check += "Collision detected.";
        }
        System.out.println("COLLISION CHECK? " + collision_check);
    

        // interactWithShelf(obs);
        firsttime = false;
        //giveMeXY(obs);

       //payAtRegister(obs);
       //cartTheftNorm(obs);

        //System.out.println("Player currently at coordinate (X,Y): (" + obs.players[0].position[0] + ", "  + obs.players[0].position[1] + ").");


        //System.out.println("Player currently at coordinate (X,Y): (" + obs.players[0].position[0] + ", "  + obs.players[0].position[1] + ").");

    }
}
