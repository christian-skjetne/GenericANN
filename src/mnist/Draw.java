package mnist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

class Draw{

    public static void main(String args[]){new Draw(null);}

    Main mnist;
    DrawCanvas canvas;
    JLabel guessLabel;

    /**
     * Creates a window for the user to draw digits in.
     * 
     * @param mnist reference to the mnist class where the ANN is.
     */
    public Draw(Main mnist)
    {
        this.mnist = mnist;

       JFrame frame = new JFrame("Draw a letter or digit");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(300,300);
       canvas = new DrawCanvas();
       JPanel buttonPanel = new JPanel();
       JButton clearButton = new JButton("Clear");
       clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.clearCanvas();
            }
       });
       JButton guessButton = new JButton("Guess");
       guessButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                guess();
            }
       });
       guessLabel = new JLabel("Guess: ?");
       buttonPanel.add(clearButton);
       buttonPanel.add(guessButton);
       buttonPanel.add(guessLabel);
       frame.getContentPane().add(BorderLayout.CENTER,canvas); 
       frame.getContentPane().add(BorderLayout.SOUTH,buttonPanel); 
       frame.pack();
       frame.setVisible(true);
    }

    /**
     * Performs a guess of the digit drawn in the window
     */
    public void guess()
    {
        int sumX = 0;
        int num = 0;
        int sumY = 0;
        for (int i = 0; i < canvas.img.length; i++) 
        {
            if(canvas.img[i] > 0)
            {
                sumX += canvas.getX(i);
                sumY += canvas.getY(i);
                num++;
            }
        }
        int avgX = sumX/num;
        int avgY = sumY/num;
        int moveX = (28/2)-avgX;
        int moveY = (28/2)-avgY;
        //System.out.println(moveX+" "+moveY);

        double[] oldCanv = canvas.img.clone();
        canvas.clearCanvas();
        for (int i = 0; i < canvas.img.length; i++) 
        {
            int x = canvas.getX(i);
            int y = canvas.getY(i);
            double val = DrawCanvas.getImgValue(oldCanv, x, y);
            canvas.setImgValue(val, x+moveX, y+moveY);
        }
        canvas.repaint();
        int guess = mnist.guess(canvas.img);
        guessLabel.setText("\nGuess: "+guess+" "+(char)(guess+'@'));
    }
}

/**
 * Canvas to draw on
 */
class DrawCanvas extends JPanel
{
    private static final long serialVersionUID = -2359907577391270042L;
    
    double[] img;

    public DrawCanvas()
    {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                paintSquare(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                paintSquare(e.getX(),e.getY());
            }
        });
        
        img = new double[28*28];
    }

    public void clearCanvas()
    {
        img = new double[28*28];
        repaint();
    }

    protected void paintSquare(int x, int y) 
    {
        int a=x;
        int b=y;
        x=b;
        y=a;
        //int offset = 28*y+x; // convert from x,y to a linear array
        try 
        {
            img[(x/10)+28*(y/10)] = 1.; // The image is scaled down to 28x28
            if((x-10)/10 > 0) img[((x-10)/10)+28*(y/10)] = 1.;
            if((y-10)/10 > 0) img[(x/10)+28*((y-10)/10)] = 1.;
            repaint();
        }
        catch(ArrayIndexOutOfBoundsException e){}
    } 
    
    public int getY(int arrayIndex)
    {
        return arrayIndex / 28;
    }

    public int getX(int arrayIndex)
    {
        return arrayIndex % 28;
    }

    public double getImgValue(int x, int y)
    {
        return img[28*y+x];
    }

    public static double getImgValue(double[] img, int x, int y)
    {
        return img[28*y+x];
    }

    public void setImgValue(double val, int x, int y)
    {
        try
        {
            img[28*y+x] = val;
        }
        catch(ArrayIndexOutOfBoundsException e){}
    }

    public Dimension getPreferredSize() 
    {
        return new Dimension(280,280);
    }

    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);       

        for (int x=0;x<getPreferredSize().width;x+=10)
        {
            for (int y=0;y<getPreferredSize().height;y+=10)
            {
                int a=x;
                int b=y;
                if(img[(x/10)+28*(y/10)] > 0) 
                {
                    g.setColor(Color.BLACK);
                    g.fillRect(b, a, 10, 10);
                }
            }
        }
        // Draw Text
        g.drawString("Draw a letter or a digit here",5,20);
    }
}