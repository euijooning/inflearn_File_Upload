package inflearn.upload.controller;


import inflearn.upload.domain.Item;
import inflearn.upload.domain.ItemRepository;
import inflearn.upload.domain.UploadFile;

import inflearn.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j // Lombok을 사용하여 로깅을 위한 Logger를 자동으로 생성
@Controller // 스프링 MVC 컨트롤러로 설정
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class ItemController {

    private final ItemRepository itemRepository; // Item 객체를 저장하고 검색하기 위한 리포지토리
    private final FileStore fileStore; // 파일 저장을 위한 FileStore 서비스

    // 상품 등록 페이지로 이동하는 GET 요청을 처리하는 메서드
    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form"; // "item-form" 뷰 페이지를 반환
    }

    // 상품을 저장하는 POST 요청을 처리하는 메서드
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form,
                           RedirectAttributes redirectAttributes) throws IOException {

        // 업로드된 첨부 파일과 이미지 파일들을 저장하고, 결과를 얻어옴
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles =
                fileStore.storeFiles(form.getImageFiles());

        // 데이터베이스에 상품 정보 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);

        itemRepository.save(item); // 상품 정보를 데이터베이스에 저장

        // 리다이렉트를 통해 상품 상세 페이지로 이동
        redirectAttributes.addAttribute("itemId", item.getId());
        return "redirect:/items/{itemId}";
    }

    // 상품 상세 페이지로 이동하는 GET 요청을 처리하는 메서드
    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id); // 아이템을 데이터베이스에서 검색
        model.addAttribute("item", item); // 모델에 상품 정보를 추가하여 뷰에 전달
        return "item-view"; // "item-view" 뷰 페이지를 반환
    }

    // 이미지 다운로드를 처리하는 메서드
    @ResponseBody // 반환되는 객체를 HTTP 응답 바디에 직접 넣음
    @GetMapping("/images/{filename}") // "/images/{filename}" 경로로 GET 요청을 처리
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // 파일 이름을 기반으로 UrlResource 객체를 생성하여 이미지 파일 다운로드를 처리
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    // 첨부 파일 다운로드를 처리하는 메서드
    @GetMapping("/attach/{itemId}") // "/attach/{itemId}" 경로로 GET 요청을 처리
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId)
            throws MalformedURLException {

        Item item = itemRepository.findById(itemId); // 데이터베이스에서 해당 itemId에 해당하는 아이템 정보를 검색

        String storeFileName = item.getAttachFile().getStoreFileName(); // 저장된 파일명
        String uploadFileName = item.getAttachFile().getUploadFileName(); // 업로드된 파일명

        UrlResource resource = new UrlResource("file:" +
                fileStore.getFullPath(storeFileName)); // 첨부 파일의 저장 경로를 기반으로 UrlResource 객체 생성

        // 다운로드할 파일명을 인코딩하여 Content-Disposition 헤더에 설정
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8); // UTF-8로 인코딩하여 파일명 깨짐 방지
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\""; // 다운로드 시 파일명 지정

        // ResponseEntity를 사용하여 응답 헤더를 설정하고, UrlResource를 응답 본문에 설정하여 다운로드 응답 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}