package annTest;

import ann.Ann;
import ann.InputOutputSizeException;

public class Main
{
	public static void main(String[] args)
	{
		String path = "..\\annBig.txt";

		Ann net = new Ann();

		String file = Ann.readFileAsString(path);


		net.createNet(file);

		try
		{
			System.out.println("Starting training...");
			double targetError = .02;
			for (int i = 0; i < 10000; i++)
			{

				net.trainAnnOnCase(new Double[]{0.0,0.0,0.0}, new Double[]{1.0,1.0});
				net.trainAnnOnCase(new Double[]{0.0,0.0,1.0}, new Double[]{0.5,1.0});
				net.trainAnnOnCase(new Double[]{0.0,1.0,0.0}, new Double[]{0.0,1.0});
				net.trainAnnOnCase(new Double[]{0.0,1.0,1.0}, new Double[]{0.0,1.0});
				net.trainAnnOnCase(new Double[]{1.0,0.0,0.0}, new Double[]{1.0,0.5});
				net.trainAnnOnCase(new Double[]{1.0,0.0,1.0}, new Double[]{1.0,0.0});
				net.trainAnnOnCase(new Double[]{1.0,1.0,0.0}, new Double[]{1.0,0.0});
				//or you could use this format:
				net.trainInput(1.0,1.0,1.0);
				net.trainOutput(1.0,0.0);
				
				if(Ann.lastRunError*100 < targetError)
				{
					System.out.println("Error <"+targetError+"% after "+i+" runs.\n");
					break;
				}
			}
			net.runAnn(2, 0.0,0.0,0.0);
			System.out.println("EX: 1,1");
			net.runAnn(2, 0.0,0.0,1.0);
			System.out.println("EX: .5,1");
			net.runAnn(2, 0.0,1.0,0.0);
			System.out.println("EX: 0,1");
			net.runAnn(2, 0.0,1.0,1.0);
			System.out.println("EX: 0,1");
			net.runAnn(2, 1.0,0.0,0.0);
			System.out.println("EX: 1,.5");
			net.runAnn(2, 1.0,0.0,1.0);
			System.out.println("EX: 1,0");
			net.runAnn(2, 1.0,1.0,0.0);
			System.out.println("EX: 1,0");
			net.runAnn(2, 1.0,1.0,1.0);
			System.out.println("EX: 1,0");
			
			System.out.println("Last run error: "+Ann.lastRunError*100+"%");
		}
		catch (InputOutputSizeException e)
		{
			e.printStackTrace();
		}
	}
}
