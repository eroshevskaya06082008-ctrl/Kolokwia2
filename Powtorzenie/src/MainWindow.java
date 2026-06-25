import client.ServerThread;
import org.example.Dot;
import server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private Server server;
    private ServerThread serverThread;

    private JTextField addressField = new JTextField("localhost", 10);
    private JTextField portField = new JTextField("5000", 5);
    private JSlider radiusSlider = new JSlider(1, 50, 5);
    private JButton colorButton = new JButton("Выбрать цвет");


    private Color selectedColor = Color.BLACK;
    private List<Circle> circles = new ArrayList<>();


    private JPanel canvas = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Circle c : circles) {
                g2.setColor(c.color);
                g2.fillOval(c.x - c.r, c.y - c.r, 2 * c.r, 2 * c.r);
            }
        }
    };

    public MainWindow(Server server, ServerThread serverThread) {
        this.server = server;
        this.serverThread = serverThread;

        this.serverThread.setOnDotReceiver(dot -> {
            circles.add(new Circle((int) dot.x(), (int) dot.y(), (int) dot.r(), dot.color()));
            //serverThread.send(dot.x(), dot.y(), dot.r(), dot.color());
            SwingUtilities.invokeLater(() -> {
                canvas.repaint();
            });
        });
        new Thread(serverThread).start();
        setTitle("Circle app");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Панель подключения
        JPanel connectionPanel = new JPanel();
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        connectionPanel.add(new JLabel("Address:"));
        connectionPanel.add(addressField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);

        JButton connectBtn = new JButton("Connect");
        JButton startServerBtn = new JButton("Create & Connect");
        connectionPanel.add(connectBtn);
        connectionPanel.add(startServerBtn);

        // 2. Панель управления и холста
        JPanel canvasContainer = new JPanel(new BorderLayout());
        canvasContainer.setBorder(BorderFactory.createTitledBorder("Canvas"));

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(new JLabel("Radius:"));
        controlsPanel.add(radiusSlider);

        // Логика кнопки цвета
        colorButton.setBackground(selectedColor);
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Выберите цвет", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorButton.setBackground(selectedColor);
            }
        });
        controlsPanel.add(new JLabel("Color:"));
        controlsPanel.add(colorButton);

        // Настройка холста
        canvas.setBackground(Color.WHITE);
        canvas.setPreferredSize(new Dimension(512, 512));
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String message = Dot.toMessage(e.getX(), e.getY(), radiusSlider.getValue(), selectedColor, false);
                serverThread.send(message);
            }
        });

        canvasContainer.add(controlsPanel, BorderLayout.NORTH);
        canvasContainer.add(canvas, BorderLayout.CENTER);

        // 3. Добавляем всё в окно
        add(connectionPanel, BorderLayout.NORTH);
        add(canvasContainer, BorderLayout.CENTER);
    }

    private static class Circle {
        int x, y, r;
        Color color;

        Circle(int x, int y, int r, Color color) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.color = color;
        }
    }

    public static void main(String[] args) throws IOException {

        ServerThread client = new ServerThread("localhost", 6666);
        MainWindow window = new MainWindow(null, client);
        window.setVisible(true);

    }

}