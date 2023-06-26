let startIdx = '';
let prevIdx = '';

$(function () {
    $('#perPage').val(perPage);
    getData(startIdx);
});

$.ajaxSetup({contentType: "application/json; charset=utf-8"});

$('#btPerPage').click(function () {
    startIdx = '';
    perPage = $('#perPage').val();
    getData(startIdx);
});

$('#btPrev').click(function () {
    getData(startIdx);
});

$('#btNext').click(function () {
    getData(prevIdx);
});

function enableButton(button, enable) {
    button.attr('disabled', !enable);
    if (enable) {
        button.removeClass('btn-light');
        button.addClass('btn-dark');
    } else {
        button.removeClass('btn-dark');
        button.addClass('btn-light');
    }
}
