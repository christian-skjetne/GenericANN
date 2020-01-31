package ann;

import java.util.ArrayList;
import java.util.Random;

public class Link
{

	public ArrayList<Arc> arcs = new ArrayList<Arc>();

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

	public Link(Layer pre, Layer post,double weightMin,double weightMax,int topology,double topArg)
	{
		presynapticLayer = pre;
		postsynapticLayer = post;
		//full
		if(topology == FULLTOPOLOGY)
		{
			for (Node preN : presynapticLayer.nodes)
			{
				for (Node postN : postsynapticLayer.nodes)
				{
					arcs.add(new Arc(preN, postN, getRandom()/*randRange(weightMin, weightMax)*/, this));
				}
			}
		}
		//one to one
		else if(topology == ONETONOETOPOLOGY)
		{
			//select the size of the smallest layer
			int min = (pre.nodes.size() < post.nodes.size()) ? pre.nodes.size():post.nodes.size();

			for (int i = 0; i < min; i++)
			{
				arcs.add(new Arc(presynapticLayer.nodes.get(i), postsynapticLayer.nodes.get(i), randRange(weightMin, weightMax), this));
			}
		}
		//stochastic
		else if(topology == STOCHASTICTOPOLOGY)
		{
			for (Node preN : presynapticLayer.nodes)
			{
				for (Node postN : postsynapticLayer.nodes)
				{
					if(Math.random() < topArg)
						arcs.add(new Arc(preN, postN, randRange(weightMin, weightMax), this));
				}
			}
		}
		//triangular
		else if(topology == TRIANGULARTOPOLOGY)
		{
			for (Node preN : presynapticLayer.nodes)
			{
				for (Node postN : postsynapticLayer.nodes)
				{
					if(postsynapticLayer.nodes.indexOf(postN) != presynapticLayer.nodes.indexOf(preN))
						arcs.add(new Arc(preN, postN, randRange(weightMin, weightMax), this));
				}
			}
		}
	}

	// random
	double getRandom() {
		return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
	}

	public void propagate()
	{
		for(Node n :postsynapticLayer.nodes)
		{
			n.checkNode();
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
