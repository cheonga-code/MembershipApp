 var IMP = window.IMP; // 생략가능
    IMP.init('imp13524189'); // 'iamport' 대신 부여받은 "가맹점 식별코드"를 사용

    /* 중략 */

    //onclick, onload 등 원하는 이벤트에 호출합니다
    IMP.request_pay({
    pg : 'kakaopay', // version 1.1.0부터 지원                //결제방식
    pay_method : 'card',                                    //결제수단
    merchant_uid : 'merchant_' + new Date().getTime(),
    name : '주문명:결제테스트',                             //order 테이블에 들어갈 주문명 혹은 주문 번호
    amount : 14000,                                         //결제금액
    buyer_email : 'iamport@siot.do',                        //구매자 이메일
    buyer_name : '구매자이름',                               //구매자 이름
    buyer_tel : '010-1234-5678',                            //구매자 번호
    buyer_addr : '서울특별시 강남구 삼성동',                  //구매자 주소
    buyer_postcode : '123-456',                             //구매자 우편번호
    m_redirect_url : 'https://www.naver.com/',    //모바일 결제 완료 후 이동될 주소
    app_scheme : 'iamporttest://bill'                                           //모바일 앱 결제도중 앱 복귀를 위한 url scheme
    }, function(rsp) {
    if ( rsp.success ) {                                    //결제 성공시
    var msg = '결제가 완료되었습니다.';
    msg += '고유ID : ' + rsp.imp_uid;
    msg += '상점 거래ID : ' + rsp.merchant_uid;
    msg += '결제 금액 : ' + rsp.paid_amount;
    msg += '카드 승인번호 : ' + rsp.apply_num;
    } else {                                                //결제 실패시
    var msg = '결제에 실패하였습니다.';
    msg += '에러내용 : ' + rsp.error_msg;
    }

    alert(msg);
    });