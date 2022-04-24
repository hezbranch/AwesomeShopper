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
All code outside of our group's Agent and Goal class is attributed to:

https://github.com/teahmarkstone/Supermarket-Environment

developed by Teah Markstone and Daniel Kasenberg


# Running Multiple Agent

How to run multiple agents in order to test shopping agent norms

## Compiling for Multiple Agents
javac -cp .:DIARC.jar runMultiplayer.java

## Running Multiple Agent Packages At Once
java -cp .:DIARC.jar Agent1 Agent2 Agent3

### Running Game with Multiple Agents
python3 socket_env.py --num_player=3
