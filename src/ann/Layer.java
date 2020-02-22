package ann;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Layer {
	

	// public ArrayList<Node> nodes = new ArrayList<Node>();
	public Node[] nodes;
	public ArrayList<Link> enteringLinks = new ArrayList<Link>();
	public ArrayList<Link> exitingLinks = new ArrayList<Link>();
	public String name = "";

	boolean learningMode = true;
	boolean quiescentMode = true;
	boolean active = true;
	int settlingRounds = 100;
	int actFunction = 1;

	final static int SIGMOID = 1;

	int ia = 0;
	int ib = 0;
	int sa = 0;
	int sb = 0;

	public Layer(String name) {
		this.name = name;
	}

	public void updateDeltaValues(double[] target) {
		// Get Delta values
		if (exitingLinks.size() == 0)// we have the outputlayer
		{
			double[] vals = new double[nodes.length];
			sa = nodes.length;
			for (ia = 0; ia < sa; ia++) {
				vals[ia] = nodes[ia].output;

				double desiredActLevel = target[ia];
				nodes[ia].deltaValue = nodes[ia].derivative(nodes[ia].output) * (desiredActLevel - nodes[ia].output);
			}
			double error = 0;
			for (int j = 0; j < target.length; j++) {
				double err = (double) Math.pow(vals[j] - target[j], 2);
				error += err;
			}
			Ann.lastRunError = error;
			
		} 
		else// hidden or input layer
		{
			double sum = 0;
			sa = nodes.length;
			for(ia = 0; ia<sa; ia++)
			{
				sum = 0;
				if(nodes[ia].outArcs == null) nodes[ia].getOutArcs();
				sb = nodes[ia].outArcs.length;
				for(ib = 0; ib<sb; ib++) //(Arc a : nodes.get(ia).outArcs)
				{
					sum += nodes[ia].outArcs[ib].postNode.deltaValue * nodes[ia].outArcs[ib].currentWeight; 
				}
				nodes[ia].deltaValue = nodes[ia].derivative(nodes[ia].output) * sum; 
			}

			/* Shitty attempt at multi-threading. does not work :(
			ArrayList<Future<?>> done = new ArrayList<>();
			sa = nodes.length;
			int split = nodes.length/2;
			
				done.add(Ann.calcService.submit(new Runnable() {
					@Override
					public void run() 
					{
						for(int ni = 0; ni<split; ni++)
						{	
							double sum = 0;
							if(nodes[ni].outArcs == null) nodes[ni].getOutArcs();
							sb = nodes[ni].outArcs.length;
							for(int ib = 0; ib<sb; ib++) //(Arc a : nodes.get(ia).outArcs)
							{
								sum += nodes[ni].outArcs[ib].postNode.deltaValue * nodes[ni].outArcs[ib].currentWeight; 
							}
							nodes[ni].deltaValue = nodes[ni].derivative(nodes[ni].output) * sum;
						}
					}
				}));
				done.add(Ann.calcService.submit(new Runnable() {
					@Override
					public void run() 
					{
						for(int ni = split; ni<nodes.length; ni++)
						{	
							double sum = 0;
							if(nodes[ni].outArcs == null) nodes[ni].getOutArcs();
							sb = nodes[ni].outArcs.length;
							for(int ib = 0; ib<sb; ib++) //(Arc a : nodes.get(ia).outArcs)
							{
								sum += nodes[ni].outArcs[ib].postNode.deltaValue * nodes[ni].outArcs[ib].currentWeight; 
							}
							nodes[ni].deltaValue = nodes[ni].derivative(nodes[ni].output) * sum;
						}
					}
				}));
			
			// Block until all nodes are done
			for (Future<?> d : done) 
			{
				try 
				{
					d.get();
				} 
				catch (InterruptedException | ExecutionException e) 
				{
					e.printStackTrace();
				}
			}
			*/
			
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
