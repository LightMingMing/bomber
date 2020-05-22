function displayChart(id, chart) {
    console.log(chart)

    let xAxis = {
        title: {
            text: chart.xAxis.title
        },
        categories: chart.xAxis.series
    }

    const yAxis = [];
    const series = [];
    let tmp;
    for (let i = 0; i < chart.yAxis.length; i++) {
        tmp = chart.yAxis[i]
        yAxis.push({title: {text: tmp.title}, opposite: tmp.opposite})
        for (let j = 0; j < tmp.series.length; j++) {
            series.push({
                yAxis: i,
                name: tmp.series[j].title,
                data: tmp.series[j].series,
                type: tmp.series[j].type
            })
        }
    }

    Highcharts.chart(id, {
        title: {
            text: chart.title
        },
        subtitle: {
            text: chart.subTitle
        },
        xAxis: xAxis,
        yAxis: yAxis,
        legend: {
            layout: 'horizontal',
            align: 'center'
        },
        plotOptions: {
            series: {
                label: {
                    connectorAllowed: false
                }
            }
        },
        series: series,
        responsive: {
            rules: [{
                condition: {
                    maxWidth: 500
                },
                chartOptions: {
                    legend: {
                        layout: 'horizontal',
                        align: 'center',
                        verticalAlign: 'bottom'
                    }
                }
            }]
        }
    });
}