// g2, cat类型的x坐标轴, 在数据重复出现时, 展示的效果不怎么好. 这里在后面追加计数用来保证x轴值不重复
function preprocess(data) {
    let result = Object.assign([], data)
    let count = 0
    for (let i = 0; i < data.length; i++) {
        let threads = `${data[i].threads}`
        if (i === data.length - 1 || data[i].threads !== data[i + 1].threads) {
            if (count > 0) {
                threads += `(${count})`
                count = 0
            }
        } else {
            threads += `(${count})`
            count++
        }
        result[i].threads = threads
    }
    return result
}

function formatTime(time) {
    const num = Number(time)
    if (num >= 1000) {
        return (num / 1000).toFixed(2) + "s"
    }
    return num.toFixed(0) + "ms"
}

function displayTps(chartId, data) {
    const chart = new G2.Chart({
        container: chartId,
        autoFit: true,
        height: 350,
    });

    chart.data(data);
    chart.scale({
        threads: {
            alias: '并发数',
            type: 'cat',
        },
        tps: {
            nice: true,
            alias: '吞吐量',
            min: 0,
            sync: true,
        }
    });

    chart.tooltip({
        showCrosshairs: true
    });

    chart.axis('threads', {
        title: {}
    });

    chart.line().position('threads*tps').label('tps');

    chart.point().position('threads*tps').shape('circle').size(4);

    chart.render();

    return chart
}

function displayAvg(chartId, data) {
    const chart = new G2.Chart({
        container: chartId,
        autoFit: true,
        height: 350,
    });

    chart.data(data);

    chart.scale({
        threads: {
            alias: '并发数',
            type: 'cat',
        }, avg: {
            min: 0,
            nice: true,
            alias: '平均响应时间',
            sync: true,
        }
    });

    chart.tooltip({
        showCrosshairs: true
    });

    chart.axis('threads', {
        title: {}
    });

    chart.axis('avg', {
        label: {
            formatter: (val) => {
                return formatTime(val);
            },
        }
    });

    chart.interval().position('threads*avg').label('avg', (avg) => {
        return {
            content: formatTime(avg),
        }
    });

    chart.render();

    return chart
}

// Tooltip联动
function displaySummaryReports(tpsChartId, avgChartId, reports) {
    const data = preprocess(reports)
    const charts = [displayTps(tpsChartId, data), displayAvg(avgChartId, data)]
    charts.forEach(chart => {
        chart.on("tooltip:show", ev => {
            chart.tooltipIsShow = true
            const snapRecords = chart.getSnapRecords({x: ev.x, y: ev.y})
            if (!snapRecords || !snapRecords.length) {
                return
            }
            const record = snapRecords[0]._origin;
            charts.filter(other => !other.tooltipIsShow).forEach(other => {
                const pos = other.getXY(record)
                if (pos) {
                    other.tooltipIsShow = true
                    other.showTooltip(pos)
                }
            })
        });

        chart.on("tooltip:hide", () => {
            chart.tooltipIsShow = false
            charts.filter(other => other.tooltipIsShow).forEach(other => {
                other.tooltipIsShow = false
                other.hideTooltip();
            })
        })
    })

    charts[0].on("tooltip:change", ev => {
        //charts[0].tooltipIsShow = true
        const snapRecords = charts[0].getSnapRecords({x: ev.x, y: ev.y})
        if (!snapRecords || !snapRecords.length) {
            return
        }
        const record = snapRecords[0]._origin;
        const pos = charts[1].getXY(record)
        if (pos) {
            charts[1].tooltipIsShow = true
            charts[1].showTooltip(pos)
        }
    });
}

function getIntersectionOnCommonThreads(records) {
    let commonThreads = new Set()
    records[0]
        .filter(r1 => !records.slice(1).some(other => !other.some(r2 => r1.threads === r2.threads)))
        .forEach(r => commonThreads.add(r.threads))
    return records.map(record => record.filter(r => commonThreads.has(r.threads)))
}

function displayCompareChart(chartId, data) {
    const chart = new G2.Chart({
        container: chartId,
        autoFit: true,
        height: 350,
    })

    chart.data(data)

    chart.scale({
        threads: {
            alias: '并发数',
            type: 'cat',
        },
        tps: {
            nice: true,
            alias: '吞吐量',
            min: 0,
            sync: true,
        }
    });

    chart.tooltip({
        showCrosshairs: true,
        shared: true,
    });

    chart.axis('threads', {
        title: {}
    });

    chart.line().position('threads*tps').color('name')

    chart.point().position('threads*tps').color('name').shape('circle').size(4)

    chart.render()
}