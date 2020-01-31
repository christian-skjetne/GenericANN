package ann;

public class Arc
{

	Node preNode;
	Node postNode;
	
	double currentWeight;
	double initialWeight;
	
	Link parentLink;
	
	public Arc(Node preNode, Node postNode, double weight, Link parent)
	{
		this.preNode = preNode;
		this.postNode = postNode;
		this.currentWeight = weight;
		this.initialWeight = weight;
		this.parentLink = parent;
	}
}
