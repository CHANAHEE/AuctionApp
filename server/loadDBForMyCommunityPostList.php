<?php
    header('Content-Type:application/json; charset=utf-8');
    $id = $_GET['id'];
    $db = mysqli_connect('localhost','tjdrjs0803','dkssud109!','tjdrjs0803');
    mysqli_query($db,"set names utf8");

    $sql = "SELECT idx,title,location,description,image FROM post_community WHERE id='$id'";
    $result = mysqli_query($db,$sql);

    // 결과표로 부터 총 레코드 수를 알아내자.
    $rowNum = mysqli_num_rows($result);

    // 여러줄을 읽어와야 하므로, 각 줄($row 배열) 을 요소로 갖는 빈 배열을 준비하자.
    $rows = array();
    for($i =0;$i<$rowNum;$i++){
        $row = mysqli_fetch_array($result,MYSQLI_ASSOC); // 한줄을 연관배열로...
        $rows[$i] = $row;
    }

    // 2차원 배열 --> json array 문자열로 변환
    echo json_encode($rows);

    mysqli_close($db);
?>