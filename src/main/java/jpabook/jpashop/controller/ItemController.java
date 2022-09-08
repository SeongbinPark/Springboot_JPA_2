package jpabook.jpashop.controller;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/items/new")//form 호출
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping(value = "/items/new")//form에 입력된 데이터 저장.
    public String create(BookForm form) {
        Book book = new Book();

        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    //상품 목록
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }


    //상품update
    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model) {//폼 먼저 호출, 그 뒤에 POST로 수정
        Book item = (Book) itemService.findOne(itemId);
        //원래 Item 종류가 3개 였다. -> 각각 서비스를 만들자.Itemservice->BookService, FoodService, MovieService

        BookForm form = new BookForm();//업데이트하는데 Book 엔티티 대신 BookForm을 보낼 거다.
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);//BookForm을 넘겨서 update
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")//실제 값 변경
    public String updateItem(@ModelAttribute BookForm form) {//Pathvariable인 itemId를 안쓸거라 안받아도됨.
        //updateItemForm.html에서 POST로 BookForm 객체가넘어옴.(이름 : form)
        //https://www.inflearn.com/questions/70393 헷갈리면 보세요.
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);//내가 new한 Book객체라서 persist해줘야함.

        //귀찮더라도 itemService.saveItem() ( merge하는 방법 ) 대신
        //
        //변경 감지(dirty checking) 쓰는 방법을 하자. ( 일일히 setter )
        //내가 업데이트하고 싶은 필드만 set 하자
        itemService.updateItem(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";//상품목록으로 
    }
}
