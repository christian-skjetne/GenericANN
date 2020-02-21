# GenericANN
Pure JAVA artificial neural network implementation.
This program is not very well optimized in its current state, but it should be enough to create and train some simple networks.


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

## Examples

There are currently two examples included

## Simple example

Under [/src/annTest/Main.java](/src/annTest/Main.java) you can find a simple example that trains an ANN to estimate the function Sin(x)

## More complex example

In the  [/src/mnist/](/src/mnist/) folder you can find an example that trains an ANN to read hand written letters and digits. 
It uses the [Emnist](https://www.nist.gov/itl/products-and-services/emnist-dataset) dataset to train and test. After the training and testing is done (warning: training takes a long time) a GUI window pops up where you can use the mouse/touchscreen to write digits and letters and make the ANN you just trained guess what you have written.



