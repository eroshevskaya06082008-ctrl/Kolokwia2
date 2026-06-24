import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DrawingCanvas extends JPanel {
    List<LineData> lines = new CopyOnWriteArrayList<>();
    private int offsetX = 0;
    private int offsetY = 0;

    public void addLine(LineData line){
        lines.add(line);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(LineData line : lines){
            g2d.setColor(line.color());
            g2d.drawLine((int)Math.round(line.x1()) + offsetX, (int)Math.round(line.y1() + offsetY),
                    (int)Math.round(line.x2()) + offsetX, (int)Math.round(line.y2()) + offsetY);
        }
        g.setColor(Color.RED);
        g.drawString("Offset: " + offsetX + ", " + offsetY, 10, 20);
    }
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(500, 500);
    }

    public void moveOffSet(int dx, int dy){
        this.offsetX += dx;
        this.offsetY += dy;
        repaint();
    }




}
