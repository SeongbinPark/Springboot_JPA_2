package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


// 무식한 방법으로 만들겠다.

/**
 * 총 주문 2개 , 주문상품 4개
 * userA
 * -JPA1 BOOK
 * -JPA2 BOOK
 * userB
 * -SPRING1 BOOK
 * -SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;


    @PostConstruct//스프링 빈이 다 올라오고 나면 스프링이 호출해주는 메서드.
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
        // 이 메서드 내용을 여기 다 넣어도 될 것같지만 빈 라이프사이클이 있어서
        //트랜젝션 먹이는게 안됨 -> 별도로 빈 등록 해야함.
        //이제 스프링 올라갈 때 마다 조회용 데이터 올라가므로 application.yml에서 ddl-auto 다시 create로 바꾸자!
        // create 는 애플리케이션 실행 시점에 DROP 후 다시 CREATE
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em; // 샘플데이터니까 그냥 em으로 바로 넣어주자.

        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...문법으로 orderItem두개 다 넘김 ( 내부적으로 배열 )
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, book1.getPrice(), 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, book2.getPrice(), 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...문법으로 orderItem두개 다 넘김 ( 내부적으로 배열 )
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);//ctrl + alt + P : 파라미터로 뽑기
            return book1;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }


    }

}


