package ann;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Ann
{
	public static ExecutorService calcService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public Layer[] layers;// = new ArrayList<Layer>();	
	HashMap<String, Layer> layerNames = new HashMap<String, Layer>();
	private Layer inputLayer;
	private Layer outputLayer;
	public static double lastRunError = 1;

	int ia = 0;
	int sa = 0;
	int ib = 0;
	int sb = 0;

	/**
	 * Constructor
	 */
	public Ann()
	{

	}

	/**
	 * Constructor that creates a complete network based on the file given as input.
	 * 
	 * @param pathToFile Neural network definition file
	 */
	public Ann(String pathToFile)
	{
		this.createNet(Ann.readFileAsString(pathToFile));;
	}

	/**
	 * Performs the back propagation based on the target output data
	 * 
	 * @param output desired output data values.
	 * @throws InputOutputSizeException output data size is different from the number of output nodes.
	 */
	private void backProp(double[] output) throws InputOutputSizeException
	{
		if(outputLayer.nodes.length != output.length)
			throw new InputOutputSizeException("Size of output data["+output.length+"] is different from the number of output nodes["+outputLayer.nodes.length+"]");
		
		for (int i = layers.length-1; i >= 0; i--)
		{
			//System.out.println("START BACK PROP: layer="+i);
			layers[i].updateDeltaValues(output);
			layers[i].updateArcWeights();
		}
	}

	/**
	 * creates a neural network out of the definition in the given file.
	 * </br></br>
	 * The system reads a script file in the following form: </br></br>
		%layer </br>
		name=input </br>
		type=1 </br>
		size=3 </br>
		activation function=1 </br></br>
		%layer </br>
		name=hidden </br>
		type=1 </br>
		size=3 </br>
		activation function=1 </br></br>
		%layer </br>
		name=output </br>
		type=1 </br>
		size=3 </br>
		activation function=1 </br></br>
		%link </br>
		pre layer=input </br>
		post layer=hidden </br>
		topology=1 </br>
		top arg=0 </br>
		learning rule=hebbian </br>
		learning rate=0.5 </br>
		min weight=0 </br>
		max weight=0 </br></br>
		%link </br>
		pre layer=hidden </br>
		post layer=output </br>
		topology=1 </br>
		top arg=0 </br>
		learning rule=hebbian </br>
		learning rate=0.5 </br>
		min weight=0 </br>
		max weight=0 </br>
		</br>
		Layers: </br>
		name -layer name is used as reference when creating links</br>
		type - if you have several subclasses of layers, then this will indicate which one. </br>
		size - number of neurons in the layer </br>
		activation function - to be used by all neurons in the layer </br>
		</br>
		Links: </br>
		pre layer - Name of the presynaptic layer </br>
		post layer - Name of the postsynaptic layer </br>
		topology - The topology between the two layers (see below) </br>
		top arg - A connection probability (in case the topology is stochastic) </br>
		learning rule - Learning rule used by all the arcs of the link. </br>
		learning rate - Learning rate used by all arcs of the link. </br>
		min weight and max weight - random range of initial weights for all the arcs of the link. </br>
		The Execution Order is given by the order of the layer entries in the script document. </br></br>
		The topology variable is defined in the following way: </br>
		FULL TOPOLOGY = 1 </br>
		ONE TO ONE TOPOLOGY = 2 </br>
		STOCHASTIC TOPOLOGY = 3 </br>
		TRIANGULAR TOPOLOGY = 4 </br></br>
		Not all features have been implemented</br>
	 * 
	 * @param file
	 */
	public void createNet(String file)
	{
		ArrayList<Layer> allLayers = new ArrayList<>();
		for (String s : file.split("%"))
		{
			String entry = s.trim();
			System.out.println("INPUT: \n"+s);
			String[] data = entry.split("\n");
			
			if(data[0].startsWith("layer"))//new layer
			{
				System.out.println("New layer");
				String 	name = "";
				String 	type = "1";
				int		size = 10;
				int	function = 1;
				for (String d : data)
				{
					d = d.trim();
					if(d.startsWith("name"))
						name = d.replace("name=", "");
					else if(d.startsWith("type"))
						type = d.replace("type=", "");
					else if(d.startsWith("size"))
						size = Integer.parseInt(d.replace("size=", ""));
					else if(d.startsWith("activation"))
						function = Integer.parseInt(d.replace("activation function=", ""));
				}
				Layer l = new Layer(name);
				l.nodes = new Node[size];
				for (int i = 0; i < size; i++)
				{
					l.nodes[i] = new Node(l);
				}
				l.actFunction = function;
				if(allLayers.size() == 0) inputLayer = l;
				allLayers.add(l);
				layerNames.put(name, l);
			}
			else if(data[0].startsWith("link"))
			{
				System.out.println("new link");
				String pre = "";
				String post = "";
				int topology = Link.FULLTOPOLOGY;
				double topArg = 0.0;
				String learningRule = "hebbian";
				double learningRate = 1;
				double minWeight = 0.1;
				double maxWeight = 0.5;
				for (String d: data)
				{
					d = d.trim();
					if(d.startsWith("pre"))
						pre = d.replace("pre layer=", "");
					else if(d.startsWith("post"))
						post = d.replace("post layer=", "");
					else if(d.startsWith("topology"))
						topology = Integer.parseInt(d.replace("topology=", ""));
					else if(d.startsWith("top arg"))
						topArg = Double.parseDouble(d.replace("top arg=", ""));
					else if(d.startsWith("learning rule"))
						learningRule = d.replace("learning rule=", "");
					else if(d.startsWith("learning rate"))
						learningRate = Double.parseDouble(d.replace("learning rate=", ""));
					else if(d.startsWith("min weight"))
						minWeight = Double.parseDouble(d.replace("min weight=", ""));
					else if(d.startsWith("max weight"))
						maxWeight = Double.parseDouble(d.replace("max weight=", ""));
				}
				Link l = new Link(layerNames.get(pre), layerNames.get(post), minWeight, maxWeight, topology, topArg);
				layerNames.get(pre).exitingLinks.add(l);
				layerNames.get(post).enteringLinks.add(l);
				l.learningRate = learningRate;
			}
		}
		outputLayer = allLayers.get(allLayers.size()-1);
		layers = new Layer[allLayers.size()];
		layers = allLayers.toArray(layers);
		System.out.println("----------NETWORK CREATED----------");
		for (Layer l : this.layers)
		{
			System.out.println("LAYER: "+l.name);
			System.out.println("nodes: "+l.nodes.length);
			int edges = 0;
			for(Link lnk : l.exitingLinks)
				edges += lnk.arcs.length;
			System.out.println("edges: "+edges);
		}
		System.out.println("");
	}

	/**
	 * Prints out the last values stored in the output nodes of the network.
	 * 
	 * @param decimals number of decimals to include in the print out.
	 */
	public void printAnnRes(int decimals)
	{
		System.out.print("ANN output: ");
		for (Node n : outputLayer.nodes)
		{
			System.out.printf("[%."+decimals+"f] ",n.output);
		}
		System.out.println();
	}

	/**
	 * Prints out the last values stored in the output nodes of the network. (minimum output)
	 * 
	 * @param decimals number of decimals to include in the print out.
	 */
	public void printMinimumAnnRes(int decimals)
	{
		for (Node n : outputLayer.nodes)
		{
			System.out.printf("%."+decimals+"f ",n.output);
		}
	}

	public double[] getAnnRes(double... input) throws InputOutputSizeException
	{
		feedAnn(input);
		return getAnnOut();
	}

	/**
	 * Returns the last values stored in the output nodes of the network.
	 */
	public double[] getAnnOut()
	{
		double[] res = new double[outputLayer.nodes.length];
		for (int i = 0; i<outputLayer.nodes.length; i++)
		{
			res[i] = outputLayer.nodes[i].output;
		}
		return res;
	}

	/**
	 * Returns a list of the last values stored in the output nodes of the network.
	 */
	public ArrayList<Double> getAnnOutList()
	{
		ArrayList<Double> res = new ArrayList<Double>();
		for (Node n : outputLayer.nodes) 
		{
			res.add(n.output);
		}
		return res;
	}

	/**
	 * Starts the back prop with the given desired output data set.
	 * @param ds output data set.
	 * @throws InputOutputSizeException output data size is different from the number of output nodes.
	 */
	private void doBackPropagation(double... ds) throws InputOutputSizeException
	{
		this.backProp(ds);
	}

	/**
	 * Train the network on a case
	 * 
	 * @param input the input data for this training case
	 * @param output the desired output data for this training case
	 * @throws InputOutputSizeException input/output data size is different from the number of input/output nodes.
	 */
	public void trainAnnOnCase(double[] input,double[] output) throws InputOutputSizeException
	{

		//CASE
		feedAnn(input);
		doBackPropagation(output);
	}

	/**
	 * Train the network on a case
	 * used before trainOutput()
	 * 
	 * @param input the input data for this training case
	 * @throws InputOutputSizeException input/output data size is different from the number of input/output nodes.
	 */
	public void trainInput(double... input) throws InputOutputSizeException
	{
		feedAnn(input);
	}

	/**
	 * Train the network on a case
	 * used after trainInput()
	 * 
	 * @param output the desired output data for this training case
	 * @throws InputOutputSizeException input/output data size is different from the number of input/output nodes.
	 */
	public void trainOutput(double... output) throws InputOutputSizeException
	{
		doBackPropagation(output);
	}


	/**
	 * Propagates the input data trough the network to calculate the output data.
	 * 
	 * @param input the input for the network.
	 * @throws InputOutputSizeException input data size is different from the number of input nodes.
	 */
	private void feedAnn(double... input) throws InputOutputSizeException
	{
		if(inputLayer.nodes.length != input.length)
			throw new InputOutputSizeException("Size of input data["+input.length+"] is different from the number of input nodes["+inputLayer.nodes.length + "]");

		for (int i = 0; i < input.length; i++)//get the number for the current input data/node.
		{
			inputLayer.nodes[i].inputToNode(input[i]);
		}

		//Start the input data propagation:
		sa = this.layers.length;
		for (ia = 0; ia<sa; ia++) //(Layer lr : this.layers)
		{
			if(this.layers[ia].exitingLinks.size() == 0) break;//reached output
			sb = this.layers[ia].exitingLinks.size();
			for (ib = 0; ib<sb; ib++) //(Link l : this.layers.get(ia).exitingLinks)
				this.layers[ia].exitingLinks.get(ib).propagate();

		}
	}

	/**
	 * Runs the ANN on the given data set and prints the results.
	 * 
	 * @param input the input for the network.
	 * @throws InputOutputSizeException input data size is different from the number of input nodes.
	 */
	public void runAnn(int decimals, double... input) throws InputOutputSizeException
	{
		feedAnn(input);
		printAnnRes(decimals);
	}

	/**
	 * Runs the ANN on the given data set and prints the results (prints only the minimum of results).
	 * 
	 * @param input the input for the network.
	 * @throws InputOutputSizeException input data size is different from the number of input nodes.
	 */
	public void runMinAnn(int decimals, double... input) throws InputOutputSizeException
	{
		feedAnn(input);
		printMinimumAnnRes(decimals);
	}

	public void combine(Ann other)
	{
		for (int layer = 0; layer < layers.length; layer++) 
		{
			for (int link = 0; link < layers[layer].exitingLinks.size(); link++) 
			{
				for (int arc = 0; arc < layers[layer].exitingLinks.get(link).arcs.length; arc++) 
				{
					this.layers[layer].exitingLinks.get(link).arcs[arc].currentWeight = (this.layers[layer].exitingLinks.get(link).arcs[arc].currentWeight + other.layers[layer].exitingLinks.get(link).arcs[arc].currentWeight)/2.;
				}
			}
		}
	}

	/**
	 * Reads in a text file and outputs it as a String object for parsing
	 * 
	 * TODO: propagate exceptions
	 * 
	 * @param filePath path to the text file
	 * @return String representation of the text file.
	 */
	public static String readFileAsString(String filePath)
	{
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		return new String(buffer);
	}

	public static String printDoubleArray(int decimals, double... da)
	{
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumFractionDigits(decimals);

		String s = "[";
		for (double d : da) 
		{
			s += nf.format(d)+", ";
		}
		s += "]";
		return s.replace(", ]", "]");
	}
}
