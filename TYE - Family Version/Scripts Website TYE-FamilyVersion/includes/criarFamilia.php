<?php
  session_start();

  if (isset( $_POST['criar'])) {
    require 'dbhelper.php';

    $designacao = $_POST['designacao'];
    $moderadorID = $_SESSION['userID'];
    $userCode = $_SESSION['codigo'];

    if (empty($designacao)) {
      header("Location: ../criarFamiliaPage.php?error=emptyfields");
      exit();
    } else {
      $sql = "SELECT * FROM familia WHERE moderadorID=?;";
      $stmt = mysqli_stmt_init($conn);
      if (!mysqli_stmt_prepare($stmt, $sql)) {
        header("Location: ../criarFamiliaPage.php?error=sqlerror");
        exit();
      } else {
        mysqli_stmt_bind_param($stmt, "s", $moderadorID);
        mysqli_stmt_execute($stmt);
        mysqli_stmt_store_result($stmt);
        $resultCheck = mysqli_stmt_num_rows($stmt);
        if ($resultCheck > 0) {
          header("Location: ../criarFamiliaPage.php?error=jatemfamiliacriada");
          exit();
        } else {
          $sql = "INSERT INTO familia (designacao, moderadorID) VALUES (?,?);";
          $stmt = mysqli_stmt_init($conn);
          if (!mysqli_stmt_prepare($stmt, $sql)) {
            header("Location: ../criarFamiliaPage.php?error=sqlerror");
            exit();
          } else {
            mysqli_stmt_bind_param($stmt, "ss", $designacao, $moderadorID);
            mysqli_stmt_execute($stmt);
            mysqli_stmt_store_result($stmt);
            $sql = "SELECT id FROM familia WHERE moderadorID=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../criarFamiliaPage.php?error=sqlerror");
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
                  header("Location: ../criarFamiliaPage.php?error=sqlerror");
                  exit();
                }
                else {
                  mysqli_stmt_bind_param($stmt, "ss", $row['id'], $moderadorID);
                  mysqli_stmt_execute($stmt);
                  mysqli_stmt_store_result($stmt);
                  $sql = "UPDATE registodespesas SET familiaID=? WHERE userCode=?;";
                  $stmt = mysqli_stmt_init($conn);
                  if (!mysqli_stmt_prepare($stmt, $sql)) {
                    header("Location: ../criarFamiliaPage.php?error=sqlerror");
                    exit();
                  }
                  else {
                    mysqli_stmt_bind_param($stmt, "ss", $row['id'], $userCode);
                    mysqli_stmt_execute($stmt);
                    mysqli_stmt_store_result($stmt);
                    $sql = "UPDATE registorendimento SET familiaID=? WHERE userCode=?;";
                    $stmt = mysqli_stmt_init($conn);
                    if (!mysqli_stmt_prepare($stmt, $sql)) {
                      header("Location: ../criarFamiliaPage.php?error=sqlerror");
                      exit();
                    }
                    else {
                      mysqli_stmt_bind_param($stmt, "ss", $row['id'], $userCode);
                      mysqli_stmt_execute($stmt);
                      mysqli_stmt_store_result($stmt);
                      $sql = "UPDATE conta SET familiaID=? WHERE userCode=?;";
                      $stmt = mysqli_stmt_init($conn);
                      if (!mysqli_stmt_prepare($stmt, $sql)) {
                        header("Location: ../criarFamiliaPage.php?error=sqlerror");
                        exit();
                      }
                      else {
                        mysqli_stmt_bind_param($stmt, "ss", $row['id'], $userCode);
                        mysqli_stmt_execute($stmt);
                        mysqli_stmt_store_result($stmt);
                        header("Location: ../criarFamiliaPage.php?criar=success");
                        exit();
                      }
                    }
                  }
                }
              } else {
                header("Location: ../criarFamiliaPage.php?error=sqlerror");
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
    header("Location: ../criarFamiliaPage.php");
    exit();
  }





?>
