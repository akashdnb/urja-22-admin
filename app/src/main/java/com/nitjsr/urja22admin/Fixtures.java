package com.nitjsr.urja22admin;

public class Fixtures {
    public int type;//CRICKET-1 FOOTBALL-2 BASKETBALL-3 VOLLEYBALL-4 BADMINTON-5 CHESS-6 HOCKEY-7 TT-8 ATHLETICS-9(+1)
    String team1, team2, roundName, result;
    String score1, score2;
    String s11, s12, s21, s22, s31, s32, s41, s42, s51, s52;
    boolean live, completed;
    String uid;
    String matchDate;

    public Fixtures() { }

    public Fixtures(String uid, int type, String team1, String team2, String roundName, String result, String score1, String score2, boolean live,boolean completed, String matchDate) {
        this.uid=uid;
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
        this.roundName = roundName;
        this.result = result;
        this.score1 = score1;
        this.score2 = score2;
        this.live = live;
        this.completed=completed;
        this.matchDate=matchDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public String getS11() {
        return s11;
    }

    public void setS11(String s11) {
        this.s11 = s11;
    }

    public String getS12() {
        return s12;
    }

    public void setS12(String s12) {
        this.s12 = s12;
    }

    public String getS21() {
        return s21;
    }

    public void setS21(String s21) {
        this.s21 = s21;
    }

    public String getS22() {
        return s22;
    }

    public void setS22(String s22) {
        this.s22 = s22;
    }

    public String getS31() {
        return s31;
    }

    public void setS31(String s31) {
        this.s31 = s31;
    }

    public String getS32() {
        return s32;
    }

    public void setS32(String s32) {
        this.s32 = s32;
    }

    public String getS41() {
        return s41;
    }

    public void setS41(String s41) {
        this.s41 = s41;
    }

    public String getS42() {
        return s42;
    }

    public void setS42(String s42) {
        this.s42 = s42;
    }

    public String getS51() {
        return s51;
    }

    public void setS51(String s51) {
        this.s51 = s51;
    }

    public String getS52() {
        return s52;
    }

    public void setS52(String s52) {
        this.s52 = s52;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }
}
