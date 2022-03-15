import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayUtils;
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
    static int STEP_LENGTH = .15;
    
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

    protected void movement2(Observation obs){
        String[] a_star_result = A_Star(obs.players[0].position, goals[0], obs);
    }

    protected List<String> reconstruct_path(Node node){
        Node curr = 
        List<String> steps = new ArrayList<String>();
        String parentDirection = node.parentDirection;

        while(parentDirection != null){
            steps.add(parentDirection);
            parentDirection = 
        }
        if(node.parentToThisDir == null){
            return []
        }
        return 

    }
    total_path := {current}
    while current in cameFrom.Keys:
        current := cameFrom[current]
        total_path.prepend(current)
    return total_path

    protected double euclideanDistance(double[] a, double[] b){
        return Math.sqrt(Math.square(a[0] - b[0]) + Math.square(a[1]- b[1]));
    }

    protected boolean wouldCollide(Node node, Observation obs){
        Observation.InteractiveObject[] allObs = ArrayUtils.addAll(obs.shelves, obs.registers, obs.counters, obs.cartReturns);
        for(Observation.InteractiveObject ob: allObs){
            if(ob.collision(node.position[0], node.position[1])){
                return true;
            }
        }
        return false
    }

    protected double hWithCollision(Node node, Goal goal, Observation obs){
        if (wouldCollide(node, obs) > 0){
            return Double.POSITIVE_INFINITY;
        }
        // Infinity
        return euclideanDistance(node.position, goal.position);
    }

    protected Node findOrCreateNeighbor(double[] position, ArrayList<Node> allNodes){
        for(Node node: allNodes){
            if(node.position.equals(position)){
                return node;
            }
        }
        Node newNeighbor = Node(position);
        allNodes.append(newNeighbor);
        return newNeighbor;
    }

    protected boolean atGoal(Node node, Goal goal, Observation obs){
        Observation.InteractiveObject[] allObs = ArrayUtils.addAll(obs.shelves, obs.registers, obs.counters, obs.cartReturns);
        Observation.InteractiveObject goalOb;
        for(Observation.InteractiveObject  object : allObs){
            if(object.position = goal.position){
                goalOb = object;
            }
        }
        // figure out if we are within interactive range of the goal 
        // System.out.println()
        if(Math.abs(node.position[1] - goalOb.position[1]) < 1){
            System.out.println('y close ');
            if(Math.abs(node.position[0] - goalOb.position[0]) < 1){
                System.out.println('x close ');
                return true;
            }
        }

        return false
    }

    String[] directions = {"east", "west", "north", "south"};
    protected neighborInDirection(Node current, String direction, ArrayList<Node> allNodes){
        if(direction == directions[0]) {
            double[] newPosition = {current.position[0] + STEP_LENGTH, current.position[1]}
            return findOrCreateNeighbor(newPosition, allNodes)
        }
        if(direction == directions[1]) {
            double[] newPosition = {current.position[0] - STEP_LENGTH, current.position[1]}
            return findOrCreateNeighbor(newPosition, allNodes);
        }
        if(direction == directions[2]) {
            double[] newPosition = {current.position[0], current.position[1] - STEP_LENGTH}
            return findOrCreateNeighbor(newPosition, allNodes)
        }
        if(direction == directions[3]) {
            double[] newPosition = {current.position[0], current.position[1] + STEP_LENGTH}
            return findOrCreateNeighbor(newPosition, allNodes)
        }
        
    }
    //Constructed off of Wikipedia pseudocode 
    // A* finds a path from start to goal.
    // h is the heuristic function. h(n) estimates the cost to reach goal from node n.
    protected String[] A_Star(double[] start, Goal goal, Observation obs){
        // The set of discovered nodes that may need to be (re-)expanded.
        // Initially, only the start node is known.
        // This is usually implemented as a min-heap or priority queue rather than a hash-set.
        // Starts with just start coordinate
        ArrayList<Node> allNodes = new ArrayList<Node>();
        ArrayList<Node> openSet = new ArrayList<Node>();
        Node startNode = new Node(start);
        allNodes.append(startNode);
        openSet.append(startNode);

        // {start}

            // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start
        // to n currently known.
        // cameFrom := an empty map
        Arraylist<Node> cameFrom = new ArrayList<Node>();
        // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
        // gScore := map with default value of Infinity
        // gScore[start] := 0

        // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
        // how short a path from start to finish can be if it goes through n.
        // fScore := map with default value of Infinity
        // fScore[start] := h(start)
       
        while(openSet.size() > 0) {
            // This operation can occur in O(Log(N)) time if openSet is a min-heap or a priority queue
            // current := the node in openSet having the lowest fScore[] value
            current = openSet.get(0);
            if(atGoal(current, goal, obs)){ //current.canInteract(goal)
                return reconstruct_path(current);
            }

            openSet.remove(0)
            for(String direction : directions) { 
                // d(current,neighbor) is the weight of the edge from current to neighbor
                // tentative_gScore is the distance from start to the neighbor through current
                tentative_gScore = current.gScore + STEP_LENGTH; // step length
                Node neighbor = neighborInDirection(direction, allNodes);
                
                // d(current, neighbor)
                if(tentative_gScore < neighbor.gscore){
                    // cameFrom[neighbor] := current
                    neighbor.parent = current;
                    neighbor.parentToThisDir = direction;

                    // gScore[neighbor] := tentative_gScore
                    neighbor.gScore = tentative_gScore;

                    neighbor.fScore = tentative_gScore + hWithCollision(neighbor, goal, obs)
                    
                    // fScore[neighbor] := tentative_gScore + h(neighbor)
                    if(!openSet.contains(neighbor){
                        openSet.add(neighbor)
                        Collections.sort(openSet);
                    }
                }
                // This path to neighbor is better than any previous one. Record it!
                  
            }
        }
        // Open set is empty but goal was never reached
        return failure

    // subsumption architecture layer
    protected void goalSearch(Observation obs, String goal) 
    {
        // Inhibit and exhibit layers
        planGoals(obs);
        // movement(obs, goal);
        // interact(obs, goal);
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
        // planGoals(obs);


        for(Goal goal: goals){
            System.out.println("goals are:" + goal.name);
        }
        
        System.out.println("cart Returns are:" + obs.cartReturns.length);
        // System.out.println("basket? Returns are:" + obs.basketReturn.length);

        shouldRunExecutionLoop = false;

        boolean actionChosen = false;
        // Check which aisle the agent is closest to
        goalSearch(obs, goalLocation);
    }
}
