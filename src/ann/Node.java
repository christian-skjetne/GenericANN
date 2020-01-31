package ann;

import java.util.ArrayList;

public class Node
{

	Layer parentLayer;
	double membranePotential;//sum of weighted inputs
	double output;
	double deltaValue;
	double prevOutput;

	public Node(Layer parent)
	{
		parentLayer = parent;
	}
	
	public void checkNode()
	{
		double s = 0;
		Node pn = null;
		for(Arc a : getInArcs())
		{
			double weight = a.currentWeight;
			pn = a.preNode;
			double al = a.preNode.output;

			s = s + (weight * al);
		}
		
		//System.out.println("NodeData: "+this+" "+sigmoid(s)+" pren:"+pn);

		output = sigmoid(s);
	}
	
	public void inputToNode(double v)
	{
		prevOutput = output;
		output = v;
	}
	
	public ArrayList<Arc> getInArcs()
	{
		ArrayList<Arc> inArcs = new ArrayList<Arc>();
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
		ArrayList<Arc> outArcs = new ArrayList<Arc>();
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

	public static double sigmoid(double x) {
		return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}

	public double derivative(double v)
	{
		return v*(1-v);//sigmoid derivative.
	}
}
