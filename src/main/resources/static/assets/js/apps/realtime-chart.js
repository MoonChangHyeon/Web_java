document.addEventListener("DOMContentLoaded", function () {

    var user_realtime = {
        series: [
            {
                name: "",
                data: [3500, 2500, 4000, 2500, 5500, 3500, 2500],
            },
            {
                name: "",
                data: [3000, 1500, 3100, 5000, 3000, 5500, 3500],
            },
        ],
        chart: {
            fontFamily: "inherit",
            foreColor: "#adb0bb",
            height: 129,
            type: "line",
            toolbar: {
                show: false,
            },
        },
        legend: {
            show: false,
        },
        stroke: {
            width: 2,
            curve: "smooth",
        },
        grid: {
            show: false,
            strokeDashArray: 3,
            borderColor: "#90A4AE50",
        },
        colors: ["var(--bs-primary)", "var(--bs-gray-300)"],
        markers: {
            size: 0,
        },
        yaxis: {
            show: false,
        },
        xaxis: {
            type: "category",
            labels: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        tooltip: {
            theme: "dark",
        },
    };
    new ApexCharts(document.querySelector("#user-realtime"), user_realtime).render();


    // =====================================
    // products chart
    // =====================================

    var device_activity = {
        color: "#adb5bd",
        series: [70, 12, 18],
        labels: ["Desktop", "Tablet", "Mobile"],
        chart: {
            height: 165,
            type: "donut",
            fontFamily: "inherit",
            foreColor: "#adb0bb",
        },
        plotOptions: {
            pie: {
                startAngle: 0,
                endAngle: 360,
                donut: {
                    size: "83%",
                },
            },
        },
        stroke: {
            show: true,
            colors: "var(--bs-card-bg)",
            width: 3,
        },
        dataLabels: {
            enabled: false,
        },

        legend: {
            show: false,
        },
        colors: ["var(--bs-primary)", "var(--bs-warning)", "var(--bs-success)"],

        tooltip: {
            theme: "dark",
            fillSeriesColor: false,
        },
    };

    var chart = new ApexCharts(document.querySelector("#device-activity"), device_activity);
    chart.render();



    // -----------------------------------------------------------------------
    // This is for the map
    // -----------------------------------------------------------------------

    $("#usa").vectorMap({
        map: "us_aea_en",
        backgroundColor: "transparent",
        zoomOnScroll: false,
        regionStyle: {
            initial: {
                fill: "#E4F1FC",
            },
        },
        markers: [
            {
                latLng: [40.71, -74.0],
                name: "Newyork",
                style: { fill: "#2952ff80" },
            },
            {
                latLng: [30.51, -91.52],
                name: "Louisiana",
                style: { fill: "#2952ff80" },
            },
            {
                latLng: [39.01, -98.48],
                name: "Kansas",
                style: { fill: "#2952ff80" },
            },
            {
                latLng: [34.04, -111.09],
                name: "Arizona ",
                style: { fill: "#2952ff80" },
            },
        ],
    });


    // =====================================
    // First User Source
    // =====================================

    var first_user_source = {
        series: [
            {
                name: "",
                data: [29, 52, 38, 47, 56, 29, 52],
            },
            {
                name: "",
                data: [71, 71, 71, 71, 71, 71, 71],
            },
        ],
        chart: {
            fontFamily: "inherit",
            type: "bar",
            height: 60,
            stacked: true,
            toolbar: {
                show: false,
            },
            sparkline: {
                enabled: true,
            },
        },
        grid: {
            show: false,
            borderColor: "rgba(0,0,0,0.1)",
            strokeDashArray: 1,
            xaxis: {
                lines: {
                    show: false,
                },
            },
            yaxis: {
                lines: {
                    show: true,
                },
            },
            padding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0,
            },
        },
        colors: ["var(--bs-primary)","#3a47520f",
        ],
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "30%",
                borderRadius: 2,
                borderRadiusApplication: "end",
                borderRadiusWhenStacked: "all",
            },
        },
        dataLabels: {
            enabled: false,
        },
        xaxis: {
            labels: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        yaxis: {
            labels: {
                show: false,
            },
        },
        tooltip: {
            theme: "dark",
        },
        legend: {
            show: false,
        },
    };

    var chart_column_stacked = new ApexCharts(
        document.querySelector("#first-user-source"),
        first_user_source
    );
    chart_column_stacked.render();



    // =====================================
    // users-by-audience
    // =====================================

    var users_by_audience = {
        series: [
            {
                name: "",
                data: [29, 52, 38, 47, 56, 29, 52],
            },
            {
                name: "",
                data: [71, 71, 71, 71, 71, 71, 71],
            },
        ],
        chart: {
            fontFamily: "inherit",
            type: "bar",
            height: 60,
            stacked: true,
            toolbar: {
                show: false,
            },
            sparkline: {
                enabled: true,
            },
        },
        grid: {
            show: false,
            borderColor: "rgba(0,0,0,0.1)",
            strokeDashArray: 1,
            xaxis: {
                lines: {
                    show: false,
                },
            },
            yaxis: {
                lines: {
                    show: true,
                },
            },
            padding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0,
            },
        },
        colors: ["var(--bs-secondary)","#3a47520f",
        ],
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "30%",
                borderRadius: 2,
                borderRadiusApplication: "end",
                borderRadiusWhenStacked: "all",
            },
        },
        dataLabels: {
            enabled: false,
        },
        xaxis: {
            labels: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        yaxis: {
            labels: {
                show: false,
            },
        },
        tooltip: {
            theme: "dark",
        },
        legend: {
            show: false,
        },
    };

    var chart_column_stacked = new ApexCharts(
        document.querySelector("#users-by-audience"),
        users_by_audience
    );
    chart_column_stacked.render();




    // =====================================
    // views-by-page-title
    // =====================================

    var views_by_page_title = {
        series: [
            {
                name: "",
                data: [29, 52, 38, 47, 56, 29, 52],
            },
            {
                name: "",
                data: [71, 71, 71, 71, 71, 71, 71],
            },
        ],
        chart: {
            fontFamily: "inherit",
            type: "bar",
            height: 60,
            stacked: true,
            toolbar: {
                show: false,
            },
            sparkline: {
                enabled: true,
            },
        },
        grid: {
            show: false,
            borderColor: "rgba(0,0,0,0.1)",
            strokeDashArray: 1,
            xaxis: {
                lines: {
                    show: false,
                },
            },
            yaxis: {
                lines: {
                    show: true,
                },
            },
            padding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0,
            },
        },
        colors: ["var(--bs-success)","#3a47520f",
        ],
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "30%",
                borderRadius: 2,
                borderRadiusApplication: "end",
                borderRadiusWhenStacked: "all",
            },
        },
        dataLabels: {
            enabled: false,
        },
        xaxis: {
            labels: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        yaxis: {
            labels: {
                show: false,
            },
        },
        tooltip: {
            theme: "dark",
        },
        legend: {
            show: false,
        },
    };

    var chart_column_stacked = new ApexCharts(
        document.querySelector("#views-by-page-title"),
        views_by_page_title
    );
    chart_column_stacked.render();




    // =====================================
    // events-by-event-name
    // =====================================

    var events_by_event_name = {
        series: [
            {
                name: "",
                data: [29, 52, 38, 47, 56, 29, 52],
            },
            {
                name: "",
                data: [71, 71, 71, 71, 71, 71, 71],
            },
        ],
        chart: {
            fontFamily: "inherit",
            type: "bar",
            height: 60,
            stacked: true,
            toolbar: {
                show: false,
            },
            sparkline: {
                enabled: true,
            },
        },
        grid: {
            show: false,
            borderColor: "rgba(0,0,0,0.1)",
            strokeDashArray: 1,
            xaxis: {
                lines: {
                    show: false,
                },
            },
            yaxis: {
                lines: {
                    show: true,
                },
            },
            padding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0,
            },
        },
        colors: ["var(--bs-info)","#3a47520f",
        ],
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "30%",
                borderRadius: 2,
                borderRadiusApplication: "end",
                borderRadiusWhenStacked: "all",
            },
        },
        dataLabels: {
            enabled: false,
        },
        xaxis: {
            labels: {
                show: false,
            },
            axisBorder: {
                show: false,
            },
            axisTicks: {
                show: false,
            },
        },
        yaxis: {
            labels: {
                show: false,
            },
        },
        tooltip: {
            theme: "dark",
        },
        legend: {
            show: false,
        },
    };

    var chart_column_stacked = new ApexCharts(
        document.querySelector("#events-by-event-name"),
        events_by_event_name
    );
    chart_column_stacked.render();


});