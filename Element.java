/*
  Landon Colburn
  ©2019
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import javax.imageio.*;

public class Element extends JComponent{

  private static final long serialVersionUID = 42l;

  private BufferedImage img,img2,img3;
  private ElementType type;

  private RescaleOp ro = new RescaleOp(1.1f, 10, null);

  private int screenX = 0;
  private int screenY = 0;
  private int x = 0;
  private int y = 0;
  private int lx,ly;
  private int size = 128;

  private int cursorX = 0;
  private int cursorY = 0;

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.drawImage(img, 0, 0, this);
  }

  public BufferedImage copyImage(BufferedImage coverImage){
    ColorModel colorModel = coverImage.getColorModel();
    boolean isAlphaPremultiplied = coverImage.isAlphaPremultiplied();
    WritableRaster raster = coverImage.copyData(null);
    BufferedImage newImage = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    return newImage;
  }

  public Element(ElementType type, int iX, int iY) {
    this.type = type;
    this.x = iX;
    this.y = iY;

    setLocation(x, y);

    setBounds(x, y, size, size);
    setOpaque(true);

    try {
      img = ImageIO.read(new File(type.getLink()));
      // img = getSprite(type.getX(), type.getY());
      img3 = copyImage(img);
      img2 = copyImage(img);
      ro.filter(img2, img2);
    } catch(IOException e){
      e.printStackTrace();
    }

    addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            System.out.println(type.getName()+" X: " +x+" Y: "+y + " ScreenX: " +lx+" ScreenY: "+ly);
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        screenX = e.getXOnScreen();
        screenY = e.getYOnScreen();

        x = getX();
        y = getY();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        check();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        img = img2;
        Window.redraw();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        img = img3;
        Window.redraw();
      }
    });

    addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        cursorX = e.getXOnScreen();
        cursorY = e.getYOnScreen();
        int deltaX = cursorX - screenX;
        int deltaY = cursorY - screenY;

        setLocation(x + deltaX, y + deltaY);
      }

      @Override
      public void mouseMoved(MouseEvent e) { }

    });
  }

  public Rectangle getBounds() {
    Rectangle rect = new Rectangle(getX()+20, getY()+20, size-20, size-20);
    return rect;
  }

  public void check(){
    Window.checkCollision(this);
  }

  public ElementType getType(){
    return type;
  }

  public BufferedImage getSprite(int x, int y){
    if(Window.spriteSheet!=null){
      return Window.spriteSheet.getSubimage(x*size, y*size, size, size);
    } else {
      return null;
    }
  }

}