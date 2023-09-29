package inflearn.upload.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository { // 이거 역시 예제이므로 클래스

    private final Map<Long, Item> store = new HashMap<>(); // 담을 HashMap
    private Long sequence = 0L; // 아이디

    public Item save(Item item) { // 아이템 저장 메서드
        item.setId(++sequence); // 하나씩 증가시킴
        store.put(item.getId(), item); // 아이템 담기
        return item;
    }

    public Item findById(Long id) { // id찾는 메서드
        return store.get(id);
    }
}
