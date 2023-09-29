package inflearn.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j // 롬복(Lombok)을 사용하여 로그를 기록하기 위한 어노테이션
@Controller // 스프링 컨트롤러로 선언
@RequestMapping("/servlet/v2") // 이 컨트롤러의 모든 요청은 "/servlet/v2" 경로에서 처리
public class ServletUploadControllerV2 {

    @Value("${file.dir}") // 스프링 프로퍼티 값을 주입받기 위한 어노테이션
    private String fileDir; // "file.dir" 프로퍼티 값을 저장할 변수


    @GetMapping("/upload") // HTTP GET 요청을 처리하는 메서드
    public String newFile() {
        return "upload-form"; // "upload-form"이라는 뷰 이름을 반환하여 해당 뷰를 표시
    }



    @PostMapping("/upload") // HTTP POST 요청을 처리하는 메서드
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}", request); // 로그에 HTTP 요청 정보를 출력

        String itemName = request.getParameter("itemName"); // HTTP 요청에서 "itemName" 매개변수 값을 가져옴
        log.info("itemName={}", itemName); // "itemName" 값을 로그에 출력

        Collection<Part> parts = request.getParts(); // HTTP 요청의 모든 파트(파일 업로드 항목)을 가져옴
        log.info("parts={}", parts); // 파트 목록을 로그에 출력

        for (Part part : parts) { // 파트 목록을 반복
            log.info("==== PART ====");
            log.info("name={}", part.getName()); // 파트의 이름을 로그에 출력

            Collection<String> headerNames = part.getHeaderNames(); // 파트의 헤더 이름 목록을 가져옴

            for (String headerName : headerNames) { // 헤더 이름 목록을 반복
                log.info("header {}: {}", headerName, part.getHeader(headerName)); // 헤더 정보를 로그에 출력
            }

            // content-disposition; filename 헤더를 통해 업로드된 파일 이름을 가져옴
            // part.getSubmittedFileName() : 클라이언트가 전달한 파일명
            log.info("submittedFileName={}", part.getSubmittedFileName());

            // 파트의 바디(body) 크기를 출력
            log.info("size={}", part.getSize());

            // 파트의 입력 스트림을 가져와서 UTF-8 인코딩으로 문자열로 변환
            // part.getInputStream(); : Part의 전송 데이터를 읽음.
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body={}", body);

            // 만약 업로드된 파일 이름이 있는 경우, 파일을 지정된 경로에 저장
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath); // Part를 통해 전송된 데이터를 저장할 수 있게 만듦
            }
        }
        return "upload-form";
    }
}