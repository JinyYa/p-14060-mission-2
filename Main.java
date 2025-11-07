package org.back;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    //폴더 위치는 상수로
    static final String UPLOAD_DIR = System.getProperty("user.dir") + "/db/wiseSaying/";

    static ArrayList<Proverb> proverbs = new ArrayList<>();
    static int lastId = 0;

    // 맨 처음에 파일 저장용 경로가 없으면 폴더를 생성
    private static void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()){
            throw new RuntimeException("에러: Could not create the directory.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        createUploadDirectory();

        System.out.println("== 명언 앱 ==");
        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();
            if (cmd.equals("등록")) {
                addProverb(sc);
            } else if (cmd.equals("목록")) {
                listProverb();
            } else if (cmd.startsWith("삭제?id=")) {
                //삭제?id= 뒤에 있는 문자열을 매개변수로 넘겨줌
                deleteProverb(cmd.substring("삭제?id=".length()).trim());
            } else if (cmd.startsWith("수정?id=")) {
                modifyProverb(sc, cmd.substring("수정?id=".length()).trim());
            } else if (cmd.equals("빌드")) {
                buildJson();
            } else if (cmd.equals("종료")) {
                sc.close();
                saveLastId();
                System.exit(0);
            }
            else {
                System.out.println("존재하지 않는 명령어입니다.");
            }
        }
    }

    //등록
    private static void addProverb(Scanner sc) {
        //입력 받기
        System.out.print("명언 : ");
        String content = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();
        //명언 데이터 생성
        Proverb newProv = new Proverb(content, author, ++lastId);
        proverbs.add(newProv);
        //파일 저장
        try (FileWriter fw = new FileWriter(UPLOAD_DIR + lastId + ".json")) {
            fw.write(newProv.toJsonString());
        } //try-with-resource라 fw.close는 불필요.
        catch (IOException e) {
            throw new RuntimeException("에러: 명언 파일 작성 실패");
        }

        System.out.println(lastId + "번 명언이 등록되었습니다.");
    }

    //수정
    private static void modifyProverb(Scanner sc, String idxStr) {
        // ID 파싱
        int modifyId;
        try{
             modifyId= Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            System.out.println("에러: id에 올바른 숫자를 입력해주세요.");
            return;
        }
        //for loop대신 stream으로 수정할 명언을 찾기
        Proverb toModify = proverbs.stream()
                .filter(p -> p.getId() == modifyId)
                .findFirst()
                .orElse(null);
        if (toModify == null) { //
            System.out.println(modifyId + "번 명언은 존재하지 않습니다.");
            return;
        }
        //입력받기
        System.out.println("명언(기존) :" + toModify.getContent());
        String newContent = sc.nextLine();
        System.out.println("작자(기존) :" + toModify.getAuthor());
        String newAuthor = sc.nextLine();
        //데이터 수정하기
        toModify.setAuthor(newAuthor);
        toModify.setContent(newContent);
        //파일 저장
        try (FileWriter fw = new FileWriter(UPLOAD_DIR + modifyId + ".json")) {
            fw.write(toModify.toJsonString() + "\r\n");
        } //try-with-resource라 fw.close는 불필요.
        catch (IOException e) {
            throw new RuntimeException("에러: 명언 파일 수정 불가");
        }
    }
    //목록
    private static void listProverb(){
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for (int i = proverbs.size()-1; i >= 0 ; i--) {
            System.out.printf("%d / %s / %s\n",
                    proverbs.get(i).getId(), proverbs.get(i).getAuthor(), proverbs.get(i).getContent());
        }
    }

    //삭제
    private static void deleteProverb(String idxStr) {
        // ID 파싱
        int deleteId;
        try {
            deleteId = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            System.out.println("에러: id에 올바른 숫자를 입력해주세요.");
            return;
        }
        //proverbs 라는 ArrayList에서 deleteId와 동일한 Id의를 가진 항목을 찾고, 해당 항목 삭제
        if (!proverbs.removeIf(proverb -> proverb.getId() == deleteId)){
            //삭제가 진행되지 않았을 경우
            System.out.println(deleteId + "번 명언은 존재하지 않습니다.");
            return;
        }
        //파일 저장
        File myFile = new File(UPLOAD_DIR + deleteId  + ".json");
        if (!myFile.delete()) {
            throw new RuntimeException("에러: 명언 파일 삭제 불가.");
        }
        System.out.println(deleteId + "번 명언이 삭제되었습니다.");
    }

    //빌드
    private static void buildJson() {
        //JsonArray 만들기
        StringBuilder jsonArrayStr = new StringBuilder();
        jsonArrayStr.append("[\n");
        if (proverbs.isEmpty()) {
            jsonArrayStr.append("\n]");
        }
        else {
            for (Proverb proverb : proverbs) {
                jsonArrayStr.append(proverb.toJsonStringWithTab()).append(",\n");
            }
            jsonArrayStr.deleteCharAt(jsonArrayStr.length() - 2);
            jsonArrayStr.append("\n]\r\n");
        }
        //파일 저장
        try (FileWriter fw = new FileWriter(UPLOAD_DIR + "data.json")) {
            fw.write(jsonArrayStr.toString());
            System.out.println("data.json 파일의 내용이 갱신되었습니다.");
        }
        catch (IOException e) {
            throw new RuntimeException("에러:data.json 파일의 빌드 실패.");
        }
    }

    //lastId 저장
    private static void saveLastId(){
        try (FileWriter fw = new FileWriter(UPLOAD_DIR + "lastId.txt")) {
            fw.write(lastId + "\n");
        } catch (IOException e) {
            throw new RuntimeException("에러: lastId.txt 파일 생성 실패.");
        }
    }
}