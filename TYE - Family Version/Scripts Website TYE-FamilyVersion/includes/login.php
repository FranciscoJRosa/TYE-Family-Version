<?php

if (isset($_POST['login'])) {
  require 'dbhelper.php';

  $username = $_POST['username'];
  $password = $_POST['password'];
  $userCode = $_POST['userCode'];

  if (empty($username) || empty($password) || empty($userCode)) {
    header("Location: ../index.php?error=emptyfields");
    exit();
  }
  else {
    $sql = "SELECT * FROM utilizador WHERE username=? AND codigo=?;";
    $stmt = mysqli_stmt_init($conn);
    if (!mysqli_stmt_prepare($stmt, $sql)) {
      header("Location: ../index.php?error=sqlerror");
      exit();
    }
    else {
      mysqli_stmt_bind_param($stmt, "ss", $username, $userCode);
      mysqli_stmt_execute($stmt);
      $result = mysqli_stmt_get_result($stmt);
      if ($row = mysqli_fetch_assoc($result)) {
        if ($password != $row['password']) {
          header("Location: ../index.php?error=wrongpasswordorcodigo");
          exit();
        }
        else if ($password == $row['password']) {
          session_start();
          $_SESSION['userID'] = $row['id'];
          $_SESSION['username'] = $row['username'];
          $_SESSION['codigo'] = $row['codigo'];

          $sql = "SELECT * FROM familia WHERE moderadorID=?;";
          $stmt = mysqli_stmt_init($conn);
          if (!mysqli_stmt_prepare($stmt, $sql)) {
            header("Location: ../index.php?error=sqlerror");
            exit();
          } else {
            mysqli_stmt_bind_param($stmt, "s", $_SESSION['userID']);
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            if ($row = mysqli_fetch_assoc($result)) {
              $familiaID = $row['id'];
              $sql = "SELECT * FROM utilizador WHERE familiaID=?;";
              $stmt = mysqli_stmt_init($conn);
              if (!mysqli_stmt_prepare($stmt, $sql)) {
                header("Location: ../index.php?error=sqlerror");
                exit();
              } else {
                mysqli_stmt_bind_param($stmt, "s", $familiaID);
                mysqli_stmt_execute($stmt);
                $result = mysqli_stmt_get_result($stmt);
                while ($row = mysqli_fetch_assoc($result)) {
                  $sql = "UPDATE registodespesas SET familiaID=? WHERE userCode=?;";
                  $stmt = mysqli_stmt_init($conn);
                  if (!mysqli_stmt_prepare($stmt, $sql)) {
                    header("Location: ../index.php?error=sqlerror");
                    exit();
                  } else {
                    mysqli_stmt_bind_param($stmt, "ss", $familiaID, $row['codigo']);
                    mysqli_stmt_execute($stmt);
                    mysqli_stmt_store_result($stmt);
                    $sql = "UPDATE registorendimento SET familiaID=? WHERE userCode=?;";
                    $stmt = mysqli_stmt_init($conn);
                    if (!mysqli_stmt_prepare($stmt, $sql)) {
                      header("Location: ../index.php?error=sqlerror");
                      exit();
                    } else {
                      mysqli_stmt_bind_param($stmt, "ss", $familiaID, $row['codigo']);
                      mysqli_stmt_execute($stmt);
                      mysqli_stmt_store_result($stmt);
                      $sql = "UPDATE conta SET familiaID=? WHERE userCode=?;";
                      $stmt = mysqli_stmt_init($conn);
                      if (!mysqli_stmt_prepare($stmt, $sql)) {
                        header("Location: ../index.php?error=sqlerror");
                        exit();
                      }
                      else {
                        mysqli_stmt_bind_param($stmt, "ss", $familiaID, $row['codigo']);
                        mysqli_stmt_execute($stmt);
                        mysqli_stmt_store_result($stmt);
                      }
                    }

                  }
                }
                header("Location: ../paginaInicial.php?login=sucess");
                exit();
              }

            } else {
              header("Location: ../paginaInicial.php?login=sucessnofamily");
              exit();
            }
          }
        }
        else {
          header("Location: ../index.php?error=wrongpasswordorcodigo");
          exit();
        }
      }
      else {
        header("Location: ../index.php?error=nouser");
        exit();
      }
    }
  }
  mysqli_stmt_close($stmt);
  mysqli_close($conn);
}
else {
  header("Location: ../index.php");
  exit();
}

?>
