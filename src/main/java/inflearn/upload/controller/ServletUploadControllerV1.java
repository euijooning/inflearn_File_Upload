package inflearn.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadControllerV1 {

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}", request); // 로깅을 통해 HTTP 요청 객체(request)를 출력한다.

        String itemName = request.getParameter("itemName"); // 요청 파라미터 중 "itemName" 값을 가져온다.
        log.info("itemName={}", itemName); // 로깅을 통해 itemName 값을 출력한다.

        Collection<Part> parts = request.getParts(); // HTTP 멀티파트 요청의 모든 파트를 가져온다. 이게 앞선 그림에서 괄호로 묶었던 3개
        log.info("parts={}", parts); // 로깅을 통해 파트 목록을 출력한다.

        return "upload-form";
    }
}
