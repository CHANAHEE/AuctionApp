<?php
    header('Content-Type:text/plain; charset=utf-8');

    $post_index=     $_POST['idx'];
    $description=     $_POST['description'];
    $nickname=     $_POST['nickname'];
    $location=   $_POST['location'];
    $id=   $_POST['id'];
    
    $description = addslashes($description);

    $now = date('Y-m-d H:i:s');

    $db = mysqli_connect('localhost','tjdrjs0803','dkssud109!','tjdrjs0803');
    
    $result = mysqli_query($db,"set names utf8");

    $sql = "INSERT INTO post_auction_comments(post_index,description,nickname,location,id,now) VALUES ('$post_index', '$description' , '$nickname' , '$location' , '$id' , '$now')";
    $result = mysqli_query($db,$sql);

    if($result) echo "게시글이 업로드 되었습니다.";
    else echo "게시글 업로드에 실패하였습니다. 다시 시도해 주세요.";

    mysqli_close($db);
?>