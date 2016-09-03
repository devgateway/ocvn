package org.devgateway.toolkit.web.excelcharts;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;

import java.util.List;

/**
 * @author idobre
 * @since 8/16/16
 *
 * Sheet used to export Dashboards.
 */
public interface ExcelChartSheet {
    void writeCell(final Object value, final Row row, final int column);

    Row createRow(final int rowNumber);

    Row createRow();

    void setColumnWidth(final int collNumber, final int size);

    Chart createChartAndLegend();

    ChartDataSource getCategoryChartDataSource();

    List<ChartDataSource<Number>> getValuesChartDataSource();
}
