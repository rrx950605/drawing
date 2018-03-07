package com.bbkj.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author rrx
 */
public class Ht {
    ChartPanel frame1;

    public Ht(String file, String outFile) throws Exception {
        XYDataset xydataset = createDataset(file);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart
                ("", "", "", xydataset, true, true, true);
        setAntiAlias(jfreechart);
        setLegendEmptyBorder(jfreechart);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        frame1 = new ChartPanel(jfreechart, true);
        dateaxis.setLabelFont(new Font("宋体", Font.BOLD, 14));
        dateaxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));
        ValueAxis rangeAxis = xyplot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 13));
        jfreechart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 13));
        jfreechart.getTitle().setFont(new Font("宋体", Font.BOLD, 18));
        saveAsFile(jfreechart, outFile, 1200, 500);
    }

    public static void setAntiAlias(JFreeChart chart) {
        chart.setTextAntiAlias(false);

    }

    public static void setLegendEmptyBorder(JFreeChart chart) {
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));

    }

    private static XYDataset createDataset(String file) throws Exception {
        File tempFile = new File(file.trim());
        String fileName = tempFile.getName();
        TimeSeries timeseries = new TimeSeries(fileName.split("\\.")[0],
                Millisecond.class);
        original(file, timeseries);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeseries);
        return timeseriescollection;
    }

    private static void original(String file, TimeSeries timeseries) throws IOException {
        BufferedReader bre;
        bre = new BufferedReader(new FileReader(file));
        String str;
        while ((str = bre.readLine()) != null) {
            String[] split1 = str.split("_");
            if (split1.length == 2) {
                String[] split = split1[1].split(",");
                long time = Long.parseLong(split1[0]);
                for (String s : split) {
                    time += 8;
                    timeseries.addOrUpdate(new Millisecond(new Date(time)), Integer.parseInt(s));
                }
            }
        }
    }

    public static void saveAsFile(JFreeChart chart, String outputPath,
                                  int weight, int height) throws Exception {
        FileOutputStream out;
        File outFile = new File(outputPath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        out = new FileOutputStream(outputPath);
        ChartUtilities.writeChartAsPNG(out, chart, weight, height);
        out.flush();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public ChartPanel getChartPanel() {
        return frame1;
    }
}
