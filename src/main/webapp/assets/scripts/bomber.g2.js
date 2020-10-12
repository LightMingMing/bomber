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

function displayTps(chartId, data) {
    const chart = new G2.Chart({
        container: chartId,
        autoFit: true,
        height: 350,
    });

    chart.data(preprocess(data));
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

    chart.line().position('threads*tps');

    chart.point().position('threads*tps').shape('circle').size(4);

    chart.render();
}

function displayAvg(chartId, data) {
    const chart = new G2.Chart({
        container: chartId,
        autoFit: true,
        height: 350,
    });

    chart.data(preprocess(data));

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
                return val + 'ms';
            },
        }
    });

    chart.interval().position('threads*avg');

    chart.render();
}