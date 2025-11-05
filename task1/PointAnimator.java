package task1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PointAnimator extends JPanel {
    private static double q = 100.0;
    private static double omega = 2.0;
    private static double time = 0.0;
    private static boolean running = false;
    private static Timer tm;

    // Компоненти керування
    private JLabel lq;
    private JTextField tfq;
    private JLabel lw;
    private JTextField tfw;
    private JButton btn;

    public PointAnimator() {
        setPreferredSize(new Dimension(500, 200));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gr = (Graphics2D) g;

        // Малювання горизонтального відрізка
        int segLength = 400;
        int margin = (getWidth() - segLength) / 2;
        int y = getHeight() / 2;
        gr.drawLine(margin, y, margin + segLength, y);
        gr.drawString("Лівий кінець", margin - 40, y - 10);
        gr.drawString("Правий кінець", margin + segLength + 5, y - 10);

        // Позиція точки
        int xPixel;
        if (running && q > 0) {
            double x = q * (1 + Math.cos(omega * time)) / 2;
            xPixel = margin + (int) ((x / q) * segLength);
        } else {
            // Початкова позиція: центр
            xPixel = margin + segLength / 2;
        }
        int pointRadius = 6;
        gr.setColor(Color.RED);
        gr.fillOval(xPixel - pointRadius, y - pointRadius, 2 * pointRadius, 2 * pointRadius);
        gr.setColor(Color.BLACK);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Гармонійні коливання точки");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Панель керування з двома рядками
        JPanel controlPan = new JPanel(new GridLayout(2, 3, 5, 5)); // 2 рядки, 3 стовпці для кращого розташування
        JLabel lq = new JLabel("Довжина відрізка q:");
        JTextField tfq = new JTextField("100", 8);
        JLabel empty1 = new JLabel(""); // Порожній для балансу
        JLabel lw = new JLabel("Кутова частота w:");
        JTextField tfw = new JTextField("2.0", 8);
        JButton btn = new JButton("Старт");

        controlPan.add(lq);
        controlPan.add(tfq);
        controlPan.add(empty1);
        controlPan.add(lw);
        controlPan.add(tfw);
        controlPan.add(btn);

        PointAnimator pan = new PointAnimator();

        frame.add(controlPan, BorderLayout.NORTH);
        frame.add(pan, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        pan.lq = lq;
        pan.tfq = tfq;
        pan.lw = lw;
        pan.tfw = tfw;
        pan.btn = btn;

        btn.addActionListener((ActionEvent e) -> {
            if (!running) {
                try {
                    q = Double.parseDouble(tfq.getText().trim());
                    omega = Double.parseDouble(tfw.getText().trim());
                    if (q <= 0 || omega <= 0) {
                        throw new NumberFormatException("Невірні значення");
                    }
                    time = 0.0;
                    running = true;
                    btn.setText("Зупинити");
                    tm.start();
                } catch (NumberFormatException ex) {
                    tfq.setText("100");
                    tfw.setText("2.0");
                }
            } else {
                running = false;
                btn.setText("Старт");
                tm.stop();
                pan.repaint();
            }
        });

        tm = new Timer(50, (ActionEvent arg0) -> {
            if (!running) return;
            time += 0.05;
            pan.repaint();
        });
    }
}