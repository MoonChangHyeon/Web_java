document.addEventListener("DOMContentLoaded", function () {


    var options_spline = {
        series: [
            {
                name: "",
                data: [165, 160, 170, 150, 170, 145, 135, 165, 160, 170, 150, 170, 145, 135],
            },
            {
                name: "",
                data: [126, 175, 95, 115, 129, 165, 126, 126, 175, 95, 115, 129, 165, 126],
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
            row: {
                colors: ["transparent"],
                opacity: 0.5,
            },
            borderColor: "var(--bs-border-color)",
        },
        fill: {
            type: "gradient",
            gradient: {
                shadeIntensity: 0,
                inverseColors: false,
                opacityFrom: 0.1,
                opacityTo: 0.01,
                stops: [0, 25],
            },
        },
        colors: ["var(--bs-primary)", "var(--bs-warning)"],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: "straight",
            width: 2,
        },
        xaxis: {
            categories: [
                "",
                "11 Sep 2023",
                "",
                "7 Oct 2023",
                "",
                "2 Nov 2023",
                "",
                "28 Nov 2023",
                "",
                "24 Dec 2023",
                "",
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
        yaxis: {
            min: 0,
            max: 180,
            tickAmount: 4,
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
        document.querySelector("#chart-area-spline"),
        options_spline
    );
    chart_area_spline.render();


});