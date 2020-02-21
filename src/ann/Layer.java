package ann;

import java.util.ArrayList;

public class Layer
{

	//public ArrayList<Node> nodes 			= new ArrayList<Node>();
	public Node[] nodes;// 			= new ArrayList<Node>();
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
			double[] vals = new double[nodes.length];
			sa = nodes.length;
			for(ia = 0; ia<sa; ia++)
			{
				vals[ia] = nodes[ia].output;
				
				double desiredActLevel = target[ia];
				nodes[ia].deltaValue = nodes[ia].derivative(nodes[ia].output) * (desiredActLevel-nodes[ia].output);
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
			sa = nodes.length;
			for(ia = 0; ia<sa; ia++)
			{
				sum = 0;
				if(nodes[ia].outArcs == null) nodes[ia].getOutArcs();
				sb = nodes[ia].outArcs.size();
				for(ib = 0; ib<sb; ib++) //(Arc a : nodes.get(ia).outArcs)
				{
					sum += nodes[ia].outArcs.get(ib).postNode.deltaValue * nodes[ia].outArcs.get(ib).currentWeight; 
				}
				nodes[ia].deltaValue = nodes[ia].derivative(nodes[ia].output) * sum; 
			}
		}
		
	}
	
	/**
	 * TODO: need to connect hebbian(Arc arc) in Link.java instead of l.learningRate * arc.preNode.output * arc.postNode.deltaValue
	 */
	public void updateArcWeights()
	{
		sa = exitingLinks.size();
		for (ia = 0; ia<sa; ia++) //(Link l : exitingLinks)
		{
			sb = exitingLinks.get(ia).arcs.length;
			for (ib = 0; ib<sb; ib++) //(Arc arc : exitingLinks.get(ia).arcs)
			{
				exitingLinks.get(ia).arcs[ib].currentWeight += exitingLinks.get(ia).learningRate * exitingLinks.get(ia).arcs[ib].preNode.output * exitingLinks.get(ia).arcs[ib].postNode.deltaValue;
			}
			
		}
	}
	
	
}
