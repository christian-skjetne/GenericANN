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
			sa = presynapticLayer.nodes.size();
			sb = postsynapticLayer.nodes.size();
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					arcs.add(new Arc(presynapticLayer.nodes.get(ia), postsynapticLayer.nodes.get(ib), getRandom()/*randRange(weightMin, weightMax)*/, this));
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
			sa = presynapticLayer.nodes.size();
			sb = postsynapticLayer.nodes.size();
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					if(Math.random() < topArg)
						arcs.add(new Arc(presynapticLayer.nodes.get(ia), postsynapticLayer.nodes.get(ib), randRange(weightMin, weightMax), this));
				}
			}
		}
		//triangular
		else if(topology == TRIANGULARTOPOLOGY)
		{
			sa = presynapticLayer.nodes.size();
			sb = postsynapticLayer.nodes.size();
			for (ia = 0; ia<sa; ia++) //(Node preN : presynapticLayer.nodes)
			{
				for (ib = 0; ib<sb; ib++) //(Node postN : postsynapticLayer.nodes)
				{
					if(postsynapticLayer.nodes.indexOf(postsynapticLayer.nodes.get(ib)) != presynapticLayer.nodes.indexOf(presynapticLayer.nodes.get(ia)))
						arcs.add(new Arc(presynapticLayer.nodes.get(ia), postsynapticLayer.nodes.get(ib), randRange(weightMin, weightMax), this));
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
		sa = postsynapticLayer.nodes.size();
		for(ia = 0; ia<sa; ia++) //(Node n :postsynapticLayer.nodes)
		{
			postsynapticLayer.nodes.get(ia).checkNode();
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
