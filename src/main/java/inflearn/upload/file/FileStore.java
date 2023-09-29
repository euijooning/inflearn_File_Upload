package inflearn.upload.file;

import inflearn.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}") // 스프링의 프로퍼티에서 값을 읽어와서 변수에 주입.
    private String fileDir; // 파일을 저장할 디렉토리 경로를 저장하는 변수.

    // 파일의 전체 경로를 반환하는 메서드
    public String getFullPath(String fileName) {
        return fileDir + "/" + fileName; // 추가한 부분 있음.
    }

    // 여러 개의 MultipartFile을 저장하고 그 결과를 리스트로 반환하는 메서드
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles)
            throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>(); // 파일 저장할 ArrayList

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) { // 파일이 비어있지 않으면
                storeFileResult.add(storeFile(multipartFile)); // 저장
            }
        }
        return storeFileResult; // 저장한 것을 반환
    }


    // 단일 MultipartFile을 저장하고 업로드된 파일 정보를 반환하는 메서드
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        // 업로드된 파일의 원래 이름과 저장할 파일 이름 생성
        // 원래 파일명 (image.png)
        String originalFilename = multipartFile.getOriginalFilename();
        // 서버에 저장할 파일명(uuid이름.확장자) 형태
        String storeFileName = createStoreFileName(originalFilename);

        // 파일을 실제로 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        // 업로드된 파일 정보를 담은 UploadFile 객체 반환
        return new UploadFile(originalFilename, storeFileName);
    }


    // 저장할 파일 이름을 생성하는 메서드
    // 랜덤 UUID와 업로드된 파일의 확장자를 조합하여 반환한다.
    private String createStoreFileName(String originalFilename) {
        // 파일 확장자 추출
        String ext = extractExt(originalFilename);
        // 랜덤 UUID 생성
        String uuid = UUID.randomUUID().toString();
        // UUID와 확장자를 조합하여 저장 파일 이름 생성
        return uuid + "." + ext;
    }


    // 파일 이름에서 확장자를 추출하는 메서드
    // 파일 이름에서 마지막 점('.') 이후의 문자열을 확장자로 반환한다.
    private String extractExt(String originalFilename) {
        // 파일 이름에서 마지막 점('.') 이후의 문자열을 추출하여 확장자 반환
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}