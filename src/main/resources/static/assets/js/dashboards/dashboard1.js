document.addEventListener("DOMContentLoaded", function () {

  //=====================================
  // Theme Onload Toast
  //=====================================
  window.addEventListener("load", () => {
    let myAlert = document.querySelectorAll('.toast')[0];
    if (myAlert) {
      let bsAlert = new bootstrap.Toast(myAlert);
      bsAlert.show();
    }
  })

  // -------------------------------------------------
  // Domain Rating
  // -------------------------------------------------
  var options = {
    series: [30, 10],
    labels: ["36%", "10%"],
    chart: {
      type: "donut",
      fontFamily: "inherit",
      foreColor: "#c6d1e9",
      height: 160,
    },
    plotOptions: {
      pie: {
        startAngle: -90,
        endAngle: 90,
        offsetY: 10,
        donut: {
          size: "70%",
          labels: {
            show: false,
          },
        },
      },
    },
    grid: {
      padding: {
        bottom: -10,
        left: -10,
        right: -10,
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
    colors: ["var(--bs-success)", "#FFFFFF1A"],
  };

  var chart = new ApexCharts(
    document.querySelector("#domain-ratings"),
    options
  );
  chart.render();

  // -----------------------------------------------------------------------
  // traffic by location
  // -----------------------------------------------------------------------
  jQuery("#trafficbylocation").vectorMap({
    map: "world_mill_en",
    backgroundColor: "#fff",
    borderColor: "#000",
    borderOpacity: 0.9,
    borderWidth: 1,
    zoomOnScroll: false,
    color: "#ddd",
    regionStyle: {
      initial: {
        fill: "#e4f1fc",
        "stroke-width": 1,
        stroke: "#black",
      },
    },
    markerStyle: {
      initial: {
        r: 5,
        fill: "#26c6da",
        "fill-opacity": 1,
        stroke: "#fff",
        "stroke-width": 1,
        "stroke-opacity": 1,
      },
    },
    enableZoom: true,
    hoverColor: "#79e580",
    markers: [
      {
        latLng: [21.0, 78.0],
        name: "India",
        style: { fill: "#74dbf7" },
      },
      {
        latLng: [-33.0, 151.0],
        name: "Australia",
        style: { fill: "#85d446" },
      },
      {
        latLng: [36.77, -119.41],
        name: "USA",
        style: { fill: "#745af2" },
      },
      {
        latLng: [55.37, -3.41],
        name: "UK",
        style: { fill: "#f25a5a" },
      },
    ],
    hoverOpacity: null,
    normalizeFunction: "linear",
    scaleColors: ["#fff", "#ccc"],
    selectedColor: "#c9dfaf",
    selectedRegions: [],
    showTooltip: true,
    onRegionClick: function (element, code, region) {
      var message =
        'You clicked "' +
        region +
        '" which has the code: ' +
        code.toUpperCase();
      alert(message);
    },
  });

  // -----------------------------------------------------------------------
  // traffic share
  // -----------------------------------------------------------------------
  var options = {
    series: [50, 30, 20],
    labels: ["36%", "10%", "36%"],
    chart: {
      type: "radialBar",
      height: 200,
      fontFamily: "inherit",
      foreColor: "#c6d1e9",
    },
    plotOptions: {
      radialBar: {
        inverseOrder: false,
        hollow: {
          margin: 1,
          size: "15%",
        },
        dataLabels: {
          show: false,
        },
      },
    },
    legend: {
      show: false,
    },
    stroke: { width: 1, lineCap: "round" },
    tooltip: {
      enabled: false,
      fillSeriesColor: false,
    },
    colors: ["var(--bs-primary)", "var(--bs-secondary)", "var(--bs-success)"],
  };

  var chart = new ApexCharts(document.querySelector("#traffic-share"), options);
  chart.render();

  // -----------------------------------------------------------------------
  // Referring Domains
  // -----------------------------------------------------------------------
  var options_Sales_Overview = {
    series: [
      {
        name: "Domains",
        data: [7, 3, 2, 1, 4, 5, 4, 7, 4, 5, 3],
      },
    ],
    chart: {
      fontFamily: "inherit",
      type: "bar",
      height: 160,
      foreColor: "#adb0bb",
      offsetY: 10,
      toolbar: {
        show: false,
      },
      sparkline: {
        enabled: true,
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
      categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
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
      colors: ["var(--bs-primary)", "var(--bs-secondary)"],
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
    document.querySelector("#re-domain"),
    options_Sales_Overview
  );
  chart_column_basic.render();

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
      { name: "dashboardjungle", data: l[1] },
      { name: "bootstrap-admintemplates.com", data: l[2] },
      { name: "themedesigner.in", data: l[3] },
      { name: "react-themes.com", data: l[4] },
      { name: "adminmart.com", data: l[5] },
    ],
    chart: {
      fontFamily: "inherit",
      height: 270,
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

// -------------------------------------------------
// Performance
// -------------------------------------------------
var options = {
  chart: {
    fontFamily: "inherit",
      type: "line",
      height: 300,
      toolbar: { show: !1 },
      offsetX: -15,
  },
  series: [
    {
      name: "Crawled Pages",
      data: [100, 100, 40, 40, 10, 10, 120, 120],
    },
    {
      name: "Referring domains",
      data: [15, 15, 65, 65, 130, 130, 90, 90],
    },
    {
      name: "Organic Traffic Value",
      data: [50, 50, 10, 10, 50, 50, 30, 30],
    },
    {
      name: "URL Rating",
      data: [50, 50, 80, 80, 30, 30, 70, 70],
    },
  ],
  legend: { show: !1  },
  dataLabels: { enabled: !1 },
  stroke: {
    curve: "smooth",
    show: !0,
    width: 2,
    colors: [
      "var(--bs-success)",
      "var(--bs-primary)",     
      "var(--bs-danger-bg-subtle)",
      "var(--bs-secondary)",
    ],
  },
  colors: [
    "var(--bs-success)",
    "var(--bs-primary)",
    "var(--bs-danger-bg-subtle)",
    "var(--bs-secondary)",
  ],
  grid: {
    borderColor: "var(--bs-border-color)",
    strokeDashArray: 4,
    yaxis: { lines: { show: !0 } },
    padding: {
      top: 0,
      right: 0,
      bottom: 0,
      left: 0,
    },
  },
  markers: {
    strokeColor: [
      "var(--bs-success)",
      "var(--bs-primary)",
      "var(--bs-danger-bg-subtle)",
      "var(--bs-secondary)",
    ],
    strokeWidth: 3,
  },
  xaxis: {
    categories: ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
    axisBorder: { show: !1 },
    axisTicks: { show: !1 },
    tickAmount: 6,
    labels: {
      rotate: 0,
      rotateAlways: !0,
      style: { fontSize: "12px", colors: "#a1aab2" },
    },
    crosshairs: {
      position: "front",
      stroke: {
        color: [
          "var(--bs-success)",
          "var(--bs-primary)",
          "var(--bs-danger-bg-subtle)",
          "var(--bs-secondary)",
        ],
        width: 1,
        dashArray: 3,
      },
    },
  },
  yaxis: {
    max: 180,
    min: 0,
    tickAmount: 3,
    labels: { style: { fontSize: "12px", colors: "#a1aab2" } },
  },
  states: {
    normal: { filter: { type: "none", value: 0 } },
    hover: { filter: { type: "none", value: 0 } },
    active: {
      allowMultipleDataPointsSelection: !1,
      filter: { type: "none", value: 0 },
    },
  },
  tooltip: {
    theme: "dark",
    shared: true,
    intersect: false,
    y: {
      formatter: function (y) {
        if (typeof y !== "undefined") {
          return y.toFixed(0) + " points";
        }
        return y;
      },
    },
  },
};

var chart = new ApexCharts(document.querySelector("#chart"), options);

chart.render();

// check if the checkbox has any unchecked item
checkLegends();

function checkLegends() {
  var allLegends = document.querySelectorAll(".legend input[type='checkbox']");

  for (var i = 0; i < allLegends.length; i++) {
    if (!allLegends[i].checked) {
      chart.toggleSeries(allLegends[i].value);
    }
  }
}

// toggleSeries accepts a single argument which should match the series name you're trying to toggle
function toggleSeries(checkbox) {
  chart.toggleSeries(checkbox.value);
}
