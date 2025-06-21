document.addEventListener("DOMContentLoaded", function () {
    // -----------------------------------------------------------------------
    // Top organic competitors
    // -----------------------------------------------------------------------
    var l = [
        [[0, 40, 20]],
        [[20, 70, 35]],
        [[90, 200, 25]],
        [[110, 205, 35]],
        [[200, 200, 25]],
        [[320, 310, 15]],
    ];
    var sales_summery = {
        series: [
            { name: "wrappixel.com", data: l[0] },
            { name: "mui.com", data: l[1] },
            { name: "getbootstrap.io", data: l[2] },
            { name: "creative-tim.com   ", data: l[3] },
            { name: "coreui.io", data: l[4] },
            { name: "flatlogic.com", data: l[5] },
        ],
        chart: {
            fontFamily: "inherit",
            height: 264,
            type: "bubble",
            foreColor: "#adb0bb",
            toolbar: {
                show: false,
            },
        },
        grid: {
            show: true,
            borderColor: "rgba(0,0,0,0.05)",
            padding: {
                right: 20,
            },
            xaxis: {
                lines: {
                    show: true,
                },
                formatter: function (e) {
                    return "$" + e + " k";
                },
            },
            yaxis: {
                lines: {
                    show: true,
                },
            },
        },
        colors: [
            "var(--bs-primary)",
            "var(--bs-success)",
            "var(--bs-warning)",
            "var(--bs-secondary)",
            "var(--bs-info)",
            "var(--bs-danger)",
        ],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: "smooth",
            width: 2,
        },
        markers: {
            size: 3,
            strokeColors: "transparent",
        },
        xaxis: {
            axisBorder: {
                show: false,
            },
        },
        tooltip: {
            x: {
                format: "dd/MM/yy HH:mm",
            },
            theme: "dark",
        },
        legend: {
            show: false,
        },
    };

    var chart_area_spline = new ApexCharts(
        document.querySelector("#organic-competitors"),
        sales_summery
    );
    chart_area_spline.render();
});