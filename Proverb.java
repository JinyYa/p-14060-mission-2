package org.back;

public class Proverb {
    private String content;
    private String author;
    private int id;

    public Proverb(String content, String author, int id) {
        this.id = id;
        this.content = content;
        this.author = author;
    }

    //기존 proverb(명언) 클래스에 json 형식 스트링으로 만드는 메서드 추가.
    public String toJsonString(){
        return "{" + "\n" +
                    "\t\"id\": " + id + "," + "\n" +
                    "\t\"content\": \"" +  escapeJson(content) +"\"," + "\n" +
                    "\t\"author\": \"" + escapeJson(author)  + "\"" + "\n" +
                "}";
    }

    //빌드에서 쓰는 메서드 (탭이 하나 더 있음)
    public String toJsonStringWithTab(){
        return "\t{" + "\n" +
                "\t\t\"id\": " + id + "," + "\n" +
                "\t\t\"content\": \"" + escapeJson(content) +"\"," + "\n" +
                "\t\t\"author\": \"" + escapeJson(author) + "\"" + "\n" +
                "\t}";
    }

    //명언에 "따옴표나 줄바꿈 슬래시 등 사용해도 문제없게
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getId() { return id; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }

}

