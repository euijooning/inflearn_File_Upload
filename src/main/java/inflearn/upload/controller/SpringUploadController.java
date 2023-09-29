package inflearn.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring") // 이 컨트롤러의 요청 매핑(prefix)을 "/spring"으로 지정합니다.
public class SpringUploadController {

    @Value("${file.dir}") // 스프링의 프로퍼티에서 값을 읽어와서 변수에 주입.
    private String fileDir; // 파일을 저장할 디렉토리 경로를 저장하는 변수.

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload") // HTTP POST 메서드에 대한 "/spring/upload" 엔드포인트를 처리하는 메서드.
    public String saveFile(@RequestParam String itemName, // 요청 파라미터 "itemName"을 받아오는 매개변수.
                           @RequestParam MultipartFile file, // 파일 업로드를 위한 매개변수로, MultipartFile 타입을 사용.
                           HttpServletRequest request) throws IOException {

        log.info("request={}", request); // 로그를 통해 HTTP 요청(request) 정보를 출력.
        log.info("itemName={}", itemName); // 로그를 통해 "itemName" 파라미터 값을 출력.
        log.info("multipartFile={}", file); // 로그를 통해 업로드된 파일 정보를 출력.

        if (!file.isEmpty()) { // 업로드된 파일이 비어있지 않은 경우
            String fullPath = fileDir + file.getOriginalFilename(); // 파일을 저장할 전체 경로를 생성한다.
            log.info("파일 저장 fullPath={}", fullPath); // 파일 저장 경로를 로그로 출력.
            file.transferTo(new File(fullPath)); // 업로드된 파일을 해당 경로에 저장한다.
        }
        return "upload-form"; // "upload-form"이라는 View 이름을 반환하여 업로드 화면을 다시 표시.
    }
}