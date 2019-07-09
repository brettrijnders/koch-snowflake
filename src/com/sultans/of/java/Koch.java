package com.sultans.of.java;

import javax.swing.*;
import java.awt.*;

public class Koch {

    private static final int ITER_MIN = 0;
    private static final int ITER_MAX = 10;
    private static final int ITER_INIT = 3;

    public static void main(String[] args) {
        System.out.println("Starting Koch application...!");

        KochComponent koch = new KochComponent(new Point(200, 500), new Point(600, 500), 0);

        JPanel kochPanel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        JCheckBox prevIter = new JCheckBox("Show previous iteration");
        prevIter.addItemListener(e -> {
            if (prevIter.isSelected()) {
                koch.showPrevIter(true);
                koch.recreate();
            } else {
                koch.showPrevIter(false);
                koch.recreate();
            }
        });

        JSlider iterSlider = new JSlider(JSlider.HORIZONTAL,
                ITER_MIN, ITER_MAX, ITER_INIT);
        iterSlider.setMajorTickSpacing(5);
        iterSlider.setMinorTickSpacing(1);
        iterSlider.setPaintTicks(true);
        iterSlider.setPaintLabels(true);
        iterSlider.setValue(0);
        iterSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int newIterValue = source.getValue();
                koch.setIteration(newIterValue);
                koch.recreate();

            }
        });

        JCheckBox showAnimation = new JCheckBox("Show animated iteration");
        Timer timer = new Timer(1000, koch);
        showAnimation.addItemListener(e -> {
            if (showAnimation.isSelected()) {
                timer.start();
            } else {
                timer.stop();
            }
        });

        //  Make sure iterslider gets updated when it for instance get changed by animation.
        koch.setActionListener(e -> iterSlider.setValue(koch.getIteration()));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.FIRST_LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        controlPanel.add(prevIter, gbc);

        gbc.fill = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;

        controlPanel.add(showAnimation, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        controlPanel.add(iterSlider, gbc);

        kochPanel.add(controlPanel, BorderLayout.PAGE_START);
        kochPanel.add(koch, BorderLayout.CENTER);

        kochPanel.setBackground(Color.WHITE);

        JFrame frame = new JFrame("Koch Snowflake");
        frame.add(kochPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 1000, 1000);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.WHITE);
    }
}
