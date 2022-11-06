const restUrl = '/api/v1';

function show(data) {
    console.log(data);
    document.getElementById("book-add-response").value = data;
}

function hello() {
    fetch(restUrl + "/hello").then(response => {
        return response;
    }).then(data => {
        data.text().then(function (text) {
            show(text);
        })
        show(data);
    }).catch(err => {
        console.error(err);
    });
}

function add() {
    const text = document.getElementById("book-add-request").value;
    $.ajax({
        type: "POST",
        url: restUrl + "/add",
        contentType: "application/json",
        data: text,
        success: function (data) {
            show(JSON.stringify(data))
        },
        error: function (err) {
            console.error(err);
        },
        dataType: "json"
    });
}