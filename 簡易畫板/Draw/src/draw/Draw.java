/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author User
 */
    public class Draw extends Frame implements MouseMotionListener,MouseListener,ItemListener{
        static Draw frm=new Draw();
        static Choice c1=new Choice();
        int x1,x2,y1,y2;
        Color co=Color.red;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frm.setSize(300,300);
        frm.setVisible(true);
        frm.setLayout(null);
        c1.setBounds(40,40,70,70);
        frm.addMouseMotionListener(frm);
        frm.addMouseListener(frm);
        c1.addItemListener(frm);
        frm.add(c1);
        c1.add("Red");
        c1.add("Blue");
        c1.add("Yellow");
        c1.add("Green");
        c1.add("Black");
        // TODO code application logic here
    }
    public void mousePressed(MouseEvent e) {
        x1=e.getX();
        y1=e.getY();
    }
    public void mouseDragged(MouseEvent e) {
        Graphics g=getGraphics();
        x2=e.getX();
        y2=e.getY();
        g.setColor(co);
        g.drawLine(x1,y1,x2,y2);
        x1=x2;
        y1=y2;
    }
    public void itemStateChanged(ItemEvent e) {
        String color=c1.getSelectedItem();
        if(color =="Red")
        {
            co=Color.RED;
        }
        else if(color =="Blue")
        {
            co=Color.BLUE;
        }
        else if(color =="Yellow")
        {
            co=Color.YELLOW;
        }
        else if(color =="Green")
        {
            co=Color.GREEN;
        }
        else if(color =="Black")
        {
            co=Color.BLACK;
        }
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
  
}
