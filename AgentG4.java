// Edited by Hezekiah Branch, Matthew Ebisu, and Michael LoTurco
// Multiplayer Agent Testing and Debugging by Matthew Ebisu

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;
import com.supermarket.*;

public class AgentG4 extends SupermarketComponentImpl
{
    // Class constructor
    public AgentG4()
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
    Boolean has_paid = false;

    double[] drop_location = NOWHERE;
    int drop_direction = -1;
    int my_cart_index = -1;
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
        if ((obs.cartReturns[0].canInteract(obs.players[this.playerIndex])
            || obs.cartReturns[1].canInteract(obs.players[this.playerIndex]))
            && obs.players[this.playerIndex].curr_cart != 0) {
            // nop();
            // toggleShoppingCart();   
            interactWithObject();    //changed from interactWithObject()
            goals.remove(0);
            my_cart_index = obs.players[this.playerIndex].curr_cart;

            // nop();
            return true;
            // Case where agent has obtained a cart
            // and needs to go north
        // } else if (obs.players[this.playerIndex].curr_cart != -1) {
        //     // Prevent endless collisions with an
        //     // infinite loop of no movement here
        //     // * Feels unethical to stop agent
        //     //  movement infinitely like this :( *
        //     // if (obs.players[this.playerIndex].position[1] < 13) {
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
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity, constant space
    // Author: Branch, H.
    protected boolean noCollision(Observation obs) {
        // Set boolean to pass test cases
        boolean success = true;
        // Check for shelf collisions
        for (int i = 0; i < obs.shelves.length; i++) {
            if (obs.shelves[i].canInteract(obs.players[this.playerIndex])) {
                success = false;
                return success;
            }
        }
        // Check for counter collisions
        for (int i = 0; i < obs.counters.length; i++) {
            if (obs.counters[i].canInteract(obs.players[this.playerIndex])) {
                success = false;
                return success;
            }
        }
        // Check for register collisions
        for (int i = 0; i < obs.registers.length; i++) {
            if (obs.registers[i].canInteract(obs.players[this.playerIndex])) {
                success = false;
                return success;
            }
        }
        // Check for cart return collisions
        for (int i = 0; i < obs.cartReturns.length; i++) {
            if (obs.cartReturns[i].canInteract(obs.players[this.playerIndex])) {
                success = false;
                return success;
            }
        }
        // Check for cart collisions
        for (int i = 0; i < obs.carts.length; i++) {
            if (obs.carts[i].canInteract(obs.players[this.playerIndex])) {
                success = false;
                return success;
            }
        }
        return success;
    }

    // Function: agentCollision
    // Purpose: Check for colliding into others agents
    //          during multiplayer mode
    // Input: An observation state (i.e. Observation)
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity O(m), m = # of agents
    //             Constant space complexity, no aux. space used
    // Author: Branch, H and LoTurco, M
    protected boolean agentCollision(Observation obs, String main_agent_direction) {
        // Set boolean to pass test cases
        boolean collision = false;
        // Set threshold for errors 
        double threshold = 1.1;
        double similarity_threshold = .2;
        // Check for collisions with other players
        for (int i = 0; i < obs.players.length; i++) {
            // Specify current agent in program
            int main_agent = this.playerIndex;
            double main_agent_x = obs.players[main_agent].position[0];
            double main_agent_y = obs.players[main_agent].position[1];
            // NORTH is 0, SOUTH is 1, EAST is 2, WEST is 3
            // int main_agent_direction = obs.players[main_agent].direction;
            // Specify other agent(s) to check
            int neighbor = i; 
            if (neighbor != main_agent) { 
                // neighbor = i;
            
                // Grab the other agents (X, Y) coordinates
                double neighbor_agent_x = obs.players[neighbor].position[0];
                double neighbor_agent_y = obs.players[neighbor].position[1];
                // Check for collision from above if within margin
                // and agent heading North
                if (main_agent_direction == "North") {
                    if ((main_agent_x  <  neighbor_agent_x + similarity_threshold &&  main_agent_x  > neighbor_agent_x - similarity_threshold)
                    && neighbor_agent_y < main_agent_y) {
                        if (main_agent_y - neighbor_agent_y < threshold) {
                            collision = true;
                        }
                    }
                }
                // Check for collision from below if within margin
                // and agent heading South
                if (main_agent_direction == "South") {
                    if ((main_agent_x  <  neighbor_agent_x + similarity_threshold &&  main_agent_x  > neighbor_agent_x - similarity_threshold)
                    && neighbor_agent_y > main_agent_y) {
                        if (neighbor_agent_y - main_agent_y < threshold) {
                            collision = true;
                        }
                    }
                }
                // Check for collision from side if within margin
                // and agent heading East
                if (main_agent_direction == "East") {
                    if ((main_agent_y < neighbor_agent_y + similarity_threshold &&  main_agent_y > neighbor_agent_y - similarity_threshold)
                    &&  main_agent_x < neighbor_agent_x) {
                        if (main_agent_x - neighbor_agent_x < threshold) {
                            collision = true;
                        }
                    }
                }
                // Check for collision from side if within margin
                // and agent heading West
                if (main_agent_direction == "West") {
                    if ((main_agent_y < neighbor_agent_y + similarity_threshold &&  main_agent_y > neighbor_agent_y - similarity_threshold)
                    &&  main_agent_x > neighbor_agent_x) {
                        if (neighbor_agent_x - main_agent_x < threshold) {
                            collision = true;
                        }
                    }
                }
            }
        }
       return collision;
    }

    // Function: cartCollision
    // Purpose: Check for colliding into others agent carts
    //          during multiplayer mode
    // Input: An observation state (i.e. Observation)
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity O(m), m = # of carts
    //             Constant space complexity, no aux. space used
    // Author: Branch, H and LoTurco, M.
    protected boolean cartCollision(Observation obs, String main_agent_direction) {
        // Set boolean to pass test cases
        boolean collision = false;
        // collision threshold as how close counts as about to collide
        double collision_threshold = 0.5;
        // similarity threshold used for checking if we are on the 'same' x or y coord (the direction not being collided on)
        double similarity_threshold = .1;
        // Check for collisions with other carts
        for (int i = 0; i < obs.carts.length; i++) {
            // Specify current agent in program
            int main_agent = this.playerIndex;
            double main_agent_x = obs.players[main_agent].position[0];
            double main_agent_y = obs.players[main_agent].position[1];
            // NORTH is 0, SOUTH is 1, EAST is 2, WEST is 3
            // int main_agent_direction = obs.players[main_agent].direction;
            // Specify other agent(s) to check
            int cartindex = -1; 
            if (i != my_cart_index) { 
                cartindex = i;
                
                // Grab the other agents (X, Y) coordinates
                double neighbor_cart_x = obs.players[cartindex].position[0];
                double neighbor_cart_y = obs.players[cartindex].position[1];
                // Check for collision from above if within margin
                // and agent heading North
                if (main_agent_direction == "North") {
                    if (
                        (main_agent_x  <  neighbor_cart_x + similarity_threshold &&  main_agent_x  > neighbor_cart_x - similarity_threshold)
                        && neighbor_cart_y < main_agent_y) {
                        if (main_agent_y - neighbor_cart_y < collision_threshold) {
                            collision = true;
                        }
                    }
                }
                // Check for collision from below if within margin
                // and agent heading South
                if (main_agent_direction == "South") {
                    if ((main_agent_x  <  neighbor_cart_x + similarity_threshold &&  main_agent_x  >   neighbor_cart_x - similarity_threshold)
                    && neighbor_cart_y > main_agent_y) {
                        if (neighbor_cart_y - main_agent_y < collision_threshold) {
                            collision = true;
                        }
                    }
                }
                // Check for collision from side if within margin
                // and agent heading East
                if (main_agent_direction == "East") {
                    if ((main_agent_y < neighbor_cart_y + similarity_threshold &&  main_agent_y > neighbor_cart_y - similarity_threshold)
                    && neighbor_cart_x > main_agent_x) {
                         if (neighbor_cart_x - main_agent_x < collision_threshold) {
                            System.out.println("cart collision noted");
                            collision = true;
                        }
                    }
                }
                // Check for collision from side if within margin
                // and agent heading West
                if (main_agent_direction == "West") {
                    if ((main_agent_y < neighbor_cart_y + similarity_threshold &&  main_agent_y > neighbor_cart_y - similarity_threshold)
                    && main_agent_x > neighbor_cart_x) {
                        if (main_agent_x - neighbor_cart_x < collision_threshold) {
                            collision = true;
                        }
                    }
                }
            }
        }
       return collision;
    }

    // Function: agentCollisionAvoidance
    // Purpose: Avoid colliding into others agents
    //          during multiplayer mode
    // Input: An observation state (i.e. Observation)
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity
    //             Constant space complexity, no aux. space used
    // Author: Branch, H.
    protected void agentCollisionAvoidance(Observation obs) {
        // QuadTree decision list
        // 1 to indicate accessible travel in a direction
        // and -1 to indicate inaccessible path
        int[] travel = new int[]{1, 1, 1, 1}; 
        // Set threshold for errors 
        double threshold = 0.5;
        // Check for collisions with other players
        for (int i = 0; i < obs.players.length; i++) {
            // Specify current agent in program
            int main_agent = 0;
            double main_agent_x = obs.players[main_agent].position[0];
            double main_agent_y = obs.players[main_agent].position[1];
            // NORTH is 0, SOUTH is 1, EAST is 2, WEST is 3
            int main_agent_direction = obs.players[main_agent].direction;
            // Specify other agent(s) to check
            int neighbor = -10000; 
            if (i != main_agent) { 
                neighbor = i;
            }
            // Grab the other agents (X, Y) coordinates
            double neighbor_agent_x = obs.players[neighbor].position[0];
            double neighbor_agent_y = obs.players[neighbor].position[1];
            // Check for collision from above if within margin
            // and agent heading North
            if (main_agent_direction == 0) {
                if (neighbor_agent_x == main_agent_x 
                && neighbor_agent_y < main_agent_y) {
                    if (main_agent_y - neighbor_agent_y < threshold) {
                        travel[0] = -1;
                    }
                }
            }
            // Check for collision from below if within margin
            // and agent heading South
            if (main_agent_direction == 1) {
                if (neighbor_agent_x == main_agent_x 
                && neighbor_agent_y > main_agent_y) {
                    if (neighbor_agent_y - main_agent_y < threshold) {
                        travel[1] = -1;
                    }
                }
            }
            // Check for collision from side if within margin
            // and agent heading East
            if (main_agent_direction == 2) {
                if (neighbor_agent_y == main_agent_y
                && neighbor_agent_x < main_agent_x) {
                    if (main_agent_x - neighbor_agent_x < threshold) {
                        travel[2] = -1;
                    }
                }
            }
            // Check for collision from side if within margin
            // and agent heading West
            if (main_agent_direction == 3) {
                if (neighbor_agent_y == main_agent_y
                && main_agent_x < neighbor_agent_x) {
                    if (neighbor_agent_x - main_agent_x < threshold) {
                        travel[3] = -1;
                    }
                }
            }
            // Check for wall collisions
            // Top and Bottom Walls
            if (obs.players[this.playerIndex].position[1] < 3
            && main_agent_direction == 0) {
                travel[0] = -1;
            }
            if (obs.players[this.playerIndex].position[1] > 23
            && main_agent_direction == 0) {
                travel[1] = -1;
            }
            // Left and Right Walls
            if (obs.players[this.playerIndex].position[0] < 3
            && main_agent_direction == 3) {
                travel[2] = -1;
            }
            if (obs.players[this.playerIndex].position[0] > 18
            && main_agent_direction == 3) {
                travel[3] = -1;
            }
        }
        // Choose new available direction to try
        for (int i = 0; i < travel.length; i++) {
            if (travel[i] == 1) {
                if (i == 0) {
                   goNorth();
                } else if (i == 1) {
                    goSouth();
                } else if (i == 2) {
                    goEast();
                } else if (i == 3) {
                    goWest();
                }
            }
        }
        // End of object avoidance function
    }

    // Function: generalCollisionCheck
    // Purpose: Avoid colliding into others agents
    //          during multiplayer mode and objects
    // Input: An observation state (i.e. Observation)
    // Returns: Boolean
    // Effect(s): External timing
    // Complexity: Linear time complexity O(m + n), m = # of agents
    //                                              n = # of objects
    //             Constant space complexity, no aux. space used
    // Author: Branch, H.
    protected boolean generalCollisionCheck(Observation obs) {
        // No collision detected
        // TODO update this check to handle direction input rather than current direction (to handle turning)
        // if (noCollision(obs) && agentCollision(obs) == false) {
        //     return false;
        // }
        // Collision detected
        return true;
    }

    // Author: LoTurco, M., Branch, H.
    protected boolean withinMarginOfLocation(double[] player_pos, double loc_x, double loc_y, double relative_error){
        if (Math.abs(player_pos[0] - loc_x) < relative_error
            && Math.abs(player_pos[1] - loc_y) < relative_error) {
            return true;
        }
        return false;
    }

    // Author: LoTurco, M.
    protected boolean inAisle(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double margin = .5;
        double offset = 1.5;
        double y_north_bound = goal_y + offset - margin;
        double y_south_bound = goal_y + offset + margin;

        if (curr_y > y_north_bound  &&  curr_y < y_south_bound){
            return true;
        } 
        return false;
    }

    // Author: LoTurco, M.
    protected String moveToShelf(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double x_offset = 1;
        double y_offset = 2;
        if(inAisle(obs, goal)){
            if(curr_x < (goal_x + x_offset)){
                // goEast();
                return "East";
            } else {
                // goWest();
                return "West";
            }
        } else {// We are not in correct aisle
            // If we are in a hub we can move to correct Y
            // System.out.println("moving to shelf " + curr_x + " in hub" + )
            if((obs.inAisleHub(this.playerIndex) && curr_x > 3.8 ) || obs.inRearAisleHub(this.playerIndex)){
                if(curr_y < goal_y + y_offset){
                    // goSouth();
                    return "South";
                } else {
                    // goNorth();
                    return "North";
                }
            } else {
                // Not in an aisle hub and not in correct aisle just go east (towards the back hub)
                // goEast();
                return "East";
            }
        }
    }

    // Author: LoTurco, M.
    protected String moveToCounter(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double x_offset = 1;
        double y_offset = 1;

        if((obs.inAisleHub(this.playerIndex) && curr_x > 4.0 ) || obs.inRearAisleHub(this.playerIndex)){
            if(curr_y < goal_y + y_offset){
                // goSouth();
                return "South";
            } else {
                // goNorth();
                return "North";
            }
        } else {
            // Not in an aisle hub and not in correct aisle just go east (towards the back hub)
            // goEast();
            return "East";
        }
        // }
    }

    // Author: LoTurco, M.
    protected String moveToRegister(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double x_offset = 1;
        double y_offset = 1;

        if(curr_x < 3.6 && curr_x > 3.39 ){
            if(curr_y < goal_y + y_offset){
                // goSouth();
                return "South";
            } else {
                // goNorth();
                return "North";
            }
        } else {
            // Not in an aisle hub and not in correct aisle just go west (towards the front of store)
            if(obs.inRearAisleHub(this.playerIndex) && !obs.aboveAisle(this.playerIndex, 1)){
                // goNorth();
                return "North";
            }
            // goWest();
            return "West";
        }
        // }
    }

    // Author LoTurco, M
    protected String moveToExit(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double x_offset = 1;
        double y_offset = 1;

        if(curr_y < 7.5){
            // goSouth();
            return "South";
        } else {
            // Not in an aisle hub and not in correct aisle just go west (towards the front of store)
            // goWest();
            return "West";
        }
        // }
    }

    // Function: moveToXY
    // Purpose: Move Agent from current location to target location (target_x, target_y)
    // Input: Current Observation, target x, target y
    // Effects: chooses and moves in a direction (unless movement not possible/valid)
    // Returns: if a movement option has been chosen
    // Author: LoTurco, M., Branch, H.
    protected boolean moveToGoal(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double x_offset = 1;
        double y_offset = 2;
        
        String outputDirection = "None";
        // Check if we in 'range' if so return false   This might not be needed if interact comes first?
        if(goal.type.equals("shelf")){
            outputDirection = moveToShelf(obs, goal);
        } else if (goal.type.equals("counter")){
            outputDirection = moveToCounter(obs, goal);
        } else if (goal.type.equals("register")){
            outputDirection = moveToRegister(obs, goal);
        } else if(goal.type.equals("leave")){
            outputDirection = moveToExit(obs, goal);
        }

        if(cartCollision(obs, outputDirection) || agentCollision(obs, outputDirection)){
            // We would love to replace this with an evasive maneuvre instead but that was harder than anticipated.
            nop();
            return true;
        }
        switch (outputDirection) {
            case "North": goNorth();
                    return true;
            case "South": goSouth();
                    return true;
            case "East":  goEast();
                    return true;
            case "West":  goWest();
                    return true;
            default: return false;
        }
    }

    protected void planGoals(Observation obs)
    {
        if(goals.size() > 0 && goals.get(0).name == PLAN){
            goals.remove(0); // Drop the initial Plan goal

            addGoal("cart_return", obs.cartReturns[0].position, "cart_return");
 
         
            // plan aisle items
            for(Observation.Shelf shelf: obs.shelves){
                if(Arrays.asList(obs.players[this.playerIndex].shopping_list).contains(shelf.food)
                    && !inGoals(shelf.food)
                ){
                    addGoal(shelf.food, shelf.position, "shelf");
                }
            }
            // plan counter items
            for(Observation.Counter counter: obs.counters){
                if(Arrays.asList(obs.players[this.playerIndex].shopping_list).contains(counter.food)
                    && !inGoals(counter.food)){
                    addGoal(counter.food, counter.position, "counter");
                }
            }

            // sort goals, comparison of goals for sorting handled by Goal.java comparison. 
            Collections.sort(goals);
           
            // choose a register default to first register
            Observation.Register chosenRegister = obs.registers[0];
            addGoal("register", chosenRegister.position, "register");
            addGoal("leave", NOWHERE, "leave");

        }
    }

    // Note, should only be used to sort goals when only food items (counters and shelves) are in goals
    // Author: LoTurco, M.
    protected void addGoal(String name, double[] position, String type){
        Goal newGoal = new Goal(name, position, type);
        goals.add(newGoal);
    }

    // Checks if a goal has been added for a give shopping list item
    // Author: LoTurco, M.
    protected boolean inGoals(String foodItem){
        for(Goal goal: goals){
            if(goal.name.equals(foodItem)){
                   return true;
            }
        }
        return false;
    }
    

    // Helper aisle function
    // Return the aisle number that the player is currently in
    // Author: Branch, H.
    protected int getCurrentAisle(Observation obs, int playerIndex) {
        int current = -1;
        for (int i = 0; i < obs.shelves.length; i++) {
            // System.out.println("Food at shelf: " + i + " " + obs.shelves[i].food);
            // System.out.println("At position: X: " + obs.shelves[i].position[0] + " , Y: " + obs.shelves[i].position[1]);
            //if (obs.belowAisle(playerIndex, i) == obs.aboveAisle(playerIndex, i + 1)) {
                //current = i;
            //}
        }
        //System.out.println("Food at current shelf: " + obs.shelves[current].food);
        return current;
    }

    // subsumption architecture layer
    // Author: LoTurco, M.
    protected void subsumption(Observation obs) 
    {
        Boolean actionChosen = false;
        // Inhibit and exhibit layers
        planGoals(obs);

        actionChosen = interactWithGoal(obs);
        if(!actionChosen){
            actionChosen = grabCartGoNorth(obs);
        }
        if(!actionChosen){
            actionChosen = moveToGoal(obs, goals.get(0));
        }
    }

    // Purpose: returns true if in the correct location to begin interaction behavior with the goal.
    // Author LoTurco, M
    protected boolean inInteractRange(Observation obs, Goal goal){
        double curr_x = obs.players[this.playerIndex].position[0];
        double curr_y = obs.players[this.playerIndex].position[1];
        double goal_x = goal.position[0];
        double goal_y = goal.position[1];
        double offset = 1;
        double margin = .2;

        if(goal.type.equals("shelf") && 
            curr_x > (goal_x + offset - margin) && 
            curr_x < (goal_x + offset + margin) && 
            inAisle(obs, goal)){
            return true;
        } else if(goal.type.equals("counter") && 
            curr_y > (goal_y + offset - margin) && 
            curr_y < (goal_y + offset + margin)){
            return true;
        } else if(goal.type.equals("register") && 
            curr_y > (goal_y + offset - margin) && 
            curr_y < (goal_y + offset + margin)){
            return true;
        }
        return false;
    }

    // Checks goal type and begins interaction.
    // Author LoTurco, M
    protected boolean interactWithGoal(Observation obs){
        Goal goal = goals.get(0);
        if(inInteractRange(obs, goal)){
            // withinMarginOfLocation(obs.players[this.playerIndex].position, goal.position[0], goal.position[1], 0.4)){
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
        return false;
    }

    // Author LoTurco, M. and Ebisu, M.
    protected boolean interactWithRegister(Observation obs){
        //System.out.println("ready to interact with register");
        //System.out.pritnln(obs.registers.)
        //In it's basic form, interactWithRegister needs do the following:
        //1. toggleShoppingCart()  //Drop shopping cart 
        //2. goNorth()             //Go up to where register is
        //3. interactWithObject()  //interact with object is all that is needed in order to interact with register to pay for all items at once
        //4. returnToXY()          //Return to cart location      
        //5. toggleShoppingCart()  //Repick up Cart     
        //The function assumes that the goals are 
        // System.out.println("starting IWR");
        //MOVEMENT: Agent and Cart need to be right next to counter in order to pay
        Boolean holding_cart = obs.players[this.playerIndex].curr_cart != -1;
        Boolean can_interact_with_register = obs.registers[0].canInteract(obs.players[this.playerIndex]); //register's index is currently hardcoded 
        // Boolean dropped_cart = false;
        if(holding_cart){
            my_cart_index = obs.players[this.playerIndex].curr_cart;
            // Let go of cart and save location
            drop_location[0] = obs.players[this.playerIndex].position[0];
            drop_location[1] = obs.players[this.playerIndex].position[1];
            drop_direction = obs.players[this.playerIndex].direction;            
            toggleShoppingCart();
            // dropped_cart = true;
            return true;
        }
        if(!has_paid){
            if(can_interact_with_register){
                interactWithObject();
                interactWithObject();
                has_paid = true;
                return true;
            } else {
                goWest();
                return true;
            }
        } else {
            if(obs.players[this.playerIndex].position[0] == drop_location[0] 
                && obs.players[this.playerIndex].position[1] == drop_location[1]){
                if(drop_direction == 0){
                    drop_direction = -1;
                    goNorth();
                }
                if(drop_direction == 1){
                    drop_direction = -1;
                    goSouth();
                }
                toggleShoppingCart();
                goals.remove(0);
                return true;
            } else {
                goEast();
                return true;
            }
        }
    }

    // Author LoTurco, M
    protected int findShelf(Observation obs){
        for(int i = 0; i < obs.shelves.length; i++){
            if(obs.shelves[i].food.equals(goals.get(0).name)
                && obs.shelves[i].position[0] == goals.get(0).position[0]
                && obs.shelves[i].position[1] == goals.get(0).position[1]){
                    return i;
            }
        }
        return -1;
    }

    // Author LoTurco, M
    protected boolean interactWithShelf(Observation obs){
        Boolean holding_cart = obs.players[this.playerIndex].curr_cart != -1;
        Boolean holding_food = obs.players[this.playerIndex].holding_food != null;
        int shelf_index = findShelf(obs);
        Boolean can_interact_with_proper_shelf = obs.shelves[shelf_index].canInteract(obs.players[this.playerIndex]);
  
        if(holding_cart){
            my_cart_index = obs.players[this.playerIndex].curr_cart;
            // Let go of cart
            drop_location[0] = obs.players[this.playerIndex].position[0];
            drop_location[1] = obs.players[this.playerIndex].position[1];
            drop_direction = obs.players[this.playerIndex].direction;
            toggleShoppingCart();
            return true;
        }
        // can interact with shelf and not holding food and 
        if( can_interact_with_proper_shelf && !holding_food){
            //  interact with shelf and take food
            interactWithObject();
            return true;
        } else if(!holding_food){
              goNorth();
            return true;
        } else {
            if(obs.players[this.playerIndex].position[0] == drop_location[0] 
                && obs.players[this.playerIndex].position[1] == drop_location[1]){
                if(drop_direction == 2){
                    drop_direction = -1;
                    goEast();
                }
                if(drop_direction == 3){
                    drop_direction = -1;
                    goWest();
                }
                interactWithObject();
                interactWithObject();
                toggleShoppingCart();
                goals.remove(0);
                return true;
                
            }
            else{
                goSouth();
                return true;
            }
        }
    }

    // Author LoTurco, M
    protected int findCounter(Observation obs){
        for(int i = 0; i < obs.counters.length; i++){
            // System.out.println(obs.shelves[i].position[0].equals(goals.get(0).position)+ " " 
            //     + obs.shelves[i].position[0] + " " + obs.shelves[i].position[1]
            //     + goals.get(0).position[0] + " " + goals.get(0).position[1]);
            //+ " " + obs.shelves[i].position.equals(goals.get(0).position));
            if(obs.counters[i].food.equals(goals.get(0).name)
                && obs.counters[i].position[0] == goals.get(0).position[0]
                && obs.counters[i].position[1] == goals.get(0).position[1]){
                    return i;
            }
        }
        return -1;
    }

    // Author LoTurco, M
    protected boolean interactWithCounter(Observation obs){
        // System.out.println("starting IWS");
        Boolean holding_cart = obs.players[this.playerIndex].curr_cart != -1;
        Boolean holding_food = obs.players[this.playerIndex].holding_food != null;
        int counter_index = findCounter(obs);
        Boolean can_interact_with_proper_counter = obs.counters[counter_index].canInteract(obs.players[this.playerIndex]);
        if(holding_cart){
            my_cart_index = obs.players[this.playerIndex].curr_cart;
            // Let go of cart
            drop_location[0] = obs.players[this.playerIndex].position[0];
            drop_location[1] = obs.players[this.playerIndex].position[1];
            drop_direction = obs.players[this.playerIndex].direction;
            toggleShoppingCart();
            return true;
        }
        // can interact with counter and not holding food and 
        if(can_interact_with_proper_counter && !holding_food){
            //  interact with counter and take food
            interactWithObject();
            return true;
        } else if(!holding_food){
            goEast();
            return true;
        } else {
            if(obs.players[this.playerIndex].position[0] == drop_location[0] 
                && obs.players[this.playerIndex].position[1] == drop_location[1]){
                if(drop_direction == 0){
                    drop_direction = -1;
                    goNorth();
                }
                if(drop_direction == 1){
                    drop_direction = -1;
                    goSouth();
                }
                interactWithObject();
                interactWithObject();
                toggleShoppingCart();
                goals.remove(0);
                return true;
                
            }
            else{
                goWest();
                return true;
            }
        }
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
        // System.out.println("Shoppping list: " + Arrays.toString(obs.players[this.playerIndex].shopping_list));
        // call function to grab cart and go north
        // grabCartGoNorth(obs);

        // move agent to specified goal
        // System.out.println("Player " + this.playerIndex + "currently at coordinate (X,Y): (" + obs.players[this.playerIndex].position[0] + ", "  + obs.players[this.playerIndex].position[1] + ").");

        subsumption(obs);
    

        // interactWithShelf(obs);
        firsttime = false;
    }
}