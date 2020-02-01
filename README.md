# GenericANN
Pure JAVA artificial neural network implementation.
This program is not very well optimized at its current state, but it should be enough to create and train some simple networks.


## Usage

Example file can be found here: [/src/annTest/Main.java](/src/annTest/Main.java)

create a text file that defines the topology of your network (see the example annBig.txt definition)

Create the network by constructing the Ann object:
```java
Ann net = new Ann("path/To/Ann/Definition/File.txt");
```

Input normalized (between [0-1]) training data and corresponding expected output in to the network using
```java
// Training input to the network.
// number of arguments has to match the number of input nodes in the network
net.trainInput(0.2, 0.6, 0.1); 

// Corresponding expected network output for the inputs given above.
// number of arguments has to match the number of output nodes in the network
net.trainOutput((Math.sin(input)+1)/2.0); 	
```	
Alternative way to input training data. 
First argument array is the normalized input and the second is the corresponding normalized expected output.

```java
net.trainAnnOnCase(new Double[]{0.2, 0.6, 0.1}, new Double[]{(Math.sin(input)+1)/2.0});	
```	

To print output from a trained network, use this method.
The first argument is the number of decimals to print. 
The other arguments are the normalized input values to the network (can either be an array or just one double for each input node.)
```java
net.runAnn(2, 0.2, 0.6, 0.1);
```	

To get just the output values you can use the following method.
Arguments are the normalized input values to the network (can either be an array or just one double for each input node.
```java
Double output = net.getAnnRes(0.2, 0.6, 0.1);
```	

