package ann;

import java.util.ArrayList;

public class Layer
{

	public ArrayList<Node> nodes 			= new ArrayList<Node>();
	public ArrayList<Link> enteringLinks 	= new ArrayList<Link>();
	public ArrayList<Link> exitingLinks 	= new ArrayList<Link>();
	public String name = "";
	
	boolean learningMode 	= true;
	boolean quiescentMode 	= true;
	boolean active 			= true;
	int 	settlingRounds 	= 100;
	int 	actFunction 	= 1;
	
	final static int SIGMOID = 1;
	
	public Layer(String name)
	{
		this.name = name;
	}

	public void updateDeltaValues(Double[] target)
	{
		//clear old delta values
		for (Node n : nodes)
		{
			n.deltaValue = 0;
		}
		//Get Delta values
		if(exitingLinks.size() == 0)//we have the outputlayer
		{
			double[] vals = new double[nodes.size()];
			int i = 0;
			for (Node n : nodes)
			{
				vals[i++] = n.output;
				
				double desiredActLevel = target[nodes.indexOf(n)];
				n.deltaValue = n.derivative(n.output) * (desiredActLevel-n.output);
			}
			double error = 0;
			for (int j = 0; j < target.length; j++) {
				double err = Math.pow(vals[j] - target[j], 2);
				error += err;
			}
			Ann.lastRunError=error;
//			System.out.println("run error:"+Ann.lastRunError);
		}
		else//hidden or input layer
		{
			double sum = 0;
			for (Node n : nodes)
			{
				sum = 0;
				if(n.outArcs == null) n.getOutArcs();
				for (Arc a : n.outArcs)
				{
					sum += a.postNode.deltaValue * a.currentWeight; 
				}
				n.deltaValue = n.derivative(n.output) * sum; 
			}
		}
		
	}
	
	/**
	 * TODO: need to connect hebbian(Arc arc) in Link.java instead of l.learningRate * arc.preNode.output * arc.postNode.deltaValue
	 */
	public void updateArcWeights()
	{
		for (Link l : exitingLinks)
		{
			for (Arc arc : l.arcs)
			{
				arc.currentWeight += l.learningRate * arc.preNode.output * arc.postNode.deltaValue;
			}
			
		}
	}
	
	
}
