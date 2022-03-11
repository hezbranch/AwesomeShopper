import java.util.Arrays;
import com.supermarket.*;

public class Agent extends SupermarketComponentImpl {

    public Agent() {
	super();
	shouldRunExecutionLoop = true;
	log.info("In Agent constructor.");
    }

    boolean firsttime = true;

    @Override
    protected void executionLoop() {
	// this is called every 100ms
	// put your code in here
	Observation obs = getLastObservation();
	System.out.println(obs.players.length + " players");
	System.out.println(obs.carts.length + " carts");
	System.out.println(obs.shelves.length + " shelves");
	System.out.println(obs.counters.length + " counters");
	System.out.println(obs.registers.length + " registers");
	System.out.println(obs.cartReturns.length + " cartReturns");
	// print out the shopping list
	System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
	// now run around in circles
	while(true) {
	    goNorth();
	    goEast();
	    goSouth();
	    goWest();
	}
    }
}
