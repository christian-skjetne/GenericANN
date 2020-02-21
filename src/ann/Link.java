package ann;

import java.util.ArrayList;
import java.util.Random;

public class Link
{

	//public ArrayList<Arc> arcs = new ArrayList<Arc>();
	public Arc[] arcs; // = new ArrayList<Arc>();

	final Random rand = new Random();
	final int randomWeightMultiplier = 1;
	Layer presynapticLayer;
	Layer postsynapticLayer;
	double learningRate;
	int learningRule;

	final static int FULLTOPOLOGY = 1; 
	final static int ONETONOETOPOLOGY = 2; 
	final static int STOCHASTICTOPOLOGY = 3; 
	final static int TRIANGULARTOPOLOGY = 4; 

	int ia = 0;
	int ib = 0;
	int sa = 0;
	int sb = 0;

	public Link(Layer pre, Layer post,double weightMin,double weightMax,int topology,double topArg)
	{
		presynapticLayer = pre;
		postsynapticLayer = post;
		//full
		if(topology == FULLTOPOLOGY)
		{
			sa = presynapticLayer.nodes.length;
			sb = postsynapticLayer.nodes.length;
			arcs = new Arc[sa*sb];
			int in = 0;
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					arcs[in++] = new Arc(presynapticLayer.nodes[ia], postsynapticLayer.nodes[ib], getRandom()/*randRange(weightMin, weightMax)*/, this);
				}
			}
		}
		//one to one
		else if(topology == ONETONOETOPOLOGY)
		{
			//select the size of the smallest layer
			int min = (pre.nodes.length < post.nodes.length) ? pre.nodes.length:post.nodes.length;
			arcs = new Arc[min];
			for (int i = 0; i < min; i++)
			{
				arcs[i] = new Arc(presynapticLayer.nodes[i], postsynapticLayer.nodes[i], randRange(weightMin, weightMax), this);
			}
		}
		//stochastic
		else if(topology == STOCHASTICTOPOLOGY)
		{
			sa = presynapticLayer.nodes.length;
			sb = postsynapticLayer.nodes.length;
			ArrayList<Arc> newArcs = new ArrayList<>();
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					if(Math.random() < topArg)
						newArcs.add(new Arc(presynapticLayer.nodes[ia], postsynapticLayer.nodes[ib], randRange(weightMin, weightMax), this));
				}
			}
			arcs = new Arc[newArcs.size()];
			arcs = (Arc[])newArcs.toArray();
		}
		//triangular
		else if(topology == TRIANGULARTOPOLOGY)
		{
			sa = presynapticLayer.nodes.length;
			sb = postsynapticLayer.nodes.length;
			ArrayList<Arc> newArcs = new ArrayList<>();
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					if(ib != ia)
						newArcs.add(new Arc(presynapticLayer.nodes[ia], postsynapticLayer.nodes[ib], randRange(weightMin, weightMax), this));
				}
			}
			arcs = new Arc[newArcs.size()];
			arcs = (Arc[])newArcs.toArray();
		}
	}

	// random
	double getRandom() {
		return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
	}

	public void propagate()
	{
		sa = postsynapticLayer.nodes.length;
		for(ia = 0; ia<sa; ia++) //(Node n :postsynapticLayer.nodes)
		{
			postsynapticLayer.nodes[ia].checkNode();
		}
	}

	//Learning rules returns deltaWeight:
	public double hebbian(Arc arc)
	{
		return learningRate*arc.preNode.output*arc.postNode.output;
	}
	public double hebb(Arc arc,double threshold)
	{
		return learningRate*(arc.preNode.output-threshold)*(arc.postNode.output-threshold);
	}
	public double oja(Arc arc)
	{
		return learningRate*arc.postNode.output*(arc.preNode.output-arc.postNode.output*arc.currentWeight);
	}

	public int randRange(int Min, int Max)
	{
		return Min + (int)(Math.random() * ((Max - Min) + 1));
	}

	public double randRange(double Min, double Max)
	{
		return Min + (Math.random() * ((Max - Min) + 1));
	}
}
