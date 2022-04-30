# AwesomeShopper

Working to create the best simple robot for shopping.

## Compiling Agent Code
`javac -cp DIARC.jar Agent.java Goal.java`

## Running Agent Package (Windows OS)
`java -cp .;DIARC.jar Agent Agent `
  
## Running Agent Package (macOS)
`java -cp .:DIARC.jar Agent Agent `
<br/>
<br/>
<br/>

## Matt's Notes for Running Test Agent
*how to create a new python game environment with multiple agents
`python3 socket_env.py --num_players 2` 
or
`python3 socket_env.py --num_player=2`
*how to compile new version of code with multiple agents with Goal.java 
`javac -cp .:DIARC.jar AgentG4.java Goal.java runMultiplayer.java`
*how to run multiple agents at once 
`java -cp .:DIARC.jar runMultiplayer Agent1 Agent2`


## List of documented changes
HW: "Your goal is to make sure that the agent you submit will violateas few norms as possible
1. Rename Agent.java to AgentG4.java
2. Upload all relevant files (TestAgent.class, DIARC-5.jar, runMultiplayer.java, runMultiplayer-1.class, runMultiplayer-2.java, runMultiplayer-3.java, runMultiplayer-4.java,  runMultiplayer$AgentThread-1.class)
3. Rename public class in AgentG4.java file from Agent to AgentG4
4. Rename constructor from Agent to AgentG4
5. Download the DIARC-5 and rename it DIARC.jar file
6. 


All code outside of our group's Agent and Goal class is attributed to:

https://github.com/teahmarkstone/Supermarket-Environment

developed by Teah Markstone and Daniel Kasenberg
