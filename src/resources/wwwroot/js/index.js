$(document).ready(function () {
    $('#loginbtn').click(function (e) {
        e.preventDefault();
        console.log("clicked login button");
        const username = $("#userfield")[0].value;
        const password = $("#passfield")[0].value;

        $.post('/senddata/loginget', 'username=' + username + '&password=' + password, function (data) {
            console.log(data);
            // todo parse different errors here with popups


            if (data.accept == true) {
                console.log("successfully logged in!");
                window.location = 'dashboard.html';
            } else {
                if (data.status == "nodbconn") {
                    Swal.fire({
                        icon: "error",
                        title: 'No connection to Database',
                        html: 'Setup DB in config file!.',
                    });
                } else if (data.status == "conferror") {
                    Swal.fire({
                        icon: "error",
                        title: 'Not configured correctly',
                        html: 'Please edit settings.prop and restart the server!',
                    });
                } else {
                    Swal.fire({
                        icon: "error",
                        title: 'Wrong login data',
                        html: 'Maybe a typo in your password?',
                    });
                }
            }
        }, 'json');
    });
});

