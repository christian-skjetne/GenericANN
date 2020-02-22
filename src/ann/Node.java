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
	Arc[] inArcs;
	Arc[] outArcs;
	//ArrayList<Arc> inArcs = null;
	//ArrayList<Arc> outArcs = null;

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
		siz = inArcs.length;
		for(itr = 0; itr<siz; itr++)
		{
			//pn = a.preNode;
			activation = activation + (inArcs[itr].currentWeight * inArcs[itr].preNode.output);
		}
		
		//System.out.println("NodeData: "+this+" "+sigmoid(s)+" pren:"+pn);
		output = sigmoid(activation);
	}
	
	public void inputToNode(double v)
	{
		prevOutput = output;
		output = v;
	}
	
	public Arc[] getInArcs() //ArrayList<Arc> getInArcs()
	{
		ArrayList<Arc> inA = new ArrayList<Arc>();
		for(Link l : this.parentLayer.enteringLinks)
		{
			for (Arc arc : l.arcs)
			{
				if(arc.postNode == this && !inA.contains(arc))
					inA.add(arc);
			}
		}
		inArcs = new Arc[inA.size()];
		inArcs = inA.toArray(inArcs);
		return inArcs;
	}

	public Arc[] getOutArcs()
	{
		ArrayList<Arc> outAs = new ArrayList<Arc>();
		for(Link l : this.parentLayer.exitingLinks)
		{
			for (Arc arc : l.arcs)
			{
				if(arc.preNode == this && !outAs.contains(arc))
					outAs.add(arc);
			}
		}
		outArcs = new Arc[outAs.size()];
		outArcs = outAs.toArray(outArcs);
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
