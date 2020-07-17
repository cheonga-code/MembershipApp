package com.example.membershipapp;

public class Users {

    String userId;
    String userToken;
    String userEmail;               //(X) 이메일 변수 필요 없음 -> 아이디가 = 이메일
    String userName;
    int userCartCount;              //(X) 장바구니 갯수 -> 필요x -> why? 장바구니 리스트.size() 로 알면됨
    int userStampCount;             //스탬프 갯수
    boolean membershipUserCheck;    //멤버십 회원 비회원 체크
                                        // -> 회원 중 탈퇴 유무 체크
    boolean membershipCardcheck;    //멤버십 카드 사용 유무 체크
    int userCardPrice;              //카드 사용시 -> 카드 잔액

    //사용자 사용가능한 쿠폰 리스트
    //사용자 쿠폰 사용내역 리스트
    //사용자 주문 리스트
    //사용자가 쓴 리뷰 리스트


    public Users() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserCartCount() {
        return userCartCount;
    }

    public void setUserCartCount(int userCartCount) {
        this.userCartCount = userCartCount;
    }

    public int getUserStampCount() {
        return userStampCount;
    }

    public void setUserStampCount(int userStampCount) {
        this.userStampCount = userStampCount;
    }

    public boolean isMembershipUserCheck() {
        return membershipUserCheck;
    }

    public void setMembershipUserCheck(boolean membershipUserCheck) {
        this.membershipUserCheck = membershipUserCheck;
    }

    public boolean isMembershipCardcheck() {
        return membershipCardcheck;
    }

    public void setMembershipCardcheck(boolean membershipCardcheck) {
        this.membershipCardcheck = membershipCardcheck;
    }

    public int getUserCardPrice() {
        return userCardPrice;
    }

    public void setUserCardPrice(int userCardPrice) {
        this.userCardPrice = userCardPrice;
    }
}
