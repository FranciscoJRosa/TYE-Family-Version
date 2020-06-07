
<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <title>Resumos</title>
    <link rel="stylesheet" href="styles/resumosStyle.css">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="js/Chart.min.js"></script>

    <script src="js/jquery.ui.js"></script>

      <script type="text/javascript">
      $(document).ready(function list(){
        $('#tipoResumo').on('change',function list(){
            var tipoResumo = $(this).val();
            var membro = $("#membros option:selected").val();
            if (tipoResumo == "contas") {
              $('#datepicker').hide();
              $('#datepicker2').hide();
            } else {
              $('#datepicker').show();
              $('#datepicker2').show();
            }
            if(tipoResumo){
                $.ajax({
                    type:'POST',
                    url:'includes/ajaxData.php',
                    data: {'membro': membro, 'tipoResumo': tipoResumo},
                    success:function list(html){
                        $('#categorias').html(html);
                    }
                });
            }else{
                $('#categorias').html('<option value="Todas">Todas</option>');
            }
        });
        $('#membros').on('change',function list(){
            var membro = $(this).val();
            var tipoResumo = $("#tipoResumo option:selected").val();
            if(membro && tipoResumo){
                $.ajax({
                    type:'POST',
                    url:'includes/ajaxData.php',
                    data: {'membro': membro, 'tipoResumo': tipoResumo},
                    success:function list(html){
                        $('#categorias').html(html);
                    }
                });
            }else{
                $('#categorias').html('<option value="Todas">Todas</option>');
            }
        });
      });
      $( function date() {
        $( "#datepicker" ).datepicker({ dateFormat: 'yy-mm-dd'});
        $( "#datepicker2" ).datepicker({ dateFormat: 'yy-mm-dd'});
      } );
  </script>

  </head>
  <header>
    <h1>TYE - Family Version</h1>
    <nav>
      <ul>
        <li><a href="resumosPage.php" class="selected">Resumos </a></li>
        <li><a href="adicionarMembroPage.php">Adicionar Membro </a></li>
        <li><a href="criarFamiliaPage.php">Criar Família </a></li>
          <li><form action="includes/logout.php" method="post">
            <button type="submit" name="logout">SAIR</button>
          </form></li>
      </ul>
    </nav>
    <nav>
      <div class="resumosBox">
        <form action="resumosPage.php" method="post">
          <select name="tipoResumo" class="select" id="tipoResumo">
            <option selected disabled>----Tipo de Resumo----</option>
            <option value="despesas">Despesas</option>
            <option value="rendimentos">Rendimentos</option>
            <option value="contas">Contas</option>
          </select><br>
          <?php
            session_start();
            require 'dbhelper.php';
            $moderadorID = $_SESSION['userID'];
            $sql = "SELECT familiaID FROM utilizador WHERE id=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "s", $moderadorID);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              if ($row = mysqli_fetch_assoc($result)) {
                $sql = "SELECT * FROM utilizador WHERE familiaID=?;";
                $stmt = mysqli_stmt_init($conn);
                if (!mysqli_stmt_prepare($stmt, $sql)) {
                  header("Location: ../resumosPage.php?error=sqlerror");
                  exit();
                } else {
                  mysqli_stmt_bind_param($stmt, "s", $row['familiaID']);
                  mysqli_stmt_execute($stmt);
                  $result =  mysqli_stmt_get_result($stmt);
              }
            }
          }
          ?>

          <select class="select" name="membros" id="membros">
            <option selected disabled>----Membros----</option>
            <option value="todos">Todos</option>
            <?php
              while ($row = mysqli_fetch_assoc($result)) {
                 echo '<option value="'.$row['codigo'].'">'.$row['username'].'</option>';
              }
            ?>
          </select><br>

          <select class="select" name="categorias" id="categorias">
            <option selected disabled>----Categorias----</option>
          </select><br>
          <input name="dataInicio" type="text" id="datepicker" placeholder="Data de Inicio">
          <input name="dataFim" type="text" id="datepicker2" placeholder="Data de Fim">
          <button type="submit" name="visualizar">VISUALIZAR</button><br>
        </form>
      </div>
    </nav>
  </header>
  <body>
    <?php
    //session_start();
    //header('Content-Type: application/json');
    //require 'dbhelper.php';
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

    if (isset( $_POST['visualizar'])) {
      if (empty($_POST['tipoResumo']) || empty($_POST['membros']) || empty($_POST['categorias'])){
        exit();
      } else if ($_POST['tipoResumo'] == 'despesas' && $_POST["membros"] == 'todos' && $_POST['categorias'] == 'todas') {
        $sql = "SELECT SUM(valor), username FROM registodespesas WHERE familiaID=? AND data>=? AND data<=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Despesa</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data da Despesa</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registodespesas WHERE familiaID=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'rendimentos' && $_POST["membros"] == 'todos' && $_POST['categorias'] == 'todas') {
        $sql = "SELECT SUM(valor), username FROM registorendimento WHERE familiaID=? AND data>=? AND data<=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div>
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação do Rendimento</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data do Rendimento</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registorendimento WHERE familiaID=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'contas' && $_POST["membros"] == 'todos' && $_POST['categorias'] == 'todas'){
        $sql = "SELECT SUM(saldo), username FROM conta WHERE familiaID=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "s", $familiaID);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(saldo)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div>
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "Saldo contas",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Conta</th>
              <th>Saldo</th>
            </tr>
            <?php
            $sql = "SELECT * FROM conta WHERE familiaID=?;";
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
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['saldo']."€</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'despesas' && $_POST["membros"] == 'todos' && $_POST['categorias'] != 'todas') {
        $sql = "SELECT SUM(valor), username FROM registodespesas WHERE familiaID=? AND categoria=? AND data>=? AND data<=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['categorias'], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Despesa</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data da Despesa</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registodespesas WHERE familiaID=? AND categoria=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['categorias'], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if($_POST['tipoResumo'] == 'rendimentos' && $_POST["membros"] == 'todos' && $_POST['categorias'] != 'todas'){
        $sql = "SELECT SUM(valor), username FROM registorendimento WHERE familiaID=? AND categoria=? AND data>=? AND data<=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['categorias'], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação do Rendimento</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data do Rendimento</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registorendimento WHERE familiaID=? AND categoria=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['categorias'], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'contas' && $_POST["membros"] == 'todos' && $_POST['categorias'] != 'todas'){
        $sql = "SELECT SUM(saldo), username FROM conta WHERE familiaID=? AND designacao=? GROUP BY userCode;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST['categorias']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(saldo)'];
            $json2[] = $row['username'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "Saldo contas",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Conta</th>
              <th>Saldo</th>
            </tr>
            <?php
            $sql = "SELECT * FROM conta WHERE familiaID=? AND designacao=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST['categorias']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['saldo']."€</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table><?php
        }
      } else if ($_POST['tipoResumo'] == 'despesas' && $_POST["membros"] != 'todos' && $_POST['categorias'] == 'todas') {
        $sql = "SELECT SUM(valor), categoria FROM registodespesas WHERE familiaID=? AND userCode=? AND data>=? AND data<=? GROUP BY categoria;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['membros'], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['categoria'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Despesa</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data da Despesa</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registodespesas WHERE familiaID=? AND userCode=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['membros'], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'rendimentos' && $_POST["membros"] != 'todos' && $_POST['categorias'] == 'todas') {
        $sql = "SELECT SUM(valor), categoria FROM registorendimento WHERE familiaID=? AND userCode=? AND data>=? AND data<=? GROUP BY categoria;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['membros'], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['categoria'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação do Rendimento</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data do Rendimento</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registorendimento WHERE familiaID=? AND userCode=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ssss", $familiaID, $_POST['membros'], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table><?php
        }
      } else if ($_POST['tipoResumo'] == 'contas' && $_POST["membros"] != 'todos' && $_POST['categorias'] == 'todas') {
        $sql = "SELECT saldo, designacao FROM conta WHERE familiaID=? AND userCode=?;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST['membros']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['saldo'];
            $json2[] = $row['designacao'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "My PIE chart",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Conta</th>
              <th>Saldo</th>
            </tr>
            <?php
            $sql = "SELECT * FROM conta WHERE familiaID=? AND userCode=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "ss", $familiaID, $_POST['membros']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['saldo']."€</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'despesas' && $_POST["membros"] != 'todos' && $_POST['categorias'] != 'todas') {
        $sql = "SELECT SUM(valor), data FROM registodespesas WHERE familiaID=? AND categoria=? AND userCode=? AND data>=? AND data<=? GROUP BY data;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "sssss", $familiaID, $_POST['categorias'], $_POST["membros"], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['data'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: "line",
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "Soma de despesas",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Despesa</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data da Despesa</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registodespesas WHERE familiaID=? AND categoria=? AND userCode=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "sssss", $familiaID, $_POST['categorias'], $_POST["membros"], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'rendimentos' && $_POST["membros"] != 'todos' && $_POST['categorias'] != 'todas') {
        $sql = "SELECT SUM(valor), data FROM registorendimento WHERE familiaID=? AND categoria=? AND userCode=? AND data>=? AND data<=? GROUP BY data;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "sssss", $familiaID, $_POST['categorias'], $_POST["membros"], $_POST['dataInicio'], $_POST['dataFim']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['SUM(valor)'];
            $json2[] = $row['data'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: "line",
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "Soma de rendimentos",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação do Rendimento</th>
              <th>Valor</th>
              <th>Categoria</th>
              <th>Data do Rendimento</th>
            </tr>
            <?php
            $sql = "SELECT * FROM registorendimento WHERE familiaID=? AND categoria=? AND userCode=? AND data>=? AND data<=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "sssss", $familiaID, $_POST['categorias'], $_POST["membros"], $_POST['dataInicio'], $_POST['dataFim']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['valor']."€</td><td>".$row['categoria']."</td><td>".$row['data']."</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else if ($_POST['tipoResumo'] == 'contas' && $_POST["membros"] != 'todos' && $_POST['categorias'] != 'todas') {
        $sql = "SELECT saldo, designacao FROM conta WHERE familiaID=? AND designacao=? AND userCode=?;";
        $stmt = mysqli_stmt_init($conn);
        if (!mysqli_stmt_prepare($stmt, $sql)) {
          header("Location: ../resumosPage.php?error=sqlerror");
          exit();
        }
        else {
          mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['categorias'], $_POST['membros']);
          mysqli_stmt_execute($stmt);
          $result = mysqli_stmt_get_result($stmt);
          $json= [];
          $json2= [];
          while ($row = mysqli_fetch_assoc($result)) {
            $json[] = $row['saldo'];
            $json2[] = $row['designacao'];
          }
          ?> <div id="pieChart" class="chart-container">
            <div class="doughnut-chat-container">
              <canvas id="chart1"></canvas>
                <script>
                  var ctx = document.getElementById("chart1").getContext('2d');
                  var myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                      labels: <?php echo json_encode($json2); ?>,
                      datasets: [{
                        label: "Saldo da conta",
                        backgroundColor: ['#177AFF','#1BB4FF','#008ABC','#05FFEE','#B5FAFF','#8AFFC1','#41FF87','#00D607','#276A00'],
                        borderColor: '#E7FDFF',
                        data: <?php echo json_encode($json); ?>,
                      }]
                    },
                    options: {}
                  });
                </script>
            </div>
          </div>
          <table>
            <tr>
              <th>Username</th>
              <th>Designação da Conta</th>
              <th>Saldo</th>
            </tr>
            <?php
            $sql = "SELECT * FROM conta WHERE familiaID=? AND designacao=? AND userCode=?;";
            $stmt = mysqli_stmt_init($conn);
            if (!mysqli_stmt_prepare($stmt, $sql)) {
              header("Location: ../resumosPage.php?error=sqlerror");
              exit();
            }
            else {
              mysqli_stmt_bind_param($stmt, "sss", $familiaID, $_POST['categorias'], $_POST['membros']);
              mysqli_stmt_execute($stmt);
              $result = mysqli_stmt_get_result($stmt);
              while ($row = mysqli_fetch_assoc($result)) {
                echo "<tr><td>".$row['username']."</td><td>".$row['designacao']."</td><td>".$row['saldo']."€</td></tr>";
              }
              echo "</table>";
            }
             ?>
          </table> <?php
        }
      } else {
        echo "Não existe informação";
      }
    } else {
      ?>
      <a>
        <img src="img/resumos.png" alt="logo" class=img>
      </a> <?php
    }
    ?>
    </script>
  </body>
</html>
