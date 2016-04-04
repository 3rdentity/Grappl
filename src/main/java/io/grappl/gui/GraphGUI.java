package io.grappl.gui;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.StatMonitor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import javax.swing.*;

public class GraphGUI extends JFrame {

    public GraphGUI(StatMonitor statMonitor) {
        System.out.println("Making grpah");

        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        defaultPieDataset.setValue("Data Sent", statMonitor.getSentData());
        defaultPieDataset.setValue("Data Received", statMonitor.getReceivedData());

        JFreeChart jFreeChart = ChartFactory.createPieChart3D("Data Usage", defaultPieDataset, true, true, false);
        PiePlot3D piePlot3D = (PiePlot3D) jFreeChart.getPlot();
        piePlot3D.setStartAngle(0);
        piePlot3D.setDirection(Rotation.ANTICLOCKWISE);
        ChartPanel chartPanel = new ChartPanel(jFreeChart);
        chartPanel.setSize(640, 480);
        setContentPane(chartPanel);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {

    }

}
