(function ($) {

  var tests = [
   {endpoint: "/empty", id: null},
   {endpoint: "/data", id: null},
   {endpoint: "/data_proxy", id: null},
   {endpoint: "/variables", id: null},
   {endpoint: "/custom_list", id: null},
   {endpoint: "/builtin", id: null},
   {endpoint: "/enum", id: null},
   {endpoint: "/statics", id: null},
   {endpoint: "/builtin_variables", id: null},
   {endpoint: "/with_common/now", id: null}
  ];

  var handleError = function(response, textStatus, errorThrown) {
      var text = response.responseText;
      alert(text);
  };

  var templateRender = function(endpoint) {
      return function(success, error) {
          $.ajax({
              type: "GET",
              url: endpoint,
              dataType: "html",
              success: success,
              error: error
          });
      };
  };

  var expectRender = function(endpoint) {
      return function(success, error) {
          $.ajax({
              type: "GET",
              url: "/expect" + endpoint + ".html",
              dataType: "text",
              success: success,
              error: error
          });
      };
  };

  var templateTest = function(endpoint) {
      return function(success, error) {
          // template
          templateRender(endpoint)(function(actualHtml) {
              expectRender(endpoint)(function(expectHtml) {
                  var assert = $.trim(actualHtml) === $.trim(expectHtml);
                  success({result: assert});
              }, error);
          }, error);
      };
  };

  $(document).ready(function() {

      var template = Handlebars.compile($("#test_list").html());
      var variables = {"tests": tests};
      $("#test_area").html(template(variables));
      return false;
  });

  $(document).on("click", ".exec-test-all", function() {
      $(tests).each(function(i, e) {
          var link = $('dl[data-endpoint="' + e.endpoint + '"] a');
          link.click();
      });

      return false;
  });

  $(document).on("click", ".exec-test", function() {
      var link = $(this);
      var parent = $(link.parents("dl")[0]);
      var endpoint = parent.data("endpoint");

      parent.removeClass("test-default");
      parent.removeClass("test-ok");
      parent.removeClass("test-ng");
      parent.removeClass("test-error");
      templateTest(endpoint)(function(test) {
          if (test.result === true) {
              parent.addClass("test-ok");
          } else {
              parent.addClass("test-ng");
          }
      }, function(response, textStatus, errorThrown) {
          parent.addClass("test-error");
      });

      return false;
  });

}(jQuery));
