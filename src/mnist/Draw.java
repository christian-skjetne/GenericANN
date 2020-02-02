package mnist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

class Draw{

    public static void main(String args[]){Draw d = new Draw(null);}

    Main mnist;
    DrawCanvas canvas;

    public Draw(Main mnist)
    {
        this.mnist = mnist;

       JFrame frame = new JFrame("Draw a digit");
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
       buttonPanel.add(clearButton);
       buttonPanel.add(guessButton);
       frame.getContentPane().add(BorderLayout.CENTER,canvas); 
       frame.getContentPane().add(BorderLayout.SOUTH,buttonPanel); 
       frame.pack();
       frame.setVisible(true);
    }

    public void guess()
    {
        mnist.guess(canvas.img);
    }
}

class DrawCanvas extends JPanel
{

    double[] img;//  = new Double[280][280];

    public DrawCanvas()
    {
        // Initialize img here.
        //this.addMouseListener(this);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });
        
        img = new double[28*28];
    }

    public void clearCanvas()
    {
        img = new double[28*28];
        repaint();
    }

    protected void moveSquare(int x, int y) 
    {
        //int offset = 28*y+x;
        img[(x/10)+28*(y/10)] = 1.;
        if((x-10)/10 > 0) img[((x-10)/10)+28*(y/10)] = 1.;
        if((y-10)/10 > 0) img[(x/10)+28*((y-10)/10)] = 1.;
        repaint();
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
                if(img[(x/10)+28*(y/10)] > 0) 
                {
                    g.setColor(Color.BLACK);//new Color(255-img[x][y],255-img[x][y],255-img[x][y]));
                    g.fillRect(x, y, 10, 10);
                }
            }
        }
        // Draw Text
        //g.drawString("This is my custom Panel!",10,20);
    }
}