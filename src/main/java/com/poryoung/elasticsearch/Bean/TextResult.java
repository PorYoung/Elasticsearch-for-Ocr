package com.poryoung.elasticsearch.Bean;

/**
 * @author Administrator
 * @Title: TextResult
 * @ProjectName movie-elasticsearch
 * @Description: TODO
 * @date 2019/7/1121:25
 */
public class TextResult {
    private int charNum;

    public TextResult() {
    }

    @Override
    public String toString() {
        return "TextResult{" +
                "charNum=" + charNum +
                ", isHandwritten=" + isHandwritten +
                ", leftBottom='" + leftBottom + '\'' +
                ", leftTop='" + leftTop + '\'' +
                ", rightBottom='" + rightBottom + '\'' +
                ", rightTop='" + rightTop + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public int getCharNum() {
        return charNum;
    }

    public void setCharNum(int charNum) {
        this.charNum = charNum;
    }

    public boolean isHandwritten() {
        return isHandwritten;
    }

    public void setHandwritten(boolean handwritten) {
        isHandwritten = handwritten;
    }

    public String getLeftBottom() {
        return leftBottom;
    }

    public void setLeftBottom(String leftBottom) {
        this.leftBottom = leftBottom;
    }

    public String getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(String leftTop) {
        this.leftTop = leftTop;
    }

    public String getRightBottom() {
        return rightBottom;
    }

    public void setRightBottom(String rightBottom) {
        this.rightBottom = rightBottom;
    }

    public String getRightTop() {
        return rightTop;
    }

    public void setRightTop(String rightTop) {
        this.rightTop = rightTop;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextResult(int charNum, boolean isHandwritten, String leftBottom, String leftTop, String rightBottom, String rightTop, String text) {
        this.charNum = charNum;
        this.isHandwritten = isHandwritten;
        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
        this.rightTop = rightTop;
        this.text = text;
    }

    private boolean isHandwritten;
    private String leftBottom;
    private String leftTop;
    private String rightBottom;
    private String rightTop;
    private String text;

}
