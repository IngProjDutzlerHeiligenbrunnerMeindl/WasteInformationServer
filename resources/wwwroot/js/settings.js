$(document).ready(function() {
  //check login state
  console.log("page loaded");
  $.post('/senddata/checkloginstate','action=getloginstate',function(data){
    console.log(data);
    if (data.loggedin == true) {
      $("#userlabel").html(" "+data.username);
    }else{
      $("#userlabel").html(" not logged in!!");
    }
  },'json');



  $('#logoutbtn').click(function() {
    $.post('/senddata/checkloginstate','action=logout',function(data){
      console.log(data);
    },'json');
  });

  $('#addnewtable-btn').click(function() {
    console.log("clicked");
    $('.create-table').show();
  });

  $('.exit-icon').click(function() {
    $('.create-table').hide();
  });
});
