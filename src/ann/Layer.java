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

	int ia = 0;
	int ib = 0;
	int sa = 0;
	int sb = 0;
	
	public Layer(String name)
	{
		this.name = name;
	}

	public void updateDeltaValues(double[] target)
	{
		//clear old delta values Could this be done on the fly??
		//sa = nodes.size();
		//for(ia = 0; ia<sa; ia++) //(Node n : nodes)
		//{
		//	nodes.get(ia).deltaValue = 0;
		//}

		//Get Delta values
		if(exitingLinks.size() == 0)//we have the outputlayer
		{
			double[] vals = new double[nodes.size()];
			sa = nodes.size();
			for(ia = 0; ia<sa; ia++)
			{
				vals[ia] = nodes.get(ia).output;
				
				double desiredActLevel = target[ia];
				nodes.get(ia).deltaValue = nodes.get(ia).derivative(nodes.get(ia).output) * (desiredActLevel-nodes.get(ia).output);
			}
			double error = 0;
			for (int j = 0; j < target.length; j++) {
				double err = (double)Math.pow(vals[j] - target[j], 2);
				error += err;
			}
			Ann.lastRunError=error;
//			System.out.println("run error:"+Ann.lastRunError);
		}
		else//hidden or input layer
		{
			double sum = 0;
			sa = nodes.size();
			for(ia = 0; ia<sa; ia++)
			{
				sum = 0;
				if(nodes.get(ia).outArcs == null) nodes.get(ia).getOutArcs();
				sb = nodes.get(ia).outArcs.size();
				for(ib = 0; ib<sb; ib++) //(Arc a : nodes.get(ia).outArcs)
				{
					sum += nodes.get(ia).outArcs.get(ib).postNode.deltaValue * nodes.get(ia).outArcs.get(ib).currentWeight; 
				}
				nodes.get(ia).deltaValue = nodes.get(ia).derivative(nodes.get(ia).output) * sum; 
			}
		}
		
	}
	
	public void updateArcWeights()
	{
		sa = exitingLinks.size();
		for (ia = 0; ia<sa; ia++) //(Link l : exitingLinks)
		{
			sb = exitingLinks.get(ia).arcs.size();
			for (ib = 0; ib<sb; ib++) //(Arc arc : exitingLinks.get(ia).arcs)
			{
				exitingLinks.get(ia).arcs.get(ib).currentWeight += exitingLinks.get(ia).learningRate * exitingLinks.get(ia).arcs.get(ib).preNode.output * exitingLinks.get(ia).arcs.get(ib).postNode.deltaValue;
			}
			
		}
	}
	
	
}
