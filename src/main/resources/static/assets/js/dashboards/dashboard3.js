document.addEventListener("DOMContentLoaded", function () {
  // -----------------------------------------------------------------------
  // Referring Domains
  // -----------------------------------------------------------------------
  var options_Sales_Overview = {
    series: [
      {
        name: "Direct",
        data: [44, 55, 41, 67, 22, 43, 21, 49, 60, 56, 48, 30],
      },
      {
        name: "Search",
        data: [13, 23, 20, 8, 13, 27, 33, 12, 20, 24, 48, 15],
      },
    ],
    chart: {
      fontFamily: "inherit",
      type: "bar",
      height: 320,
      foreColor: "#adb0bb",
      offsetY: 10,
      stacked: true,
      stackType: "100%",
      toolbar: {
        show: false,
      },
    },
    grid: {
      show: true,
      strokeDashArray: 3,
      borderColor: "rgba(0,0,0,.1)",
    },
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "50%",
        endingShape: "flat",
        borderRadius: 8,
        borderRadiusApplication: "around",
        borderRadiusWhenStacked: "around",
      },
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      show: true,
      width: 5,
      colors: ["transparent"],
    },
    xaxis: {
      type: "category",
      categories: [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "July",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
      ],
      axisTicks: {
        show: false,
      },
      axisBorder: {
        show: false,
      },
    },
    yaxis: {
      tickAmount: 4,
    },
    fill: {
      opacity: [1, 0.6],
      colors: ["var(--bs-primary)", "#3a47520f"],
    },
    tooltip: {
      theme: "dark",
      x: {
        show: false,
      },
    },
    legend: {
      show: false,
    },
  };

  var chart_column_basic = new ApexCharts(
    document.querySelector("#search-volume"),
    options_Sales_Overview
  );
  chart_column_basic.render();

  // Multiple Series Radar Chart -------> RADAR CHART
  var options_multiple = {
    series: [
      { name: "England", data: [32, 27, 27, 30, 25, 25] },
      { name: "United States", data: [25, 20, 20, 20, 30, 20] },
    ],

    chart: {
      fontFamily: "inherit",
      type: "radar",
      height: 315,
      toolbar: { show: !1 },
      foreColor: "#adb0bb",
    },
    plotOptions: {
      radar: {
        polygons: {
          strokeColors: "var(--bs-border-color)",
          connectorColors: "var(--bs-border-color)",
          fill: {
            colors: ["#F1F8FD", "#E4F2FB"],
          },
        },
      },
    },
    colors: ["var(--bs-secondary)", "var(--bs-primary)"],
    legend: {
      show: false,
    },
    fill: {
      colors: ["var(--bs-secondary)", "var(--bs-primary)"],
      opacity: [1, 0.4],
    },
    markers: { size: 0 },
    grid: {
      show: false,
    },
    xaxis: {
      categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
      labels: {
        show: !0,
        style: {
          fontSize: "13px",
        },
      },
    },
    yaxis: { show: !1, min: 0, max: 40, tickAmount: 4 },
    responsive: [{ breakpoint: 769, options: { chart: { height: 400 } } }],
    tooltip: {
      theme: "dark",
    },
  };

  var chart_radar_multiple_series = new ApexCharts(
    document.querySelector("#global-statistics"),
    options_multiple
  );
  chart_radar_multiple_series.render();

  // -----------------------------------------------------------------------
  // Clicks | CPC
  // -----------------------------------------------------------------------

  var chart = {
    series: [
      {
        name: "Organic",
        data: [0, 130, 80, 70, 180, 105, 250],
      },
      {
        name: "Paid",
        data: [0, 100, 60, 200, 150, 90, 150],
      },
    ],
    chart: {
      toolbar: {
        show: false,
      },
      type: "line",
      fontFamily: "inherit",
      foreColor: "#adb0bb",
      height: 265,
      stacked: false,
    },
    colors: ["var(--bs-secondary)", "var(--bs-primary)"],
    plotOptions: {},
    dataLabels: {
      enabled: false,
    },
    legend: {
      show: false,
    },
    stroke: {
      width: 2,
      curve: "smooth",
    },
    grid: {
      borderColor: "rgba(0,0,0,0.1)",
      strokeDashArray: 3,
      xaxis: {
        lines: {
          show: false,
        },
      },
    },
    xaxis: {
      axisBorder: {
        show: false,
      },
      axisTicks: {
        show: false,
      },
      categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
    },
    yaxis: {
      tickAmount: 4,
    },
    markers: {
      strokeColor: ["var(--bs-info)", "var(--bs-primary)"],
      strokeWidth: 2,
    },
    tooltip: {
      theme: "dark",
    },
  };

  var chart = new ApexCharts(document.querySelector("#clicks-cpc"), chart);
  chart.render();

  // -----------------------------------------------------------------------
  // Referring Domains
  // -----------------------------------------------------------------------
  var options_Sales_Overview = {
    series: [
      {
        name: "Net Profit",
        data: [44, 28, 22],
      },
      {
        name: "Revenue",
        data: [35, 25, 42],
      },
    ],
    chart: {
      fontFamily: "inherit",
      type: "bar",
      height: 315,
      foreColor: "#adb0bb",
      offsetY: 10,
      toolbar: {
        show: false,
      },
    },
    grid: {
      show: true,
      strokeDashArray: 3,
      borderColor: "rgba(0,0,0,.1)",
    },
    colors: ["var(--bs-primary)"],
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "50%",
        endingShape: "flat",
        borderRadius: 5,
      },
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      show: true,
      width: 5,
      colors: ["transparent"],
    },
    xaxis: {
      type: "category",
      categories: ["AUG", "SEP", "OCT"],
      axisTicks: {
        show: false,
      },
      axisBorder: {
        show: false,
      },
      labels: {
        style: {
          colors: "#a1aab2",
        },
      },
    },
    yaxis: {
      labels: {
        style: {
          colors: "#a1aab2",
        },
      },
    },
    fill: {
      opacity: 1,
      colors: ["var(--bs-primary)", "var(--bs-primary-bg-subtle)"],
    },
    tooltip: {
      theme: "dark",
    },
    legend: {
      show: false,
    },
    responsive: [
      {
        breakpoint: 767,
        options: {
          stroke: {
            show: false,
            width: 5,
            colors: ["transparent"],
          },
        },
      },
    ],
  };

  var chart_column_basic = new ApexCharts(
    document.querySelector("#month-revenue"),
    options_Sales_Overview
  );
  chart_column_basic.render();


  // 
  // Best Product
  // 
  var options = {
    series: [30, 20, 10],
    labels: ["36%", "10%", "16%", "16%", "10%", "15%"],
    chart: {
      type: "donut",
      fontFamily: "inherit",
      foreColor: "#c6d1e9",
    },
    plotOptions: {
      pie: {
        startAngle: -90,
        endAngle: 90,
        offsetY: 10,
        donut: {
          size: "75%",
          labels: {
            show: true,
            name: {
              show: true,
              fontSize: "15px",
              color: undefined,
              offsetY: 5,
              color: "var(--bs-dark)",
            },
            value: {
              show: false,
            },
            total: {
              show: true,
              color: "var(--bs-gray-600)",
              fontSize: "28px",
              fontWeight: "600",
              label: "3256",
            },
          },
        },
      },
    },
    grid: {
      padding: {
        bottom: -80,
      },
    },
    legend: {
      show: false,
    },
    dataLabels: {
      enabled: false,
      name: {
        show: false,
      },
    },
    stroke: {
      width: 0,
    },
    tooltip: {
      enabled: false,
      fillSeriesColor: false,
    },
    colors: ["var(--bs-gray-200)", "var(--bs-secondary)", "var(--bs-primary)"],
  };

  var chart = new ApexCharts(document.querySelector("#product-sales"), options);
  chart.render();
});
