<?php
  session_start();
  require 'dbhelper.php';
  $moderadorID = $_SESSION['userID'];

  $sql = "SELECT id FROM familia WHERE moderadorID=?;";
  $stmt = mysqli_stmt_init($conn);
  if (!mysqli_stmt_prepare($stmt, $sql)) {
    header("Location: ../resumosPage.php?error=sqlerror");
    exit();
  }
  else {
    mysqli_stmt_bind_param($stmt, "s", $moderadorID);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    while ($row = mysqli_fetch_assoc($result)) {
      $familiaID = $row['id'];
    }
  }

  if (isset($_POST["tipoResumo"]) && !empty($_POST["tipoResumo"]) && isset($_POST["membro"]) && !empty($_POST["membro"])) {
    if ($_POST["tipoResumo"] == 'despesas' && $_POST["membro"] == 'todos') {
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT DISTINCT categoria FROM registodespesas WHERE familiaID=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "s", $familiaID);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['categoria'].'">'.$row['categoria'].'</option>';
        }
      }
    } else if ($_POST["tipoResumo"] == 'despesas' && $_POST["membro"] != 'todos') {
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT DISTINCT categoria FROM registodespesas WHERE familiaID=? AND userCode=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST["membro"]);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['categoria'].'">'.$row['categoria'].'</option>';
        }
      }
    } else if ($_POST["tipoResumo"] == 'rendimentos' && $_POST["membro"] == 'todos'){
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT DISTINCT categoria FROM registorendimento WHERE familiaID=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "s", $familiaID);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['categoria'].'">'.$row['categoria'].'</option>';
        }
      }
    }else if ($_POST["tipoResumo"] == 'rendimentos' && $_POST["membro"] != 'todos'){
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT DISTINCT categoria FROM registorendimento WHERE familiaID=? AND userCode=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST["membro"]);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['categoria'].'">'.$row['categoria'].'</option>';
        }
      }
    } else if ($_POST["tipoResumo"] == 'contas' && $_POST["membro"] == 'todos'){
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT designacao FROM conta WHERE familiaID=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "s", $familiaID);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['designacao'].'">'.$row['designacao'].'</option>';
        }
      }
    } else if ($_POST["tipoResumo"] == 'contas' && $_POST["membro"] != 'todos'){
      echo '<option selected disabled>----Categorias----</option> <option value="todas">Todas</option>';
      $sql = "SELECT designacao FROM conta WHERE familiaID=? AND userCode=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../resumosPage.php?error=sqlerror");
        exit();
      }
      else {
        mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST["membro"]);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_assoc($result)) {
            echo '<option value="'.$row['designacao'].'">'.$row['designacao'].'</option>';
        }
      }
    } else {
      echo '<option selected disabled>----Categorias----</option> <option value="naoExiste">Não existe info</option>';
    }
  } else {
    echo '<option selected disabled>----Não há informação----</option>';
  }



?>
