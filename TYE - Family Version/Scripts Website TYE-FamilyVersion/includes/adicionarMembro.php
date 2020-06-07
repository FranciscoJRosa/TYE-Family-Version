<?php
session_start();

if (isset($_POST['adicionar'])) {
  require 'dbhelper.php';

  $userCode = $_POST['codigo'];
  $moderadorID = $_SESSION['userID'];

  if (empty($userCode)) {
    header("Location: ../adicionarMembroPage.php?error=emptyfields");
    exit();
  } else {
    $sql = "SELECT * FROM utilizador WHERE codigo=?;";
    $stmt = mysqli_stmt_init($conn);
    if (!mysqli_stmt_prepare($stmt, $sql)) {
      header("Location: ../adicionarMembroPage.php?error=sqlerror");
      exit();
    } else {
      mysqli_stmt_bind_param($stmt, "s", $userCode);
      mysqli_stmt_execute($stmt);
      mysqli_stmt_store_result($stmt);
      $resultCheck = mysqli_stmt_num_rows($stmt);
      if ($resultCheck == 0) {
        header("Location: ../adicionarMembroPage.php?error=naoexisteutilizador");
        exit();
      }else {
        $sql = "SELECT familiaID FROM utilizador WHERE codigo=?;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../adicionarMembroPage.php?error=sqlerror");
          exit();
        } else {
          mysqli_stmt_bind_param($stmt, "s", $userCode);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          if($row = mysqli_fetch_assoc($result)){
            if (is_null($row['familiaID'])) {
              $sql = "SELECT id FROM utilizador WHERE codigo=?;";
              $stmt = mysqli_stmt_init($conn);
              if (!mysqli_stmt_prepare($stmt, $sql)) {
                header("Location: ../adicionarMembroPage.php?error=sqlerror");
                exit();
              } else {
                mysqli_stmt_bind_param($stmt, "s", $userCode);
                mysqli_stmt_execute($stmt);
                $result = mysqli_stmt_get_result($stmt);
                if($row = mysqli_fetch_assoc($result)){
                  $membroID = $row['id'];
                  $sql = "SELECT familiaID FROM utilizador WHERE id=?;";
                  $stmt = mysqli_stmt_init($conn);
                  if (!mysqli_stmt_prepare($stmt, $sql)) {
                    header("Location: ../adicionarMembroPage.php?error=sqlerror");
                    exit();
                  }
                  else {
                    mysqli_stmt_bind_param($stmt, "s", $moderadorID);
                    mysqli_stmt_execute($stmt);
                    $result = mysqli_stmt_get_result($stmt);
                    if ($row = mysqli_fetch_assoc($result)) {
                      $sql = "UPDATE utilizador SET familiaID=? WHERE id=?;";
                      $stmt = mysqli_stmt_init($conn);
                      if (!mysqli_stmt_prepare($stmt, $sql)) {
                        header("Location: ../adicionarMembroPage.php?error=sqlerror");
                        exit();
                      }
                      else {
                        mysqli_stmt_bind_param($stmt, "ss", $row['familiaID'], $membroID);
                        mysqli_stmt_execute($stmt);
                        mysqli_stmt_store_result($stmt);
                        $sql = "UPDATE registodespesas SET familiaID=? WHERE userCode=?;";
                        $stmt = mysqli_stmt_init($conn);
                        if (!mysqli_stmt_prepare($stmt, $sql)) {
                          header("Location: ../adicionarMembroPage.php?error=sqlerror");
                          exit();
                        }
                        else {
                          mysqli_stmt_bind_param($stmt, "ss", $row['familiaID'], $userCode);
                          mysqli_stmt_execute($stmt);
                          mysqli_stmt_store_result($stmt);
                          $sql = "UPDATE registorendimento SET familiaID=? WHERE userCode=?;";
                          $stmt = mysqli_stmt_init($conn);
                          if (!mysqli_stmt_prepare($stmt, $sql)) {
                            header("Location: ../adicionarMembroPage.php?error=sqlerror");
                            exit();
                          }
                          else {
                            mysqli_stmt_bind_param($stmt, "ss", $row['familiaID'], $userCode);
                            mysqli_stmt_execute($stmt);
                            mysqli_stmt_store_result($stmt);
                            $sql = "UPDATE conta SET familiaID=? WHERE userCode=?;";
                            $stmt = mysqli_stmt_init($conn);
                            if (!mysqli_stmt_prepare($stmt, $sql)) {
                              header("Location: ../adicionarMembroPage.php?error=sqlerror");
                              exit();
                            }
                            else {
                              mysqli_stmt_bind_param($stmt, "ss", $row['familiaID'], $userCode);
                              mysqli_stmt_execute($stmt);
                              mysqli_stmt_store_result($stmt);
                              header("Location: ../adicionarMembroPage.php?criar=success");
                              exit();
                            }
                          }
                        }
                      }
                  } else {
                    header("Location: ../adicionarMembroPage.php?error=sqlerror");
                    exit();
                  }
                }
              }
            }

            } else {
              header("Location: ../adicionarMembroPage.php?error=membrojatemfamilia");
              exit();
            }

          }
        }
      }
    }
  }
  mysqli_stmt_close($stmt);
  mysqli_close($conn);
} else {
  header("Location: ../adicionarMembroPage.php");
  exit();
}

 ?>
