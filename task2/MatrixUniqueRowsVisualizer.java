package task2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class InvalidDimensionException extends ArithmeticException {
    public InvalidDimensionException(String message) {
        super(message);
    }
}

public class MatrixUniqueRowsVisualizer extends JFrame {
    private final DefaultTableModel matrixModel;
    private final DefaultTableModel yModel;

    public MatrixUniqueRowsVisualizer() {
        setTitle("Візуалізатор унікальних рядків матриці");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Локальні змінні для компонентів (ефективно final для лямбд)
        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel fileLabel = new JLabel("Шлях до файлу:");
        JTextField filePathField = new JTextField("matrix.txt", 20);
        JButton loadButton = new JButton("Завантажити матрицю");
        JButton processButton = new JButton("Обчислити Y");
        JButton exitButton = new JButton("Вихід"); // Нова кнопка виходу
        JLabel statusLabel = new JLabel("Готово до завантаження");

        controlPanel.add(fileLabel);
        controlPanel.add(filePathField);
        controlPanel.add(loadButton);
        controlPanel.add(processButton);
        controlPanel.add(exitButton); // Додано кнопку виходу
        controlPanel.add(statusLabel);

        // Моделі таблиць (поля, бо використовуються в кількох лямбдах)
        matrixModel = new DefaultTableModel();
        yModel = new DefaultTableModel(new String[]{"Індекс рядка", "Y[i] (унікальний?)"}, 0);

        JTable matrixTable = new JTable(matrixModel);
        matrixTable.setPreferredScrollableViewportSize(new Dimension(300, 200));
        JScrollPane matrixScroll = new JScrollPane(matrixTable);

        JTable yVectorTable = new JTable(yModel);
        yVectorTable.setPreferredScrollableViewportSize(new Dimension(200, 200));
        JScrollPane yScroll = new JScrollPane(yVectorTable);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        tablesPanel.add(matrixScroll);
        tablesPanel.add(yScroll);

        add(controlPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);

        // Лямбди для обробників (використовують локальні змінні)
        loadButton.addActionListener(e -> {
            try {
                String path = filePathField.getText().trim();
                if (path.isEmpty()) {
                    throw new FileNotFoundException("Шлях до файлу не вказано");
                }

                BufferedReader reader = new BufferedReader(new FileReader(path));
                String firstLine = reader.readLine();
                if (firstLine == null) {
                    throw new NumberFormatException("Файл порожній");
                }
                int n = Integer.parseInt(firstLine.trim());

                if (n < 1 || n > 20) {
                    throw new InvalidDimensionException("Розмірність матриці n повинна бути від 1 до 20");
                }

                List<String[]> rows = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    String line = reader.readLine();
                    if (line == null) {
                        throw new NumberFormatException("Недостатньо рядків у файлі");
                    }
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length != n) {
                        throw new NumberFormatException("Неправильна кількість елементів у рядку " + i);
                    }
                    rows.add(parts);
                }
                reader.close();

                matrixModel.setColumnIdentifiers(generateColumnHeaders(n));
                matrixModel.setRowCount(0);
                for (String[] row : rows) {
                    Integer[] intRow = new Integer[n];
                    for (int j = 0; j < n; j++) {
                        intRow[j] = Integer.parseInt(row[j]);
                    }
                    matrixModel.addRow(intRow);
                }

                statusLabel.setText("Матрицю завантажено: розмір " + n + "x" + n);
                processButton.setEnabled(true);

            } catch (FileNotFoundException ex) {
                statusLabel.setText("Помилка: Файл не знайдено - " + ex.getMessage());
                JOptionPane.showMessageDialog(MatrixUniqueRowsVisualizer.this, "Файл не знайдено: " + ex.getMessage(), "Помилка файлу", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Помилка: Невірний формат даних - " + ex.getMessage());
                JOptionPane.showMessageDialog(MatrixUniqueRowsVisualizer.this, "Невірний формат чисел у файлі: " + ex.getMessage(), "Помилка формату", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDimensionException ex) {
                statusLabel.setText("Помилка: " + ex.getMessage());
                JOptionPane.showMessageDialog(MatrixUniqueRowsVisualizer.this, ex.getMessage(), "Невірна розмірність", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                statusLabel.setText("Помилка читання файлу: " + ex.getMessage());
                JOptionPane.showMessageDialog(MatrixUniqueRowsVisualizer.this, "Помилка читання файлу: " + ex.getMessage(), "I/O Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });

        processButton.addActionListener(e -> {
            try {
                int n = matrixModel.getRowCount();
                if (n == 0) {
                    throw new IllegalStateException("Матриця не завантажена");
                }

                int[][] a = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        a[i][j] = (Integer) matrixModel.getValueAt(i, j);
                    }
                }

                boolean[] Y = new boolean[n];
                for (int i = 0; i < n; i++) {
                    boolean unique = true;
                    for (int j = 0; j < n; j++) {
                        for (int k = j + 1; k < n; k++) {
                            if (a[i][j] == a[i][k]) {
                                unique = false;
                                break;
                            }
                        }
                        if (!unique) break;
                    }
                    Y[i] = unique;
                }

                yModel.setRowCount(0);
                for (int i = 0; i < n; i++) {
                    yModel.addRow(new Object[]{i, Y[i] ? "Так" : "Ні"});
                }

                statusLabel.setText("Вектор Y обчислено успішно");

            } catch (IllegalStateException ex) {
                statusLabel.setText("Помилка: " + ex.getMessage());
                JOptionPane.showMessageDialog(MatrixUniqueRowsVisualizer.this, ex.getMessage(), "Помилка обробки", JOptionPane.ERROR_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        processButton.setEnabled(false);

        pack();
        setVisible(true);
    }

    private String[] generateColumnHeaders(int n) {
        String[] headers = new String[n];
        for (int i = 0; i < n; i++) {
            headers[i] = "Колонка " + i;
        }
        return headers;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MatrixUniqueRowsVisualizer::new);
    }
}