$(function () {
  
  // 룰 검색 테이블 초기화
  $("#rule_search_table").DataTable({
    dom: "Bfrtip",
    buttons: ["copy", "csv", "excel", "pdf", "print"],
    order: [[0, "asc"]],
    language: {
      search: "검색:",
      lengthMenu: "_MENU_ 개씩 보기",
      info: "총 _TOTAL_개 중 _START_에서 _END_까지 표시",
      infoEmpty: "표시할 데이터가 없습니다.",
      infoFiltered: "(총 _MAX_개에서 필터링됨)",
      paginate: {
        first: "처음",
        last: "마지막",
        next: "다음",
        previous: "이전",
      },
    },
  });

  // 시스템 로그 테이블 초기화
  $("#logs_table").DataTable({
    dom: "Bfrtip",
    buttons: ["copy", "csv", "excel", "pdf", "print"],
    order: [[0, "desc"]],
    language: {
      search: "검색:",
      lengthMenu: "_MENU_ 개씩 보기",
      info: "총 _TOTAL_개 중 _START_에서 _END_까지 표시",
      infoEmpty: "표시할 데이터가 없습니다.",
      infoFiltered: "(총 _MAX_개에서 필터링됨)",
      paginate: {
        first: "처음",
        last: "마지막",
        next: "다음",
        previous: "이전",
      },
    },
  });

  // ✨ 상세 보기 테이블 초기화 (새로 추가)
$("#details_mapping_table").DataTable({
  dom: "rtip", // 검색(f)과 내보내기(B) 버튼은 제외
  pageLength: 5, // 한 페이지에 5개씩 표시
  language: {
      info: "총 _TOTAL_개",
      infoEmpty: "",
      infoFiltered: "",
      paginate: {
          next: "다음",
          previous: "이전",
      },
  },
});

    // ✨ 검색 결과 테이블 초기화 (새로 추가)
    $("#search_results_table").DataTable({
      dom: "rtip", // 검색(f)과 내보내기(B) 버튼은 제외
      pageLength: 10, // 한 페이지에 10개씩 표시
      language: {
        info: "총 _TOTAL_개",
        infoEmpty: "",
        infoFiltered: "",
        paginate: {
          next: "다음",
          previous: "이전",
        },
      },
    });

  // ✨ 아코디언 내 테이블들 초기화 (새로 추가)
  const miniDataTableOptions = {
    dom: "frtip", // 내보내기 버튼(B) 제외, f(검색), r(처리중), t(테이블), i(정보), p(페이징)
    pageLength: 5, // 한 페이지에 5개씩 표시
    language: {
      search: "규칙 검색:",
      lengthMenu: "_MENU_ 개씩 보기",
      info: "총 _TOTAL_개",
      infoEmpty: "",
      infoFiltered: "(총 _MAX_개에서 필터링)",
      paginate: {
        next: "다음",
        previous: "이전",
      },
    },
  };

  $("#rules_table_a").DataTable(miniDataTableOptions);
  $("#rules_table_b").DataTable(miniDataTableOptions);
  $("#rules_table_common").DataTable(miniDataTableOptions);

});

