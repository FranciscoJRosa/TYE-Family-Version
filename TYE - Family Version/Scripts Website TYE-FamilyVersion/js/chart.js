$(document).ready(function(){

  $.ajax({
    url : "http://localhost/TYE-FamilyVersion/includes/resumos.php",
    type : "GET",
    sucess : function(data) {
      console.log(data);
    },
    error : function(data) {
      console.log(data);
    }
  });
})
