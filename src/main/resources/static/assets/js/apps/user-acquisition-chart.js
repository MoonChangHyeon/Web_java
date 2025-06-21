document.addEventListener("DOMContentLoaded", function () {
    // Spline Area Chart -------> AREA CHART
    var options_spline = {
        series: [
            {
                name: "Organic Search",
                data: [41, 60, 20, 90, 115, 80, 83],
                color: "var(--bs-primary)",
            },
            {
                name: "Direct",
                data: [32, 21, 100, 80, 90, 70, 70],
                color: "var(--bs-secondary)",
            },
            {
                name: "Referral",
                data: [28, 35, 15, 18, 20, 38, 35],
                color: "var(--bs-success)",
            },
            {
                name: "Organic Social",
                data: [15, 10, 18, 8, 9, 28, 25],
                color: "var(--bs-warning)",
            },
        ],
        chart: {
            fontFamily: "inherit",
            height: 300,
            type: "area",
            toolbar: {
                show: false,
            },
        },

        grid: {
            show: false,
        },
        colors: ["#615dff", "#3dd9eb"],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            width: 2,
            curve: "smooth",
        },
        fill: {
            type: "gradient",
            gradient: {
              shadeIntensity: 0,
              inverseColors: false,
              opacityFrom: 0.1,
              opacityTo: 0.01,
              stops: [0, 100],
            },
          },
        xaxis: {
            show: false,
            axisTicks: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            labels: {
                show: false,
            },
        },
        yaxis: {
            show: false,
            labels: {
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
            show: true,
            labels: {
                colors: ["#a1aab2"],
            },
        },
    };

    var chart_area_spline = new ApexCharts(
        document.querySelector("#chart-area-spline"),
        options_spline
    );
    chart_area_spline.render();

    // Basic Bar Chart -------> BAR CHART
    var options_basic = {
        series: [
            {
                data: [20000, 14000, 9000, 3500, 2000, 800],
            },
        ],
        chart: {
            fontFamily: "inherit",
            type: "bar",
            height: 345,
            toolbar: {
                show: false,
            },
        },
        colors: ["var(--bs-primary)"],
        plotOptions: {
            bar: {
                horizontal: true,
            },
        },
        dataLabels: {
            enabled: false,
        },
        xaxis: {
            categories: [
                "Organic Search",
                "Referral",
                "Direct",
                "Organic Social",
                "Organic Video",
                "Unassigned",
            ],
            labels: {
                style: {
                    colors: [
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                    ],
                },
            },
        },
        grid: {
            show: true,
            borderColor: "var(--bs-border-color)",
            xaxis: {
                lines: {
                    show: true,
                },
            },
        },
        yaxis: {
            min: 0,
            max: 25000,
            labels: {
                style: {
                    colors: [
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                        "#a1aab2",
                    ],
                },
            },
        },
        tooltip: {
            theme: "dark",
        },
    };

    var chart_bar_basic = new ApexCharts(
        document.querySelector("#chart-bar-basic"),
        options_basic
    );
    chart_bar_basic.render();

});