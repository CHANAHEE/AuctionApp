<?php
    header('Content-Type:text/plain; charset=utf-8');

    $title=             $_POST['title'];
    $category=           $_POST['category'];
    $price=          $_POST['price'];
    $description=     $_POST['description'];
    $tradingplace=   $_POST['tradingplace'];
    $nickname=     $_POST['nickname'];
    $location=   $_POST['location'];
    $id=   $_POST['id'];
    $video=     $_POST['video'];

    // $file=      $_FILES['video'];
    // $srcName=   $file['name'];   
    // $tmpName=   $file['tmp_name'];

    // // 이미지 파일을 영구적으로 저장하기 위해 임시저장소에서 이동을 시키자. 
    // $dstName =  "./video/".date('YmdHis').$srcName;
    // move_uploaded_file($tmpName,$dstName);



    // // 이미지 파일을 영구적으로 저장하기 위해 임시저장소에서 이동을 시키자. 
   
    //move_uploaded_file($tmpName,$dstName);

    // // 메세지 중에 특수문자를 사용할수도 있음. 근데 특수문자 때문에 쿼리문이 SQL 에서 잘못 해석될 여지가 있음. 
    // // 앞에 슬래시를 추가해주자.
    $title = addslashes($title); // 슬래시가 필요한 부분에 다 넣어준다. 근데 원본은 안바뀜. 그래서 다시 넣어주자.
    $description = addslashes($description);

    // 데이터가 저장되는 시간을 만들어주자.
    $now = date('Y-m-d H:i:s');

    // // 이렇게 하면 준비가 다 끝난거임.
    // // MySQL DB 에 데이터를 저장해보자 [테이블 명 : market] 테이블은 만들었다! 
    $db = mysqli_connect('localhost','tjdrjs0803','dkssud109!','tjdrjs0803');
    mysqli_query($db,"set names utf8");

    // 저장할 데이터($name, $title, $message, $price, $dstName, $now) 를 저장하자!
    //$sql = "INSERT INTO post_auction(title,category,price,description,tradingplace,nickname,location,id,now,video) VALUES ( '$title' , '$category' , '$price' , '$description' , '$tradingplace' , '$nickname','$location','$id','$now','$dstName' )";
    $sql = "INSERT INTO post_auction(title,category,price,description,tradingplace,nickname,location,id,now,video) VALUES ( '$title' , '$category' , '$price' , '$description' , '$tradingplace' , '$nickname','$location','$id','$now','$video')";
    $result = mysqli_query($db,$sql);

    if($result) echo "게시글이 업로드 되었습니다.";
    else echo "게시글 업로드에 실패하였습니다. 다시 시도해 주세요.";

    mysqli_close($db);
?>