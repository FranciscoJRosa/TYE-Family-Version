<?php
  require "header.php";
?>

    <main>
      <?php
        if (isset($_SESSION['userID'])) {
          //echo 'You are logged in!';
        }
        else {
          //echo 'You are logged out!';
        }

      ?>
    </main>

<?php
  require "footer.php";
?>
