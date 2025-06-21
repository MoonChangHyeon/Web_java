document.addEventListener("DOMContentLoaded", function () {
  // date range picker
  var start = moment().subtract(29, "days");
  var end = moment();

  function cb(start, end) {
    $(".daterange").html(
      start.format("MMMM D, YYYY") + " - " + end.format("MMMM D, YYYY")
    );
  }

  $(".daterange").daterangepicker(
    {
      startDate: start,
      endDate: end,
    },
    cb
  );

  cb(start, end);

  // -----------------------------------------------------------------------
  // Users in last 30 minutes
  // -----------------------------------------------------------------------
  var options_Users_30 = {
    series: [
      {
        name: "Users",
        data: [
          7, 3, 2, 1, 4, 5, 4, 7, 4, 5, 3, 1, 2, 3, 4, 3, 5, 1, 6, 7, 1, 2, 6,
          7,
        ],
      },
    ],
    chart: {
      fontFamily: "inherit",
      type: "bar",
      height: 78,
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
      show: false,
    },
    colors: ["var(--bs-primary)"],
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "75%",
        endingShape: "flat",
        borderRadius: 3,
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
  };

  var chart_column_basic = new ApexCharts(
    document.querySelector("#users-30"),
    options_Users_30
  );
  chart_column_basic.render();

  // -----------------------------------------------------------------------
  // Traffic Overview
  // -----------------------------------------------------------------------

  var chart = {
    series: [
      {
        name: "New Users",
        data: [5, 1, 17, 6, 15, 9, 6],
      },
      {
        name: "Users",
        data: [7, 11, 4, 16, 10, 14, 10],
      },
    ],
    chart: {
      toolbar: {
        show: false,
      },
      type: "line",
      fontFamily: "inherit",
      foreColor: "#adb0bb",
      height: 320,
      stacked: false,
    },
    colors: ["var(--bs-gray-300)", "var(--bs-primary)"],
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
      dashArray: [8, 0],
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
    yaxis: {
      title: {
        // text: 'Age',
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
      strokeColor: ["var(--bs-gray-300)", "var(--bs-primary)"],
      strokeWidth: 2,
    },
    tooltip: {
      theme: "dark",
    },
  };

  var chart = new ApexCharts(
    document.querySelector("#traffic-overview"),
    chart
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
        latLng: [-5.0, 120.0],
        name: "Indonesia",
        style: { fill: "#85d446" },
      },
      {
        latLng: [36.77, -119.41],
        name: "USA",
        style: { fill: "#745af2" },
      },
      {
        latLng: [60.0, -95.0],
        name: "Canada",
        style: { fill: "#f25a5a" },
      },
      {
        latLng: [39.0, 35.0],
        name: "Turkey",
        style: { fill: "#f2d15a" },
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

  // -------------------------------------------------
  // Performance
  // -------------------------------------------------
  var activity = {
    series: [
      {
        name: "30 days",
        data: [0, 50, 56, 100, 150],
      },
      {
        name: "7 days",
        data: [0, 30, 20, 80, 120],
      },
      {
        name: "1 day",
        data: [0, 20, 70, 30, 60],
      },
    ],
    chart: {
      fontFamily: "inherit",
      type: "line",
      height: 220,
      toolbar: { show: !1 },
      offsetX: -15,
      sparkline: {
        enabled: true,
      },
    },
    legend: { show: !1 },
    stroke: {
      curve: "straight",
      lineCap: "round",
      show: !0,
      width: 2,
      colors: ["var(--bs-primary)", "var(--bs-success)", "var(--bs-warning)"],
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
    },
    yaxis: {
      max: 180,
      min: 0,
      tickAmount: 3,
      labels: { style: { fontSize: "12px", colors: "#a1aab2" } },
    },
    
    tooltip: {
      theme: "dark",
    },
    colors: ["var(--bs-primary)", "var(--bs-success)", "var(--bs-warning)"],
    grid: {
      show: true,
      borderColor: "var(--bs-border-color)",
      strokeDashArray: 4,
      yaxis: { lines: { show: false } },
      xaxis: { lines: { show: !0 } },
      padding: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0,
      },
    },
    markers: {
      strokeColor: [
        "var(--bs-primary)",
        "var(--bs-success)",
        "var(--bs-warning)",
      ],
      strokeWidth: 3,
    },
  };

  var chart_area_spline = new ApexCharts(
    document.querySelector("#activity"),
    activity
  );
  chart_area_spline.render();

  // -----------------------------------------------------------------------
  // Users
  // -----------------------------------------------------------------------
  var chart_users = {
    series: [
      {
        name: "Users",
        labels: ["2012", "2013", "2014", "2015", "2016", "2017"],
        data: [0, 15, 2, 15],
      },
    ],
    chart: {
      fontFamily: "inherit",
      height: 50,
      type: "line",
      toolbar: {
        show: false,
      },
      sparkline: {
        enabled: true,
      },
    },
    grid: {
      show: false,
    },
    stroke: {
      curve: "smooth",
      colors: "var(--bs-success)",
      width: 2,
    },
    markers: {
      colors: "var(--bs-success)",
      strokeColors: "transparent",
    },
    fill: {
      type: "solid",
      colors: ["#FDD835"],
    },
    tooltip: {
      theme: "dark",
      style: {
        fontFamily: "inherit",
      },
      x: {
        show: false,
      },
      y: {
        formatter: undefined,
      },
      marker: {
        show: false,
      },
      followCursor: true,
    },
  };
  var chart_line_basic = new ApexCharts(
    document.querySelector("#users"),
    chart_users
  );
  chart_line_basic.render();

  // -----------------------------------------------------------------------
  // Bounce rate
  // -----------------------------------------------------------------------
  var chart_bounce_rate = {
    series: [
      {
        name: "Rate",
        labels: ["2012", "2013", "2014", "2015", "2016", "2017"],
        data: [7, 3, 2, 4, 4, 5, 4, 7, 4, 5],
      },
    ],
    chart: {
      fontFamily: "inherit",
      height: 50,
      type: "bar",
      toolbar: {
        show: false,
      },
      sparkline: {
        enabled: true,
      },
    },
    colors: ["var(--bs-secondary)"],
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "35%",
        endingShape: "flat",
        borderRadius: 2,
      },
    },
    tooltip: {
      theme: "dark",
      followCursor: true,
    },
  };
  var chart_line_basic = new ApexCharts(
    document.querySelector("#bounce-rate"),
    chart_bounce_rate
  );
  chart_line_basic.render();

  // -----------------------------------------------------------------------
  // Page / Session
  // -----------------------------------------------------------------------
  var chart_pages_session = {
    series: [
      {
        name: "Session",
        labels: ["2012", "2013", "2014", "2015", "2016", "2017"],
        data: [15, 9, 15, 2],
      },
    ],
    chart: {
      fontFamily: "inherit",
      height: 50,
      type: "line",
      toolbar: {
        show: false,
      },
      sparkline: {
        enabled: true,
      },
    },
    grid: {
      show: false,
    },
    stroke: {
      curve: "smooth",
      colors: "var(--bs-primary)",
      width: 2,
    },
    markers: {
      colors: "var(--bs-primary)",
      strokeColors: "transparent",
    },
    fill: {
      type: "solid",
      colors: ["#FDD835"],
    },
    tooltip: {
      theme: "dark",
      style: {
        fontFamily: "inherit",
      },
      x: {
        show: false,
      },
      y: {
        formatter: undefined,
      },
      marker: {
        show: false,
      },
      followCursor: true,
    },
  };
  var chart_line_basic = new ApexCharts(
    document.querySelector("#pages-session"),
    chart_pages_session
  );
  chart_line_basic.render();

  // -----------------------------------------------------------------------
  // Page views
  // -----------------------------------------------------------------------
  var page_views = {
    series: [45, 27],
    labels: ["45", "27"],
    chart: {
      type: "donut",
      fontFamily: "inherit",
      height: 150,
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      width: 0,
    },
    plotOptions: {
      pie: {
        expandOnClick: true,
        donut: {
          size: "90",
          labels: {
            show: true,
            name: {
              show: true,
              offsetY: 10,
            },
            value: {
              show: false,
            },
            total: {
              show: true,
              fontSize: "22px",
              color: "var(--bs-heading-color)",
              label: "32.3k",
              fontWeight: 600,
            },
          },
        },
      },
    },
    colors: ["var(--bs-primary)", "var(--bs-primary-bg-subtle)"],
    tooltip: {
      show: true,
      fillSeriesColor: false,
    },
    legend: {
      show: false,
    },
  };

  var chart_pie_donut = new ApexCharts(
    document.querySelector("#page-views"),
    page_views
  );
  chart_pie_donut.render();

  // -----------------------------------------------------------------------
  // New Users Channel
  // -----------------------------------------------------------------------
  var chart_new_users_channel = {
    series: [
      {
        name: "Channel",
        data: [18, 13, 4, 1, 0.5, 0],
      },
    ],
    chart: {
      fontFamily: "inherit",
      foreColor: "#768B9E",
      height: 290,
      type: "bar",
      toolbar: {
        show: false,
      },
    },
    colors: ["var(--bs-primary)"],
    plotOptions: {
      bar: {
        horizontal: true,
        columnWidth: "40%",
        borderRadius: 4,
      },
    },
    tooltip: {
      theme: "dark",
      followCursor: true,
    },
    xaxis: {
      max: 20,
      tickAmount: 5,
      categories: [
        "Organic Search",
        "Referral",
        "Direct",
        "Organic Social",
        "Organic Video",
        "Unassigned",
      ],
      axisBorder: {
        show: false,
      },
      labels: {
        formatter: function (e) {
          return e + "K";
        },
        style: {
          fontSize: "13px",
          fontWeight: "500",
          align: "left",
        },
      },
    },
    dataLabels: {
      enabled: false,
    },
    yaxis: {
      labels: {
        style: {
          fontSize: "13px",
          fontWeight: "500",
          align: "left",
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
      yaxis: {
        lines: {
          show: false,
        },
      },
    },
  };
  var chart_line_basic = new ApexCharts(
    document.querySelector("#new-users-channel"),
    chart_new_users_channel
  );
  chart_line_basic.render();
});
