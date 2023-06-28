$.ajaxSetup({ contentType: "application/json; charset=utf-8" });

$(function () {
    $.getJSON(apiUrl + '/setting/' + serviceName, function (data) { initForm(data) });
});

function initForm(data) {
    $.each(data, function (key, val) {
        let checkEl = $('#' + val.key);
        checkEl.prop('checked', val.value);
        setCheckName(val.key);
    });
}

function setCheckName(element){
    let checkEl = $('#' + element);
    let descrEl = $('#lbl' + element);
    let val = $(checkEl).is(':checked') ? "Включено" : "Выключено";
    descrEl.text(val);
}

function save(){
    let names = ["CheckUniqueMessageId", "CheckUniqueReference", "ValidateAccountState"];
    $.each(names, function (key, name) {
        let el = $('#' + name);
        let val = $(el).is(':checked') 
        $.post(apiUrl + '/setting/' + serviceName + '/' + name, JSON.stringify(val));
        console.log(name+ ': '+ val);
    });
}
