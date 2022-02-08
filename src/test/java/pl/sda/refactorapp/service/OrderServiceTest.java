package pl.sda.refactorapp.service;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sda.refactorapp.dao.DiscountCouponsDao;
import pl.sda.refactorapp.dao.OrderDao;
import pl.sda.refactorapp.entity.Customer;
import pl.sda.refactorapp.entity.DiscountCoupon;
import pl.sda.refactorapp.entity.Item;
import pl.sda.refactorapp.entity.Order;
import pl.sda.refactorapp.service.exception.CustomerNotExistsException;
import pl.sda.refactorapp.service.exception.InvalidOrderItemsException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private CustomerService customerService;

    @Mock
    private DiscountCouponsDao couponsDao;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldThrowCustomerNotExistException() {
        // given
        final var item = new Item();
        item.setPrice(new BigDecimal("24.00"));
        item.setQuantity(1);
        item.setWeight(0.2f);
        final var form = new MakeOrderForm(randomUUID(), List.of(item), "ABC200");

        // when & then
        assertThrows(CustomerNotExistsException.class, () -> orderService.makeNewOrder(form));
    }

    @Test
    void shouldThrowOrderItemsInvalidException() {
        // given
        final var customerId = randomUUID();
        final var form = new MakeOrderForm(customerId, List.of(), "ABC200");

        // when & then
        assertThrows(InvalidOrderItemsException.class, () -> orderService.makeNewOrder(form));
    }

    @Test
    void shouldMakeAnOrderWithDiscountCoupon() throws Exception {
        // given
        final var customerId = randomUUID();
        final var customer = new Customer();
        customer.setEmail("email@email.com");
        given(customerService.findById(customerId)).willReturn(Optional.of(customer));

        final var item = new Item();
        item.setWeight(1.f);
        item.setQuantity(1);
        item.setPrice(BigDecimal.ONE);
        final var items = List.of(item);

        final var couponCode = "ABC200";
        final var discountCoupon = new DiscountCoupon();
        discountCoupon.setCoupon(couponCode);
        discountCoupon.setValue(0.2f);
        given(couponsDao.findByCode(couponCode)).willReturn(Optional.of(discountCoupon));

        final var mailServiceMockedStatic = mockStatic(MailService.class);
        mailServiceMockedStatic.when(() -> MailService.sendEmail(any(), any(), any())).thenReturn(true);

        // when
        final var result = orderService.makeOrder(new MakeOrderForm(customerId, items, couponCode));

        // then
        verify(couponsDao).save(discountCoupon);
        assertTrue(discountCoupon.isUsed());
        final var orderCapture = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(orderCapture.capture());
        final var order = orderCapture.getValue();
        assertEquals(customerId, order.getCid());
        assertEquals(Order.ORDER_STATUS_WAITING, order.getStatus());
        assertEquals(0.2f, order.getDiscount());
        assertEquals(new BigDecimal("35"), order.getDeliveryCost());
        assertTrue(result);

        mailServiceMockedStatic.close();
    }
}