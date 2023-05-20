package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.*;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.StackType;
import com.github.appreciated.apexcharts.config.chart.Toolbar;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.plotoptions.Bar;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.tooltip.X;
import com.github.appreciated.apexcharts.config.xaxis.builder.AxisBorderBuilder;
import com.github.appreciated.apexcharts.config.xaxis.builder.AxisTicksBuilder;
import com.github.appreciated.apexcharts.config.xaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.thoroldvix.g2gcalculator.server.ServerResponse;

public class PopulationChart {

    public static ApexCharts getChart(ServerResponse server) {
        XAxis xAxis = XAxisBuilder.get()
                .withLabels(LabelsBuilder.get()
                        .withShow(false)
                        .build())
                .withAxisBorder(AxisBorderBuilder.get()
                        .withShow(false)
                        .build())
                .withAxisTicks(AxisTicksBuilder.get()
                        .withShow(false)
                        .build())
                .build();

        YAxis yAxis = YAxisBuilder.get()
                .withLabels(com.github.appreciated.apexcharts.config.yaxis.builder.LabelsBuilder.get()
                        .withShow(false)
                        .build())
                .withAxisBorder(com.github.appreciated.apexcharts.config.yaxis.builder.AxisBorderBuilder.get()
                        .withShow(false)
                        .build())
                .withAxisTicks(com.github.appreciated.apexcharts.config.yaxis.builder.AxisTicksBuilder.get()
                        .withShow(false)
                        .build())
                .build();

        Bar bar = BarBuilder.get()
                .withHorizontal(true)
                .build();

        PlotOptions plotOptions = PlotOptionsBuilder.get()
                .withBar(bar)
                .build();

        Toolbar toolbar = ToolbarBuilder.get()
                .withShow(false)
                .build();

        Chart chart = ChartBuilder.get()
                .withType(Type.BAR)
                .withForeColor("#D4D0C3")
                .withStacked(true)
                .withOffsetY(-34.0)
                .withStackType(StackType.FULL)
                .withToolbar(toolbar)
                .build();

        X x = new X();
        x.setShow(false);

        Grid grid = GridBuilder.get()
                .withShow(false)
                .build();

        Tooltip tooltip = TooltipBuilder.get()
                .withX(x)
                .withTheme("dark")
                .build();

        return ApexChartsBuilder.get()
                .withChart(chart)
                .withGrid(grid)
                .withTooltip(tooltip)
                .withColors("#1565CE", "#CD2B0A")
                .withXaxis(xAxis)
                .withYaxis(yAxis)
                .withLegend(LegendBuilder.get()
                        .withHorizontalAlign(HorizontalAlign.CENTER)
                        .withOffsetY(-9.0)
                        .build())
                .withPlotOptions(plotOptions)
                .withSeries(new Series<>("Alliance", server.population().popAlliance()),
                        new Series<>("Horde", server.population().popHorde())

                )
                .build();
    }
}
