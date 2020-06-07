<?php
  session_start();
?>
<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="Track Your Expenses - Family Version">
    <link rel="stylesheet" type="text/css" href="styles/styleLogin.css">
  </head>
  <body>


    <header>
      <nav>
        <a href="#">
          <img src="img/LogoNoBackground.png" alt="logo" class=fundo>
        </a>
        <div class="loginbox">
          <img src="img/Logo.png" class=avatar>
          <form action="includes/login.php" method="post">
            <h1>TYE</h1><br>
            <h1>Family Version</h1><br>
            <input type="text" name="username" placeholder="Username"><br>
            <input type="password" name="password" placeholder="Password"><br>
            <input type="text" name="userCode" placeholder="Código"><br>
            <button type="submit" name="login">ENTRAR</button><br>
          </form>
        </div>
      </nav>
    </header>
  </body>
</html>
