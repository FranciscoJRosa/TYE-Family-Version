<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <title>Criar Família</title>
    <link rel="stylesheet" href="styles/criarFamiliaStyle.css">
  </head>
  <header>
    <h1>TYE - Family Version</h1>
    <nav>
      <ul>
        <li><a href="resumosPage.php">Resumos</a></li>
        <li><a href="adicionarMembroPage.php">Adicionar Membro</a></li>
        <li><a href="criarFamiliaPage.php" class="selected">Criar Família</a></li>
          <li><form action="includes/logout.php" method="post">
            <button type="submit" name="logout">SAIR</button>
          </form></li>
      </ul>
    </nav>
    <nav>
      <a href="#">
        <img src="img/CriarFamilia.png" alt="logo" class=criarFamilia>
      </a>
      <div class="criarFamiliaBox">
        <form action="includes/criarFamilia.php" method="post">
          <h1>Criar Nova Família</h1><br>
          <input type="text" name="designacao" placeholder="Designação"><br>
          <button type="submit" name="criar">CRIAR</button><br>
        </form>
      </div>
    </nav>
  </header>
  <body>

  </body>
</html>
