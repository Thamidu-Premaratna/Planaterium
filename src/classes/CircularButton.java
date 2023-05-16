package classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;

public class CircularButton extends JButton {

    public CircularButton(String label) {
        super(label);
        setPreferredSize(new Dimension(50, 50)); // Set preferred size of the button
        setContentAreaFilled(false); // Remove the background color of the button
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray); // Change the color of the button when it's pressed
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1); // Draw the circular shape of the button
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1); // Draw the border of the button
    }
}
