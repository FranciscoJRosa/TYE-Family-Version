<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <title>Adicionar Membro</title>
    <link rel="stylesheet" href="styles/adicionarMembroStyle.css">
  </head>
  <header>
    <h1>TYE - Family Version</h1>
    <nav>
      <ul>
        <li><a href="resumosPage.php">Resumos</a></li>
        <li><a href="adicionarMembroPage.php" class="selected">Adicionar Membro</a></li>
        <li><a href="criarFamiliaPage.php">Criar Família</a></li>
          <li><form action="includes/logout.php" method="post">
            <button type="submit" name="logout">SAIR</button>
          </form></li>
      </ul>
    </nav>
    <nav>
      <a href="#">
        <img src="img/adicionarMembro.png" alt="logo" class=img>
      </a>
      <div class="adicionarMembroBox">
        <form action="includes/adicionarMembro.php" method="post">
          <h1>Adicionar Membro à Família</h1><br>
          <input type="text" name="codigo" placeholder="Código"><br>
          <button type="submit" name="adicionar">ADICIONAR</button><br>
        </form>
      </div>
    </nav>
  </header>
  <body>

  </body>
</html>
