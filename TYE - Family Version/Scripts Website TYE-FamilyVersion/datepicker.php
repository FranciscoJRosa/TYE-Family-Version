<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>jQuery UI Datepicker - Default functionality</title>
  <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  <link rel="stylesheet" href="/resources/demos/style.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>


  <script>
  $( function date() {
    $( "#datepicker" ).datepicker({ dateFormat: 'yy-mm-dd'});
    $( "#datepicker2").datepicker();
  } );
  </script>
</head>
<body>

<p>Date: <input type="text" id="datepicker"></p>
<p>Date: <input type="text" id="datepicker2"></p>


</body>
</html>
