package ann;

import java.util.ArrayList;

public class Node
{

	Layer parentLayer;
	double membranePotential;//sum of weighted inputs
	double output;
	double deltaValue;
	double prevOutput;
	double activation = 0;
	ArrayList<Arc> inArcs = null;
	ArrayList<Arc> outArcs = null;

	int itr = 0;
	int siz = 0;

	public Node(Layer parent)
	{
		parentLayer = parent;
	}
	
	public void checkNode()
	{
		activation = 0;
		if(inArcs == null) getInArcs();
		//Node pn = null;
		siz = inArcs.size();
		for(itr = 0; itr<siz; itr++)
		{
			//pn = a.preNode;
			activation = activation + (inArcs.get(itr).currentWeight * inArcs.get(itr).preNode.output);
		}
		
		//System.out.println("NodeData: "+this+" "+sigmoid(s)+" pren:"+pn);
		output = sigmoid(activation);
	}
	
	public void inputToNode(double v)
	{
		prevOutput = output;
		output = v;
	}
	
	public ArrayList<Arc> getInArcs()
	{
		inArcs = new ArrayList<Arc>();
		for(Link l : this.parentLayer.enteringLinks)
		{
			for (Arc arc : l.arcs)
			{
				if(arc.postNode == this && !inArcs.contains(arc))
					inArcs.add(arc);
			}
		}
		return inArcs;
	}

	public ArrayList<Arc> getOutArcs()
	{
		outArcs = new ArrayList<Arc>();
		for(Link l : this.parentLayer.exitingLinks)
		{
			for (Arc arc : l.arcs)
			{
				if(arc.preNode == this && !outArcs.contains(arc))
					outArcs.add(arc);
			}
		}
		return outArcs;
	}

	public static double sigmoid(double x) 
	{
		return (1/( 1 + (double)Math.pow(Math.E,(-1*x))));
	}

	public double derivative(double v)
	{
		return v*(1-v);//sigmoid derivative.
	}
}
