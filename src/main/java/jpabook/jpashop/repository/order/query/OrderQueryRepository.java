package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractAuditable_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * JPA에서 DTO 바로 뽑는 로직이 API에 의존적이어서 OrderRepository는 순수한 엔티티용도로 유지하고자 따로 빼놈
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * Collections -> Iterable
     * Iterable의 method forEach( default void forEach(Consumer<? super T> action))
     * default implementation :
     * for (T t : this)
     * action.accept(t);
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //query 1번 -> N개

        //OrderQueryDto에 List<OrderItemQueryDto> orderItems 을 세팅.
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //query N번
            o.setOrderItems(orderItems);
        }); //이때 result값은 안바뀜.
        //Consumer 이므로 반환값없이 result의 실제값이 바뀐다.
        return result;
    }

    //V5
    public List<OrderQueryDto> findAllByDtoOptimizatmion() {
        //주문을 모두 가져온 후
        List<OrderQueryDto> result = findOrders();

        //orderId만 추출 (두 개)
        List<Long> orderIds = toOrderIds(result);

       //바로 Map으로 뽑는 것 까지 메서드에 포함시킴(메모리에 올림.)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        //result의 orderItems 설정 (이게 V5의 핵심)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" + //그냥 join
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                //orderId 두 개를 = 이 아닌 in으로 넣는다. -> orderItems를 쿼리 한번으로 다 가져옴.
                .setParameter("orderIds", orderIds)
                .getResultList();

        //orderId 기준으로 orderItems를 map으로 바꿈.
        //key: o.getOrderId(), values: 원래 OrderItems타입 (=List<OrderItemQueryDto>)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderId()));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(orderQueryDto -> orderQueryDto.getOrderId())
                .collect(Collectors.toList());
    }


    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        //em.createQuery 에서 트랜젝션 열고 닫힘.
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" + //그냥 join
                                " where oi.order.id= :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name,o.orderDate,o.status,d.address)" +
                                " from Order o" +
                                " join o.member m" + //그냥 join
                                " join o.delivery d", OrderQueryDto.class) //그냥 join
                .getResultList();
    }


}
