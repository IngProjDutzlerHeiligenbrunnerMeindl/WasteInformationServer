$(document).ready(function () {
    $('#loginbtn').click(function (e) {
        e.preventDefault();
        console.log("clicked login button");
        var username = $("#userfield")[0].value;
        var password = $("#passfield")[0].value;

        $.post('/senddata/loginget', 'username=' + username + '&password=' + password, function (data) {

            console.log(data);
            if (data.status == "nodbconn"){
                Swal.fire({
                    type: "error",
                    title: 'No connection to Database',
                    html: 'Setup DB here --> <a href="index.html">click<a/>.',
                }).then((result) => {
                    console.log('Popup closed. ')

                });
            }
            if (data.accept == true) {
                console.log("successfully logged in!");
                document.cookie = "username=" + username;
                window.location = 'dashboard.html';
            }
        }, 'json');
    });


    //register pwa
    async function registerSW() {
        console.log("registering service worker!");
        if ('serviceWorker' in navigator) {
            try {
                await navigator.serviceWorker.register('/sw.js');
            } catch (e) {
                console.log(`SW registration failed`);
            }
        }
    }

    registerSW();
});

