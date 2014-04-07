$(":button").click(function () {
    var isbn = this.id;
    alert('About to report lost on ISBN ' + isbn);

    var bookStatUrl = "/library/v1/books/" + isbn + "?status=lost";

    $.ajax({
        type: "PUT",
        url: bookStatUrl
    })
        .done(function (msg) {
            $("#"+isbn).attr('disabled','disabled');
            $("#statusLbl-"+isbn).text("lost");
        });
});

$(document).ready(function() {
    $("table").tablecloth({
        theme: "dark",
        striped: true,
        sortable: true,
        condensed: true
    });
});

