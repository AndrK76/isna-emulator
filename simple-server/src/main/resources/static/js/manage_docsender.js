$.ajaxSetup({contentType: "application/json; charset=utf-8"});

$(function () {
    $.getJSON(apiUrl + '/send-file/file-modes', function (modes) {
        initAddFileMode(modes)
    });
});

function initAddFileMode(modes) {
    const addFileMode = $('#fileType');
    for (let mode in modes) {
        addFileMode.append($("<option></option>")
            .attr("value", mode)
            .text(modes[mode]));
    }
}


function addFile() {

    let form = $('#sendForm')[0];
    let data = new FormData(form);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: apiUrl + "/send-file/add-new",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function (data, textStatus, jqXHR) {
            console.log("success");
            $('#result_div').html(data);
            //console.log(data);
            /*
            $("#result").html(data);
            console.log("SUCCESS : ", data);
            $("#submitButton").prop("disabled", false);
            $('#fileUploadForm')[0].reset();
             */
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("error " + jqXHR.responseText);
            /*
            $("#result").html(jqXHR.responseText);
            console.log("ERROR : ", jqXHR.responseText);
            $("#submitButton").prop("disabled", false);
            */
        }
    });
}