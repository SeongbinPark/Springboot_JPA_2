package jpabook.jpashop.repository.order.simplequery;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    /**
     * 3-4. 간단주문조회. JPA에서 DTO로 바로 조회
     * JPA에서 DTO 바로 뽑는 로직이 API에 의존적이어서 OrderRepository는 순수한 엔티티용도로 유지하고자 따로 빼놈
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                //이 부분 jpql 이 화면에 종속적인 코드.
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name,o.orderDate,o.status,d.address)" +
                                "from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }


}
