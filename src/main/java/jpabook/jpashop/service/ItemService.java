package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Transactional(readOnly = true)//일단 true로 깔아둠. 값 변경로직 시 false로 각각 설정
@RequiredArgsConstructor
public class ItemService {

    private ItemRepository itemRepository;

    @Transactional//readOnly false로 하기 위해 ( 어노테이션 가까운게 우선권 ( 오버라이드))
    public void saveItem(Item item) {
        itemRepository.save(item);
    }


    //여긴 readOnly true 가 먹힌다.
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    //여긴 readOnly true 가 먹힌다.
    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }
}
