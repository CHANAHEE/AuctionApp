<?php
    header('Content-Type:text/plain; charset=utf-8');

    $price=     $_GET['price'];
    $idx=       $_GET['index'];
    $last=      $_GET['last'];

    $db = mysqli_connect('localhost','tjdrjs0803','dkssud109!','tjdrjs0803');
    mysqli_query($db,"set names utf8");

    $sql = "UPDATE post_auction SET price='$price' , last='$last' WHERE idx='$idx'";
    $result = mysqli_query($db,$sql);

    if($result) echo "게시글이 업로드 되었습니다.";
    else echo "게시글 업로드에 실패하였습니다. 다시 시도해 주세요.";

    mysqli_close($db);
?>