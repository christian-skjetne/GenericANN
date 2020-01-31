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
	int actFunction = 1;
	
	final static int SIGMOID = 1;
	
	public Layer(String name)
	{
		this.name = name;
	}

	public void updateDeltaValues(ArrayList<Double> target)
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
				
				double desiredActLevel = target.get(nodes.indexOf(n));
				n.deltaValue = n.derivative(n.output) * (desiredActLevel-n.output);
			}
			double error = 0;
			for (int j = 0; j < target.size(); j++) {
				double err = Math.pow(vals[j] - target.get(j), 2);
				error += err;
			}
			Ann.lastRunError=error;
//			System.out.println("run error:"+Ann.lastRunError);
		}
		else//hidden or input layer
		{
			for (Node n : nodes)
			{
				double sum = 0;
				for (Arc a : n.getOutArcs())
				{
					sum += a.postNode.deltaValue * a.currentWeight; 
				}
				n.deltaValue = n.derivative(n.output) * sum; 
			}
		}
		
	}
	
	public void updateArcWeights()
	{
		for (Link l : exitingLinks)
		{
			for (Arc arc : l.arcs)
			{
				double delta = l.learningRate * arc.preNode.output * arc.postNode.deltaValue;
				arc.currentWeight += delta;
			}
			
		}
	}
	
	
}
