$(document).ready(function () {
    $('#btn-newdevice').click(function (e) {
        e.preventDefault();

        Swal.showLoading({
                title: 'No connection to Database',
                html: 'Setup DB here --> <a href="index.html">click<a/>.',
            });

        // Swal.fire({
        //     type: "error",
        //     title: 'No connection to Database',
        //     html: 'Setup DB here --> <a href="index.html">click<a/>.',
        //     onBeforeOpen: () => {
        //         Swal.showLoading()
        //     },
        // }).then((result) => {
        //     console.log('Popup closed. ')
        //
        // });


        // $.post('/senddata/loginget', 'username=' + username + '&password=' + password, function (data) {
        //
        //     console.log(data);
        //     if (data.status == "nodbconn"){
        //
        //     }
        //     if (data.accept == true) {
        //         console.log("successfully logged in!");
        //         document.cookie = "username=" + username;
        //         window.location = 'dashboard.html';
        //     }
        // }, 'json');
    });
});

